package dev.flur.ranks.command;

import dev.flur.commands.CommandInfo;
import dev.flur.ranks.Ranks;
import dev.flur.ranks.service.ServiceContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public class CommandManager {

    private final Ranks plugin;
    private final ServiceContainer serviceContainer;
    private final Logger logger;

    public CommandManager(@NotNull Ranks plugin, @NotNull ServiceContainer serviceContainer) {
        this.plugin = plugin;
        this.serviceContainer = serviceContainer;
        this.logger = plugin.getLogger();
        registerCommands();
    }

    private void registerCommands() {
        // Use reflection to find all classes annotated with @CommandInfo
        Reflections reflections = new Reflections("dev.flur.ranks.command.commands");
        Set<Class<?>> commandClasses = reflections.getTypesAnnotatedWith(CommandInfo.class);

        for (Class<?> commandClass : commandClasses) {
            try {
                CommandInfo commandInfo = commandClass.getAnnotation(CommandInfo.class);
                if (commandInfo != null) {
                    BaseCommand command = createCommandInstance(commandClass);
                    if (command != null) {
                        registerCommand(commandInfo.name(), command);
                        logger.info("Registered command: " + commandInfo.name() + " (" + commandClass.getSimpleName() + ")");
                    }
                }
            } catch (Exception e) {
                logger.warning("Failed to register command from class: " + commandClass.getSimpleName());
                logger.warning(e.getMessage());
            }
        }
    }

    private @Nullable BaseCommand createCommandInstance(Class<?> commandClass) {
        try {
            Constructor<?>[] constructors = commandClass.getConstructors();

            for (Constructor<?> constructor : constructors) {
                Parameter[] parameters = constructor.getParameters();
                Object[] args = new Object[parameters.length];

                boolean canInstantiate = true;
                for (int i = 0; i < parameters.length; i++) {
                    Object dependency = resolveDependency(parameters[i].getType());
                    if (dependency == null) {
                        canInstantiate = false;
                        break;
                    }
                    args[i] = dependency;
                }

                if (canInstantiate) {
                    return (BaseCommand) constructor.newInstance(args);
                }
            }

            logger.warning("Could not resolve dependencies for command: " + commandClass.getSimpleName());
            return null;

        } catch (Exception e) {
            logger.warning("Failed to create instance of command: " + commandClass.getSimpleName());
            logger.warning(e.getMessage());
            return null;
        }
    }

    private @Nullable Object resolveDependency(Class<?> type) {
        // Resolve dependencies based on type
        if (type == Ranks.class) {
            return plugin;
        } else if (type == ServiceContainer.class) {
            return serviceContainer;
        } else if (type == Logger.class) {
            return logger;
        } else if (type.getSimpleName().endsWith("Service")) {
            // Handle service dependencies
            return resolveService(type);
        } else if (type.getSimpleName().endsWith("Validator") ||
                type.getSimpleName().endsWith("Processor") ||
                type.getSimpleName().endsWith("Notifier")) {
            // Handle other service container dependencies
            return resolveServiceContainerDependency(type);
        }

        return null;
    }

    private @Nullable Object resolveService(Class<?> serviceType) {
        try {
            // Use reflection to call the appropriate getter method on ServiceContainer
            String serviceName = serviceType.getSimpleName();
            String methodName = "get" + serviceName;

            return serviceContainer.getClass().getMethod(methodName).invoke(serviceContainer);
        } catch (Exception e) {
            logger.warning("Failed to resolve service: " + serviceType.getSimpleName());
            return null;
        }
    }

    private @Nullable Object resolveServiceContainerDependency(Class<?> type) {
        try {
            String typeName = type.getSimpleName();
            String methodName = "get" + typeName;

            return serviceContainer.getClass().getMethod(methodName).invoke(serviceContainer);
        } catch (Exception e) {
            logger.warning("Failed to resolve service container dependency: " + type.getSimpleName());
            return null;
        }
    }

    private void registerCommand(String name, BaseCommand command) {
        try {
            Objects.requireNonNull(plugin.getCommand(name)).setExecutor(command);
            Objects.requireNonNull(plugin.getCommand(name)).setTabCompleter(command);
        } catch (Exception e) {
            logger.warning("Failed to register command: " + name);
            logger.warning(e.getMessage());
        }
    }
}