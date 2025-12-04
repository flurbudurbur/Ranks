package dev.flur.ranks.service.services;

import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.requirement.RequirementEntry;
import dev.flur.ranks.requirement.RequirementType;
import dev.flur.ranks.service.RequirementService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Default implementation of RequirementService.
 * Combines requirement registration (for external plugins) with player validation.
 */
public class DefaultRequirementService implements RequirementService {

    private final Map<String, RequirementEntry> customRegistry = new ConcurrentHashMap<>();
    private final Logger logger;

    public DefaultRequirementService(@NotNull Logger logger) {
        this.logger = logger;
        logger.info("Requirement service initialized with " + RequirementType.values().length + " built-in types");
    }

    // --- Registry methods ---

    @Override
    public void register(@NotNull String key, int minParams, int maxParams,
                         @NotNull String usage, @NotNull Function<String[], Requirement> factory) {
        if (RequirementType.fromKey(key) != null) {
            throw new IllegalArgumentException("Cannot override built-in requirement: " + key);
        }
        customRegistry.put(key, new RequirementEntry(key, minParams, maxParams, usage, factory));
        logger.info("Registered custom requirement: " + key);
    }

    @Override
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
    public String getUsage(@NotNull String name) {
        RequirementType builtIn = RequirementType.fromKey(name);
        if (builtIn != null) {
            return builtIn.getUsage();
        }
        RequirementEntry custom = customRegistry.get(name);
        return custom != null ? custom.usage() : null;
    }

    @Override
    @NotNull
    public Set<String> getRegisteredNames() {
        Set<String> names = new HashSet<>();
        for (RequirementType type : RequirementType.values()) {
            names.add(type.getKey());
        }
        names.addAll(customRegistry.keySet());
        return Collections.unmodifiableSet(names);
    }

    // --- Validation methods ---

    @Override
    public boolean meetsAllRequirements(@NotNull Player player, @NotNull List<Requirement> requirements) {
        for (Requirement req : requirements) {
            try {
                if (!req.meetsRequirement(player)) {
                    return false;
                }
            } catch (Exception e) {
                logger.severe("Error checking requirement: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    @Override
    @NotNull
    public List<Requirement> getUnmetRequirements(@NotNull Player player, @NotNull List<Requirement> requirements) {
        if (requirements.isEmpty()) {
            return List.of();
        }

        List<Requirement> unmet = new ArrayList<>(requirements.size());
        for (Requirement req : requirements) {
            try {
                if (!req.meetsRequirement(player)) {
                    unmet.add(req);
                }
            } catch (Exception e) {
                logger.severe("Error checking requirement: " + e.getMessage());
                unmet.add(req);
            }
        }
        return unmet.isEmpty() ? List.of() : List.copyOf(unmet);
    }

    @Override
    @NotNull
    public String getRequirementDescription(@NotNull Requirement requirement) {
        return requirement.toString();
    }
}
