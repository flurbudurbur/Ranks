package dev.flur.ranks.service;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Service interface for managing rank progression.
 */
public interface RankProgressionService {

    /**
     * Performs a rank upgrade for a player.
     *
     * @param player     The player to upgrade
     * @param targetRank The rank to upgrade to
     * @return True if the upgrade was successful, false otherwise
     */
    boolean upgradeRank(@NotNull Player player, @NotNull String targetRank);

    /**
     * Gets the available ranks a player can upgrade to from their current rank.
     *
     * @param player The player to check
     * @return A map of rank names to rank display names that the player can upgrade to
     */
    @NotNull
    Map<String, String> getAvailableRanks(@NotNull Player player);

    /**
     * Checks if a player can upgrade to a specific rank.
     *
     * @param player     The player to check
     * @param targetRank The rank to check
     * @return True if the player can upgrade to the rank, false otherwise
     */
    boolean canUpgradeToRank(@NotNull Player player, @NotNull String targetRank);

    /**
     * Gets the cost of upgrading to a specific rank.
     *
     * @param targetRank The rank to check
     * @return The cost of upgrading to the rank, or 0 if there is no cost
     */
    double getUpgradeCost(@NotNull String targetRank);

    /**
     * Broadcasts a rank upgrade message.
     *
     * @param player      The player who upgraded
     * @param currentRank The player's previous rank
     * @param targetRank  The player's new rank
     */
    void broadcastRankUpgrade(@NotNull Player player, @NotNull String currentRank, @NotNull String targetRank);
}