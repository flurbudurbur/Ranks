package dev.flur.ranks.service;

import dev.flur.ranks.requirement.Requirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing ranks and rank operations.
 */
public interface RanksService {

    /**
     * Gets all available ranks.
     */
    @NotNull
    List<String> getAllRanks();

    /**
     * Gets the next available ranks for a player's current rank.
     */
    @NotNull
    Map<String, String> getNextRanks(@NotNull String currentRank);

    /**
     * Gets requirements for a specific rank transition.
     */
    @NotNull
    List<Requirement> getRequirements(@NotNull String nextRank, @NotNull Player player);

    /**
     * Checks if a player can upgrade to a specific rank.
     */
    boolean canUpgrade(@NotNull Player player, @NotNull String targetRank);

    /**
     * Performs a rank upgrade for a player.
     */
    boolean upgradeRank(@NotNull Player player, @NotNull String targetRank);

    /**
     * Gets a player's current rank.
     */
    @NotNull
    String getCurrentRank(@NotNull Player player);

    /**
     * Reloads rank configuration.
     */
    void reload();
}