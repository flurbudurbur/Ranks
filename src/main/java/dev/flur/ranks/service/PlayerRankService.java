package dev.flur.ranks.service;

import dev.flur.ranks.requirement.Requirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Service interface for player-specific rank operations.
 */
public interface PlayerRankService {

    /**
     * Gets a player's current rank.
     *
     * @param player The player to get the rank for
     * @return The player's current rank
     */
    @NotNull
    String getCurrentRank(@NotNull Player player);

    /**
     * Gets the requirements for a player to upgrade to a specific rank.
     *
     * @param player     The player to get requirements for
     * @param targetRank The rank to get requirements for
     * @return A list of requirements for the player to upgrade to the rank
     */
    @NotNull
    List<Requirement> getRequirementsForRank(@NotNull Player player, @NotNull String targetRank);

    /**
     * Gets the requirements that a player has not met for a specific rank.
     *
     * @param player     The player to check
     * @param targetRank The rank to check
     * @return A list of requirements that the player has not met
     */
    @NotNull
    List<Requirement> getUnmetRequirements(@NotNull Player player, @NotNull String targetRank);

    /**
     * Gets the progress of a player towards meeting all requirements for a rank.
     *
     * @param player     The player to check
     * @param targetRank The rank to check
     * @return A map of requirement descriptions to progress percentages (0-100)
     */
    @NotNull
    Map<String, Integer> getRequirementProgress(@NotNull Player player, @NotNull String targetRank);

    /**
     * Checks if a player has permission to use a specific rank command.
     *
     * @param player  The player to check
     * @param command The command to check
     * @return True if the player has permission, false otherwise
     */
    boolean hasRankCommandPermission(@NotNull Player player, @NotNull String command);
}