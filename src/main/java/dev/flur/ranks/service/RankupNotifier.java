package dev.flur.ranks.service;

import dev.flur.ranks.requirement.Requirement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Service interface for notifying players about rankup operations.
 */
public interface RankupNotifier {

    /**
     * Sends a player-only message to a command sender.
     *
     * @param sender The command sender to send the message to
     */
    void sendPlayerOnlyMessage(@NotNull CommandSender sender);

    /**
     * Sends an error message to a player.
     *
     * @param player       The player to send the message to
     * @param errorMessage The error message or exception details
     */
    void sendErrorMessage(@NotNull Player player, @NotNull String errorMessage);

    /**
     * Sends a current rank error message to a player.
     *
     * @param player The player to send the message to
     */
    void sendCurrentRankErrorMessage(@NotNull Player player);

    /**
     * Sends a highest rank message to a player.
     *
     * @param player The player to send the message to
     */
    void sendHighestRankMessage(@NotNull Player player);

    /**
     * Shows available ranks to a player.
     *
     * @param player         The player to show the ranks to
     * @param availableRanks The available ranks to show
     */
    void showAvailableRanks(@NotNull Player player, @NotNull Map<String, String> availableRanks);

    /**
     * Shows an invalid rank message to a player.
     *
     * @param player         The player to send the message to
     * @param availableRanks The available ranks to show
     */
    void showInvalidRankMessage(@NotNull Player player, @NotNull Map<String, String> availableRanks);

    /**
     * Sends a rankup success message to a player.
     *
     * @param player     The player to send the message to
     * @param targetRank The rank the player upgraded to
     */
    void sendRankupSuccessMessage(@NotNull Player player, @NotNull String targetRank);

    /**
     * Sends a rankup failed message to a player.
     *
     * @param player The player to send the message to
     */
    void sendRankupFailedMessage(@NotNull Player player);

    /**
     * Notifies a player about unmet requirements.
     *
     * @param player            The player to notify
     * @param unmetRequirements The list of unmet requirements
     */
    void notifyUnmetRequirements(@NotNull Player player, @NotNull List<Requirement> unmetRequirements);

    /**
     * Broadcasts a rankup message to all players.
     *
     * @param player      The player who ranked up
     * @param currentRank The player's previous rank
     * @param targetRank  The player's new rank
     */
    void broadcastRankup(@NotNull Player player, @NotNull String currentRank, @NotNull String targetRank);
}