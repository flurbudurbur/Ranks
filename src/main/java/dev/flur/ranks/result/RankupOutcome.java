package dev.flur.ranks.result;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the outcome of a rankup operation.
 */
public class RankupOutcome {

    private final Player player;
    private final String currentRank;
    private final String targetRank;
    private final boolean successful;

    /**
     * Creates a new rankup outcome.
     *
     * @param player      The player who ranked up
     * @param currentRank The player's previous rank
     * @param targetRank  The player's new rank
     * @param successful  Whether the rankup was successful
     */
    public RankupOutcome(
            @NotNull Player player,
            @NotNull String currentRank,
            @NotNull String targetRank,
            boolean successful) {
        this.player = player;
        this.currentRank = currentRank;
        this.targetRank = targetRank;
        this.successful = successful;
    }

    /**
     * Gets the player who ranked up.
     *
     * @return The player who ranked up
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the player's previous rank.
     *
     * @return The player's previous rank
     */
    @NotNull
    public String getCurrentRank() {
        return currentRank;
    }

    /**
     * Gets the player's new rank.
     *
     * @return The player's new rank
     */
    @NotNull
    public String getTargetRank() {
        return targetRank;
    }

    /**
     * Checks if the rankup was successful.
     *
     * @return True if the rankup was successful, false otherwise
     */
    public boolean isSuccessful() {
        return successful;
    }
}