package dev.flur.ranks.service;

import dev.flur.ranks.requirement.Requirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Service for requirement registration and validation.
 * Combines requirement lookup/creation with player validation.
 */
public interface RequirementService {

    // --- Registry methods (external plugin API) ---

    /**
     * Registers a custom requirement type.
     *
     * @param key       the requirement key (e.g., "custom-req")
     * @param minParams minimum number of parameters
     * @param maxParams maximum number of parameters
     * @param usage     usage description for error messages
     * @param factory   factory function to create instances
     */
    void register(@NotNull String key, int minParams, int maxParams,
                  @NotNull String usage, @NotNull Function<String[], Requirement> factory);

    /**
     * Creates a requirement instance.
     *
     * @param key    the requirement key
     * @param params the parameters
     * @return the created requirement
     * @throws IllegalArgumentException if the requirement type is not found or params are invalid
     */
    @NotNull
    Requirement create(@NotNull String key, @NotNull String[] params);

    /**
     * Checks if a requirement type exists.
     */
    boolean hasRequirement(@NotNull String name);

    /**
     * Gets the usage string for a requirement type.
     */
    @Nullable
    String getUsage(@NotNull String name);

    /**
     * Gets all registered requirement names (built-in + custom).
     */
    @NotNull
    Set<String> getRegisteredNames();

    // --- Validation methods ---

    /**
     * Checks if the player meets all requirements.
     */
    boolean meetsAllRequirements(@NotNull Player player, @NotNull List<Requirement> requirements);

    /**
     * Gets the list of unmet requirements for a player.
     */
    @NotNull
    List<Requirement> getUnmetRequirements(@NotNull Player player, @NotNull List<Requirement> requirements);

    /**
     * Gets a human-readable description of a requirement.
     */
    @NotNull
    String getRequirementDescription(@NotNull Requirement requirement);
}
