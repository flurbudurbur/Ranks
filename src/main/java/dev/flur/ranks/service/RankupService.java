package dev.flur.ranks.service;

import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.result.RankupOutcome;
import dev.flur.ranks.result.Result;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Service for handling all rankup operations.
 * Combines player rank info, validation, processing, and notifications.
 */
public interface RankupService {

    // --- Player rank info ---

    /**
     * Gets the player's current rank/group.
     */
    @NotNull
    String getCurrentRank(@NotNull Player player);

    /**
     * Gets available ranks the player can upgrade to from their current rank.
     *
     * @return map of rank name to display name
     */
    @NotNull
    Map<String, String> getAvailableRanks(@NotNull Player player);

    /**
     * Gets the requirements for a specific rank.
     */
    @NotNull
    List<Requirement> getRequirementsForRank(@NotNull Player player, @NotNull String targetRank);

    // --- Validation ---

    /**
     * Checks if the player can upgrade to the specified rank.
     */
    boolean canRankup(@NotNull Player player, @NotNull String targetRank);

    /**
     * Determines the target rank from available ranks and command args.
     *
     * @return the target rank name, or null if cannot be determined
     */
    @Nullable
    String determineTargetRank(@NotNull Map<String, String> availableRanks, @NotNull String[] args);

    /**
     * Checks if multiple rank options should be shown to the player.
     */
    boolean shouldShowRankOptions(@NotNull Map<String, String> availableRanks, @NotNull String[] args);

    // --- Processing ---

    /**
     * Processes a rankup for the player.
     */
    @NotNull
    Result<RankupOutcome> processRankup(@NotNull Player player, @NotNull String targetRank);

    /**
     * Gets the cost to upgrade to a rank.
     */
    double getUpgradeCost(@NotNull String targetRank);

    // --- Notifications ---

    /**
     * Sends a message that the command is for players only.
     */
    void sendPlayerOnlyMessage(@NotNull CommandSender sender);

    /**
     * Sends a generic error message.
     */
    void sendErrorMessage(@NotNull Player player, @NotNull String errorMessage);

    /**
     * Sends a message that there was an error getting the player's current rank.
     */
    void sendCurrentRankErrorMessage(@NotNull Player player);

    /**
     * Sends a message that the player is at the highest rank.
     */
    void sendHighestRankMessage(@NotNull Player player);

    /**
     * Shows the available ranks to the player.
     */
    void showAvailableRanks(@NotNull Player player, @NotNull Map<String, String> availableRanks);

    /**
     * Shows a message that the specified rank is invalid.
     */
    void showInvalidRankMessage(@NotNull Player player, @NotNull Map<String, String> availableRanks);

    /**
     * Sends a rankup success message.
     */
    void sendRankupSuccessMessage(@NotNull Player player, @NotNull String targetRank);

    /**
     * Sends a rankup failed message.
     */
    void sendRankupFailedMessage(@NotNull Player player);

    /**
     * Notifies the player about unmet requirements for a target rank.
     */
    void notifyUnmetRequirements(@NotNull Player player, @NotNull String targetRank);

    /**
     * Broadcasts a rankup to all players.
     */
    void broadcastRankup(@NotNull Player player, @NotNull String currentRank, @NotNull String targetRank);
}
