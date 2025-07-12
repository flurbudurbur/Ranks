package dev.flur.ranks.service;

import dev.flur.ranks.requirement.Requirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Service interface for validating rankup operations.
 */
public interface RankupValidator {

    /**
     * Checks if a player has a valid current rank.
     *
     * @param player The player to check
     * @return True if the player has a valid current rank, false otherwise
     */
    boolean hasValidCurrentRank(@NotNull Player player);

    /**
     * Gets the current rank of a player.
     *
     * @param player The player to get the rank for
     * @return The player's current rank, or empty string if not found
     */
    @NotNull
    String getCurrentRank(@NotNull Player player);

    /**
     * Checks if a player should be shown rank options.
     *
     * @param availableRanks The available ranks for the player
     * @param args           Command arguments
     * @return True if rank options should be shown, false otherwise
     */
    boolean shouldShowRankOptions(@NotNull Map<String, String> availableRanks, @NotNull String[] args);

    /**
     * Determines the target rank for a player based on available ranks and command arguments.
     *
     * @param availableRanks The available ranks for the player
     * @param args           Command arguments
     * @return The target rank, or null if no valid rank was determined
     */
    @Nullable
    String determineTargetRank(@NotNull Map<String, String> availableRanks, @NotNull String[] args);

    /**
     * Checks if a player meets all requirements for a rank.
     *
     * @param player     The player to check
     * @param targetRank The rank to check requirements for
     * @return True if the player meets all requirements, false otherwise
     */
    boolean meetsRequirementsForRank(@NotNull Player player, @NotNull String targetRank);

    /**
     * Gets the unmet requirements for a player for a specific rank.
     *
     * @param player     The player to check
     * @param targetRank The rank to check requirements for
     * @return A list of unmet requirements
     */
    @NotNull
    List<Requirement> getUnmetRequirementsForRank(@NotNull Player player, @NotNull String targetRank);
}