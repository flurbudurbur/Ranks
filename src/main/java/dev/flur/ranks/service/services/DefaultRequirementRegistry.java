package dev.flur.ranks.service.services;

import dev.flur.ranks.requirement.AnnotatedRequirement;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.service.RequirementDiscovery;
import dev.flur.ranks.service.RequirementLookup;
import dev.flur.ranks.requirement.annotations.RequirementAnnotation;
import dev.flur.ranks.requirement.records.RequirementRecord;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.HashMap;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

/**
 * Default implementation of the RequirementDiscovery and RequirementLookup interfaces.
 * <p>
 * This class provides a static API for backward compatibility with RequirementRegistry.
 * </p>
 */
public class DefaultRequirementRegistry implements RequirementDiscovery, RequirementLookup {

    private final Map<String, RequirementRecord> nameRegistry = new ConcurrentHashMap<>();
    private final Map<Class<? extends Requirement>, RequirementRecord> classRegistry = new ConcurrentHashMap<>();
    private final Logger logger;

    /**
     * Creates a new DefaultRequirementRegistry.
     *
     * @param logger The logger to use
     */
    public DefaultRequirementRegistry(@NotNull Logger logger) {
        this.logger = logger;
    }

    /**
     * Determines the requirement name from the class.
     */
    private static String getRequirementName(@NotNull Class<? extends AnnotatedRequirement> clazz) {
        return clazz.getAnnotation(RequirementAnnotation.class).name();
    }

