package dev.flur.ranks.requirement;

import dev.flur.ranks.Ranks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

/**
 * Registry for managing requirement types and their automatic discovery.
 * <p>
 * This class replaces the hardcoded RequirementType enum with a dynamic registry
 * that automatically discovers and registers all classes extending {@link AnnotatedRequirement}.
 * </p>
 *
 * <h3>Features:</h3>
 * <ul>
 *   <li>Automatic discovery of requirement classes using reflection</li>
 *   <li>Support for {@link RequirementName} annotation for custom names</li>
 *   <li>Fallback to class name derivation if no annotation is present</li>
 *   <li>Thread-safe registration and lookup</li>
 * </ul>
 *
 * @see AnnotatedRequirement
 * @see RequirementName
 * @see RequirementParams
 * @since 1.0
 */
public class RequirementRegistry {

    private static final Map<String, RequirementInfo> REGISTRY = new ConcurrentHashMap<>();
    private static final Map<Class<? extends Requirement>, RequirementInfo> CLASS_REGISTRY = new ConcurrentHashMap<>();

    static {
        registerAnnotatedRequirements();
    }

    /**
     * Automatically discovers and registers all requirement classes.
     */
    private static void registerAnnotatedRequirements() {
        try {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackages("dev.flur.ranks.requirement.requirements")
                    .setScanners(SubTypes, TypesAnnotated));

            Set<Class<? extends AnnotatedRequirement>> requirementClasses =
                    reflections.getSubTypesOf(AnnotatedRequirement.class);

            for (Class<? extends AnnotatedRequirement> clazz : requirementClasses) {
                try {
                    registerRequirement(clazz);
                } catch (Exception e) {
                    System.err.println("Failed to register requirement class: " + clazz.getName());
                    Ranks.getPlugin().getLogger().severe("Failed to register requirement class: " + clazz.getName());
                }
            }

            System.out.println("Registered " + REGISTRY.size() + " requirement types: " +
                    String.join(", ", REGISTRY.keySet()));

        } catch (Exception e) {
            System.err.println("Failed to initialize requirement registry");
            Ranks.getPlugin().getLogger().severe("Failed to initialize requirement registry");
        }
    }

    /**
     * Registers a single requirement class.
     */
    private static void registerRequirement(Class<? extends AnnotatedRequirement> clazz) {
        String name = getRequirementName(clazz);
        Function<String[], Requirement> constructor = createConstructor(clazz);

        RequirementInfo info = new RequirementInfo(name, constructor, clazz);
        REGISTRY.put(name, info);
        CLASS_REGISTRY.put(clazz, info);
    }

    /**
     * Determines the requirement name from the class.
     */
    private static String getRequirementName(Class<? extends AnnotatedRequirement> clazz) {
        RequirementName nameAnnotation = clazz.getAnnotation(RequirementName.class);
        if (nameAnnotation != null) {
            return nameAnnotation.value();
        }

        // Derive name from class name
        String className = clazz.getSimpleName();
        if (className.endsWith("Requirement")) {
            className = className.substring(0, className.length() - "Requirement".length());
        }

        // Convert to kebab-case
        return className.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }

    /**
     * Creates a constructor function for the requirement class.
     */
    private static Function<String[], Requirement> createConstructor(Class<? extends AnnotatedRequirement> clazz) {
        return params -> {
            try {
                Constructor<? extends AnnotatedRequirement> constructor =
                        clazz.getConstructor(String[].class);
                return constructor.newInstance((Object) params);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException("Failed to create requirement instance: " + clazz.getName(), e);
            }
        };
    }

    /**
     * Retrieves a requirement info by name.
     *
     * @param name the requirement name
     * @return the requirement info, or null if not found
     */
    @Nullable
    public static RequirementInfo fromName(@NotNull String name) {
        return REGISTRY.get(name);
    }

    /**
     * Retrieves a requirement info by class.
     *
     * @param clazz the requirement class
     * @return the requirement info, or null if not found
     */
    @Nullable
    public static RequirementInfo fromClass(@NotNull Class<? extends Requirement> clazz) {
        return CLASS_REGISTRY.get(clazz);
    }

    /**
     * Gets all registered requirement names.
     *
     * @return an unmodifiable set of requirement names
     */
    @NotNull
    public static Set<String> getRegisteredNames() {
        return Collections.unmodifiableSet(REGISTRY.keySet());
    }

    /**
     * Gets all registered requirement classes.
     *
     * @return an unmodifiable set of requirement classes
     */
    @NotNull
    public static Set<Class<? extends Requirement>> getRegisteredClasses() {
        return Collections.unmodifiableSet(CLASS_REGISTRY.keySet());
    }

    /**
     * Gets all registered requirement infos.
     *
     * @return an unmodifiable collection of requirement infos
     */
    @NotNull
    public static Collection<RequirementInfo> getAllRequirements() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    /**
         * Information about a registered requirement type.
         */
        public record RequirementInfo(String name, Function<String[], Requirement> constructor,
                                      Class<? extends Requirement> requirementClass) {
            public RequirementInfo(@NotNull String name,
                                   @NotNull Function<String[], Requirement> constructor,
                                   @NotNull Class<? extends Requirement> requirementClass) {
                this.name = name;
                this.constructor = constructor;
                this.requirementClass = requirementClass;
            }

            @Override
            @NotNull
            public String name() {
                return name;
            }

            @Override
            @NotNull
            public Function<String[], Requirement> constructor() {
                return constructor;
            }

            @Override
            @NotNull
            public Class<? extends Requirement> requirementClass() {
                return requirementClass;
            }

            @Override
            public @NotNull String toString() {
                return "RequirementInfo{" +
                        "name='" + name + '\'' +
                        ", class=" + requirementClass.getSimpleName() +
                        '}';
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                RequirementInfo that = (RequirementInfo) o;
                return Objects.equals(name, that.name) &&
                        Objects.equals(requirementClass, that.requirementClass);
            }

            @Override
            public int hashCode() {
                return Objects.hash(name, requirementClass);
            }
        }
}
