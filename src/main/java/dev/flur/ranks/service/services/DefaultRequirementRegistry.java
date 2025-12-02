package dev.flur.ranks.service.services;

import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.requirement.RequirementEntry;
import dev.flur.ranks.requirement.RequirementType;
import dev.flur.ranks.service.RequirementLookup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Registry for requirement types.
 * <p>
 * Built-in requirements are defined in {@link RequirementType} enum.
 * External plugins can register custom requirements via {@link #register}.
 * </p>
 */
public class DefaultRequirementRegistry implements RequirementLookup {

    private final Map<String, RequirementEntry> customRegistry = new ConcurrentHashMap<>();
    private final Logger logger;

    public DefaultRequirementRegistry(@NotNull Logger logger) {
        this.logger = logger;
        logger.info("Requirement registry initialized with " + RequirementType.values().length + " built-in types");
    }

    /**
     * Registers a custom requirement type (for external plugins).
     *
     * @param key       the requirement key (e.g., "custom-req")
     * @param minParams minimum number of parameters
     * @param maxParams maximum number of parameters
     * @param usage     usage description for error messages
     * @param factory   factory function to create instances
     */
    public void register(@NotNull String key, int minParams, int maxParams,
                         @NotNull String usage, @NotNull Function<String[], Requirement> factory) {
        if (RequirementType.fromKey(key) != null) {
            throw new IllegalArgumentException("Cannot override built-in requirement: " + key);
        }
        customRegistry.put(key, new RequirementEntry(key, minParams, maxParams, usage, factory));
        logger.info("Registered custom requirement: " + key);
    }

    /**
     * Creates a requirement instance.
     *
     * @param key    the requirement key
     * @param params the parameters
     * @return the created requirement
     * @throws IllegalArgumentException if the requirement type is not found or params are invalid
     */
    @NotNull
    public Requirement create(@NotNull String key, @NotNull String[] params) {
        // Try built-in types first
        RequirementType builtIn = RequirementType.fromKey(key);
        if (builtIn != null) {
            return builtIn.create(params);
        }

        // Try custom registry
        RequirementEntry custom = customRegistry.get(key);
        if (custom != null) {
            return custom.create(params);
        }

        throw new IllegalArgumentException("Unknown requirement type: " + key);
    }

    @Override
    public boolean hasRequirement(@NotNull String name) {
        return RequirementType.fromKey(name) != null || customRegistry.containsKey(name);
    }

    @Override
    @Nullable
    public Class<? extends Requirement> getRequirementClass(@NotNull String name) {
        // For built-in types, we don't expose the class directly
        // This method is mainly for legacy compatibility
        return null;
    }

    @Override
    @Nullable
    public Requirement createRequirement(@NotNull String name, @NotNull Map<String, String> params) {
        // Convert map to array - for backwards compatibility
        String[] paramsArray = params.values().toArray(new String[0]);
        try {
            return create(name, paramsArray);
        } catch (IllegalArgumentException e) {
            logger.warning("Failed to create requirement " + name + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    @Nullable
    public Requirement createRequirement(@NotNull String name, @NotNull String params) {
        String[] paramsArray = params.isEmpty() ? new String[0] : params.split(",");
        try {
            return create(name, paramsArray);
        } catch (IllegalArgumentException e) {
            logger.warning("Failed to create requirement " + name + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    public int getMinParams(@NotNull String name) {
        RequirementType builtIn = RequirementType.fromKey(name);
        if (builtIn != null) {
            return builtIn.getMinParams();
        }
        RequirementEntry custom = customRegistry.get(name);
        return custom != null ? custom.minParams() : -1;
    }

    @Override
    public int getMaxParams(@NotNull String name) {
        RequirementType builtIn = RequirementType.fromKey(name);
        if (builtIn != null) {
            return builtIn.getMaxParams();
        }
        RequirementEntry custom = customRegistry.get(name);
        return custom != null ? custom.maxParams() : -1;
    }

    @Override
    @NotNull
    public List<String> getParamNames(@NotNull String name) {
        // Parameter names are not tracked in this simplified version
        return Collections.emptyList();
    }

    /**
     * Gets all registered requirement names (built-in + custom).
     */
    @NotNull
    public Set<String> getRegisteredNames() {
        Set<String> names = new HashSet<>();
        for (RequirementType type : RequirementType.values()) {
            names.add(type.getKey());
        }
        names.addAll(customRegistry.keySet());
        return Collections.unmodifiableSet(names);
    }

    /**
     * Gets the usage string for a requirement type.
     */
    @Nullable
    public String getUsage(@NotNull String name) {
        RequirementType builtIn = RequirementType.fromKey(name);
        if (builtIn != null) {
            return builtIn.getUsage();
        }
        RequirementEntry custom = customRegistry.get(name);
        return custom != null ? custom.usage() : null;
    }
}