    /**
     * Creates a constructor function for the requirement class.
     */
    @Contract(pure = true)
    private static @NotNull Function<String[], Requirement> createConstructor(Class<? extends AnnotatedRequirement> clazz) {
        return params -> {
            try {
                Constructor<? extends AnnotatedRequirement> constructor =
                        clazz.getConstructor(String[].class);
                return constructor.newInstance((Object) params);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof IllegalArgumentException) {
                    throw (IllegalArgumentException) e.getCause();
                }
                throw new RuntimeException("Failed to create requirement instance: " + clazz.getName(), e);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Failed to create requirement instance: " + clazz.getName(), e);
            }
        };
    }

    @Override
    public void registerRequirement(@NotNull Class<? extends Requirement> requirementClass) {
        if (!(AnnotatedRequirement.class.isAssignableFrom(requirementClass))) {
            logger.warning("Requirement class " + requirementClass.getName() + " does not extend AnnotatedRequirement");
            return;
        }

        @SuppressWarnings("unchecked")
        Class<? extends AnnotatedRequirement> annotatedClass = (Class<? extends AnnotatedRequirement>) requirementClass;

        String name = getRequirementName(annotatedClass);
        Function<String[], dev.flur.ranks.requirement.Requirement> constructor = createConstructor(annotatedClass);

        RequirementRecord info = new RequirementRecord(name, constructor, requirementClass);
        nameRegistry.put(name, info);
        classRegistry.put(requirementClass, info);

        logger.info("Registered requirement: " + name + " (" + requirementClass.getSimpleName() + ")");
    }

    @Override
    public int discoverRequirements(@NotNull String packageName) {
        try {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackages(packageName)
                    .setScanners(SubTypes, TypesAnnotated));

            Set<Class<? extends AnnotatedRequirement>> requirementClasses =
                    reflections.getSubTypesOf(AnnotatedRequirement.class);

            int count = 0;
            for (Class<? extends AnnotatedRequirement> clazz : requirementClasses) {
                try {
                    registerRequirement(clazz);
                    count++;
                } catch (Exception e) {
                    logger.severe("Failed to register requirement class: " + clazz.getName() + " - " + e.getMessage());
                }
            }

            logger.info("Discovered " + count + " requirement types in package " + packageName);
            return count;
        } catch (Exception e) {
            logger.severe("Failed to discover requirements in package " + packageName + ": " + e.getMessage());
            return 0;
        }
    }

    @Override
    @NotNull
    public List<Class<? extends Requirement>> getRegisteredRequirementClasses() {
        return new ArrayList<>(classRegistry.keySet());
    }

    @Override
    @NotNull
    public List<String> getRegisteredRequirementNames() {
        return new ArrayList<>(nameRegistry.keySet());
    }

    @Override
    public @NotNull Map<String, dev.flur.ranks.requirement.records.RequirementRecord> getRequirementInfo() {
        return Collections.unmodifiableMap(nameRegistry);
    }

    @NotNull
    public Map<String, RequirementRecord> getRequirementRecord() {
        return Collections.unmodifiableMap(nameRegistry);
    }

    @Override
    @Nullable
    public Class<? extends Requirement> getRequirementClass(@NotNull String name) {
        RequirementRecord info = nameRegistry.get(name);
        return info != null ? info.requirementClass() : null;
    }

    @Override
    @Nullable
    public Requirement createRequirement(@NotNull String name, @NotNull Map<String, String> params) {
        RequirementRecord info = nameRegistry.get(name);
        if (info == null) {
            return null;
        }

        try {
            // Extract the amount parameter if it exists
            String amount = params.get("amount");

            // Create a new map without the amount parameter
            Map<String, String> otherParams = new HashMap<>(params);
            if (amount != null) {
                otherParams.remove("amount");
            }

            // Create an array with the parameters, ensuring amount is last if present
            String[] paramsArray;
            if (amount != null) {
                paramsArray = new String[otherParams.size() * 2 + 1];
                int i = 0;
                for (Map.Entry<String, String> entry : otherParams.entrySet()) {
                    paramsArray[i++] = entry.getKey();
                    paramsArray[i++] = entry.getValue();
                }
                paramsArray[paramsArray.length - 1] = amount;
            } else {
                paramsArray = new String[params.size() * 2];
                int i = 0;
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    paramsArray[i++] = entry.getKey();
                    paramsArray[i++] = entry.getValue();
                }
            }

            return info.constructor().apply(paramsArray);
        } catch (Exception e) {
            logger.severe("Failed to create requirement " + name + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    @Nullable
    public dev.flur.ranks.requirement.Requirement createRequirement(@NotNull String name, @NotNull String params) {
        RequirementRecord info = nameRegistry.get(name);
        if (info == null) {
            return null;
        }

        try {
            String[] paramsArray = params.split(",");
            return info.constructor().apply(paramsArray);
        } catch (Exception e) {
            logger.severe("Failed to create requirement " + name + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    public int getMinParams(@NotNull String name) {
        RequirementRecord info = nameRegistry.get(name);
        if (info == null) {
            return -1;
        }

        Class<? extends dev.flur.ranks.requirement.Requirement> clazz = info.requirementClass();
        RequirementAnnotation paramsAnnotation = clazz.getAnnotation(RequirementAnnotation.class);
        return paramsAnnotation != null ? paramsAnnotation.minimum() : 0;
    }

    @Override
    public int getMaxParams(@NotNull String name) {
        RequirementRecord info = nameRegistry.get(name);
        if (info == null) {
            return -1;
        }

        Class<? extends dev.flur.ranks.requirement.Requirement> clazz = info.requirementClass();
        RequirementAnnotation paramsAnnotation = clazz.getAnnotation(RequirementAnnotation.class);
        return paramsAnnotation != null ? paramsAnnotation.maximum() : Integer.MAX_VALUE;
    }

    @Override
    @NotNull
    public List<String> getParamNames(@NotNull String name) {
        RequirementRecord info = nameRegistry.get(name);
        if (info == null) {
            return Collections.emptyList();
        }

        Class<? extends dev.flur.ranks.requirement.Requirement> clazz = info.requirementClass();
        RequirementAnnotation paramsAnnotation = clazz.getAnnotation(RequirementAnnotation.class);
        // Since RequirementAnnotation doesn't have a names() method, return a list with just the name
        return paramsAnnotation != null ? Collections.singletonList(paramsAnnotation.name()) : Collections.emptyList();
    }

    @Override
    public boolean hasRequirement(@NotNull String name) {
        return nameRegistry.containsKey(name);
    }

    @Nullable
    public RequirementRecord fromName(@NotNull String name) {
        return nameRegistry.get(name);
    }

    @Nullable
    public RequirementRecord fromClass(@NotNull Class<? extends dev.flur.ranks.requirement.Requirement> clazz) {
        return classRegistry.get(clazz);
    }

    @NotNull
    public Set<String> getRegisteredNames() {
        return Collections.unmodifiableSet(nameRegistry.keySet());
    }

    @NotNull
    public Set<Class<? extends dev.flur.ranks.requirement.Requirement>> getRegisteredClasses() {
        return Collections.unmodifiableSet(classRegistry.keySet());
    }

    @NotNull
    public Collection<RequirementRecord> getAllRequirements() {
        return Collections.unmodifiableCollection(nameRegistry.values());
    }
}
