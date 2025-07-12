package dev.flur.ranks.service;

import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.result.RankupOutcome;
import dev.flur.ranks.result.Result;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Service interface for processing rankup operations.
 */
public interface RankupProcessor {

    /**
     * Gets the available ranks a player can upgrade to.
     *
     * @param player The player to check
     * @return A map of rank names to rank display names that the player can upgrade to
     */
    @NotNull
    Map<String, String> getAvailableRanks(@NotNull Player player);

    /**
     * Processes a rankup operation for a player.
     *
     * @param player      The player to process the rankup for
     * @param currentRank The player's current rank
     * @param targetRank  The rank to upgrade to
     * @return A result containing the rankup outcome if successful, or an error message if failed
     */
    @NotNull
    Result<RankupOutcome> processRankup(@NotNull Player player, @NotNull String currentRank, @NotNull String targetRank);

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
     * Gets the unmet requirements for a player for a specific rank.
     *
     * @param player     The player to check
     * @param targetRank The rank to check requirements for
     * @return A list of unmet requirements
     */
    @NotNull
    List<Requirement> getUnmetRequirementsForRank(@NotNull Player player, @NotNull String targetRank);

    /**
     * Upgrades a player's rank.
     *
     * @param player     The player to upgrade
     * @param targetRank The rank to upgrade to
     * @return A result containing the rankup outcome if successful, or an error message if failed
     */
    @NotNull
    Result<RankupOutcome> upgradeRank(@NotNull Player player, @NotNull String targetRank);
}
