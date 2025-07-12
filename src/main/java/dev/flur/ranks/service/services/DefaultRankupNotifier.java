package dev.flur.ranks.service.services;

import dev.flur.ranks.message.Messages;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.service.MessageService;
import dev.flur.ranks.service.RankProgressionService;
import dev.flur.ranks.service.RankupNotifier;
import dev.flur.ranks.service.RequirementValidator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Default implementation of the RankupNotifier interface.
 */
public class DefaultRankupNotifier implements RankupNotifier {

    private final MessageService messageService;
    private final RequirementValidator requirementValidator;
    private final RankProgressionService rankProgressionService;
    private final Logger logger;

    public DefaultRankupNotifier(
            @NotNull MessageService messageService,
            @NotNull RequirementValidator requirementValidator,
            @NotNull RankProgressionService rankProgressionService,
            @NotNull Logger logger) {
        this.messageService = messageService;
        this.requirementValidator = requirementValidator;
        this.rankProgressionService = rankProgressionService;
        this.logger = logger;
    }

    @Override
    public void sendPlayerOnlyMessage(@NotNull CommandSender sender) {
        messageService.sendMessage(sender, Messages.PLAYER_ONLY);
    }

    @Override
    public void sendErrorMessage(@NotNull Player player, @NotNull String errorMessage) {
        messageService.sendMessage(player, Messages.ERROR_OCCURRED);
        logger.severe("Error in rankup command: " + errorMessage);
    }

    @Override
    public void sendCurrentRankErrorMessage(@NotNull Player player) {
        messageService.sendMessage(player, Messages.CURRENT_RANK_ERROR);
    }

    @Override
    public void sendHighestRankMessage(@NotNull Player player) {
        messageService.sendMessage(player, Messages.HIGHEST_RANK);
    }

    @Override
    public void showAvailableRanks(@NotNull Player player, @NotNull Map<String, String> availableRanks) {
        messageService.sendMessage(player, Messages.MULTIPLE_RANKS);
        availableRanks.keySet().forEach(rank -> {
            Map<String, Object> context = new HashMap<>();
            context.put("rank", rank);
            messageService.sendMessage(player, Messages.RANK_LIST_ITEM, context);
        });
    }

    @Override
    public void showInvalidRankMessage(@NotNull Player player, @NotNull Map<String, String> availableRanks) {
        messageService.sendMessage(player, Messages.INVALID_RANK);
        availableRanks.keySet().forEach(rank -> {
            Map<String, Object> context = new HashMap<>();
            context.put("rank", rank);
            messageService.sendMessage(player, Messages.RANK_LIST_ITEM, context);
        });
    }

    @Override
    public void sendRankupSuccessMessage(@NotNull Player player, @NotNull String targetRank) {
        Map<String, Object> context = new HashMap<>();
        context.put("targetRank", targetRank);
        messageService.sendMessage(player, Messages.RANKUP_SUCCESS, context);
    }

    @Override
    public void sendRankupFailedMessage(@NotNull Player player) {
        messageService.sendMessage(player, Messages.RANKUP_FAILED);
    }

    @Override
    public void notifyUnmetRequirements(@NotNull Player player, @NotNull List<Requirement> unmetRequirements) {
        messageService.sendMessage(player, Messages.REQUIREMENTS_NOT_MET);
        unmetRequirements.forEach(req -> {
            String reqName = requirementValidator.getRequirementDescription(req);
            Map<String, Object> context = new HashMap<>();
            context.put("requirement", reqName);
            messageService.sendMessage(player, Messages.REQUIREMENT_ITEM, context);
        });
    }

    @Override
    public void broadcastRankup(@NotNull Player player, @NotNull String currentRank, @NotNull String targetRank) {
        rankProgressionService.broadcastRankUpgrade(player, currentRank, targetRank);
    }
}