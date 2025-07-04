package dev.flur.ranks.command;

import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.util.Objects;
import java.util.Set;

public class AnnotationCommandManager {

    private final JavaPlugin plugin;

    public AnnotationCommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
        registerAnnotatedCommands();
    }

    private void registerAnnotatedCommands() {
        Reflections reflections = new Reflections("dev.flur.ranks.command.commands");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(CommandInfo.class);

        for (Class<?> clazz : annotatedClasses) {
            try {
                CommandInfo info = clazz.getAnnotation(CommandInfo.class);
                BaseCommand command = (BaseCommand) clazz.getDeclaredConstructor().newInstance();

                Objects.requireNonNull(plugin.getCommand(info.name())).setExecutor(command);
                Objects.requireNonNull(plugin.getCommand(info.name())).setTabCompleter(command);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to register command: " + clazz.getSimpleName());
            }
        }
    }
}