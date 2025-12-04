package dev.flur.ranks.service.services;

import dev.flur.ranks.message.Messages;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.result.RankupOutcome;
import dev.flur.ranks.result.Result;
import dev.flur.ranks.service.*;
import dev.flur.ranks.template.context.TemplateContextBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Default implementation of RankupService.
 * Combines player rank info, validation, processing, and notifications.
 */
public class DefaultRankupService implements RankupService {

    private final PermissionService permissionService;
    private final RequirementService requirementService;
    private final MessageService messageService;
    private final RanksService ranksService;
    private final Logger logger;
    private final boolean broadcastRankups;

    public DefaultRankupService(
            @NotNull PermissionService permissionService,
            @NotNull RequirementService requirementService,
            @NotNull MessageService messageService,
            @NotNull RanksService ranksService,
            @NotNull Logger logger,
            boolean broadcastRankups) {
        this.permissionService = permissionService;
        this.requirementService = requirementService;
        this.messageService = messageService;
        this.ranksService = ranksService;
        this.logger = logger;
        this.broadcastRankups = broadcastRankups;
    }

    // --- Player rank info ---

    @Override
    @NotNull
    public String getCurrentRank(@NotNull Player player) {
        try {
            String group = permissionService.getPrimaryGroup(player);
            return group != null ? group : "";
        } catch (Exception e) {
            logger.severe("Failed to get current rank for player " + player.getName() + ": " + e.getMessage());
            return "";
        }
    }

    @Override
    @NotNull
    public Map<String, String> getAvailableRanks(@NotNull Player player) {
        String currentRank = permissionService.getPrimaryGroup(player);
        return ranksService.getNextRanks(currentRank);
    }

    @Override
    @NotNull
    public List<Requirement> getRequirementsForRank(@NotNull Player player, @NotNull String targetRank) {
        return ranksService.getRequirements(targetRank, player);
    }

    // --- Validation ---

    @Override
    public boolean canRankup(@NotNull Player player, @NotNull String targetRank) {
        if (!permissionService.groupExists(targetRank)) {
            logger.warning("Rank " + targetRank + " does not exist");
            return false;
        }

        Map<String, String> availableRanks = getAvailableRanks(player);
        if (!availableRanks.containsKey(targetRank)) {
            logger.warning("Rank " + targetRank + " is not available for player " + player.getName());
            return false;
        }

        List<Requirement> requirements = getRequirementsForRank(player, targetRank);
        return requirementService.meetsAllRequirements(player, requirements);
    }

    @Override
    @Nullable
    public String determineTargetRank(@NotNull Map<String, String> availableRanks, @NotNull String[] args) {
        if (availableRanks.size() == 1) {
            return availableRanks.keySet().iterator().next();
        }

        if (args.length > 0) {
            String specifiedRank = args[0].toLowerCase();
            return availableRanks.keySet().stream()
                    .filter(rank -> rank.toLowerCase().equals(specifiedRank))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    @Override
    public boolean shouldShowRankOptions(@NotNull Map<String, String> availableRanks, @NotNull String[] args) {
        return availableRanks.size() > 1 && args.length == 0;
    }

    // --- Processing ---

    @Override
    @NotNull
    public Result<RankupOutcome> processRankup(@NotNull Player player, @NotNull String targetRank) {
        List<Requirement> requirements = getRequirementsForRank(player, targetRank);
        List<Requirement> unmetRequirements = requirementService.getUnmetRequirements(player, requirements);

        if (!unmetRequirements.isEmpty()) {
            return Result.failure("Player does not meet all requirements for rank " + targetRank);
        }

        return upgradeRank(player, targetRank);
    }

    @NotNull
    private Result<RankupOutcome> upgradeRank(@NotNull Player player, @NotNull String targetRank) {
        try {
            String currentRank = getCurrentRank(player);

            // Remove from current rank
            if (!currentRank.isEmpty()) {
                permissionService.removeFromGroup(player, currentRank);
            }

            // Add to new rank
            boolean success = permissionService.addToGroup(player, targetRank);

            if (success) {
                logger.info("Player " + player.getName() + " upgraded from " + currentRank + " to " + targetRank);
                return Result.success(new RankupOutcome(player, currentRank, targetRank, true));
            } else {
                // Revert if failed
                if (!currentRank.isEmpty()) {
                    permissionService.addToGroup(player, currentRank);
                }
                logger.warning("Failed to upgrade player " + player.getName() + " to rank " + targetRank);
                return Result.failure("Failed to upgrade rank for player " + player.getName());
            }
        } catch (Exception e) {
            logger.severe("Error upgrading player " + player.getName() + " to rank " + targetRank + ": " + e.getMessage());
            return Result.failure("Error upgrading rank: " + e.getMessage());
        }
    }

    @Override
    public double getUpgradeCost(@NotNull String targetRank) {
        // Cost handling is done through requirements (e.g., money requirement)
        return 0;
    }

    // --- Notifications ---

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
        Map<String, Object> context = new HashMap<>(1);
        for (String rank : availableRanks.keySet()) {
            context.clear();
            context.put("rank", rank);
            messageService.sendMessage(player, Messages.RANK_LIST_ITEM, context);
        }
    }

    @Override
    public void showInvalidRankMessage(@NotNull Player player, @NotNull Map<String, String> availableRanks) {
        messageService.sendMessage(player, Messages.INVALID_RANK);
        Map<String, Object> context = new HashMap<>(1);
        for (String rank : availableRanks.keySet()) {
            context.clear();
            context.put("rank", rank);
            messageService.sendMessage(player, Messages.RANK_LIST_ITEM, context);
        }
    }

    @Override
    public void sendRankupSuccessMessage(@NotNull Player player, @NotNull String targetRank) {
        String currentRank = getCurrentRank(player);
        Map<String, Object> context = TemplateContextBuilder.create()
                .withPlayer(player)
                .withStep("step", currentRank, targetRank, player, ranksService, requirementService)
                .with("targetRank", targetRank) // Keep for backwards compatibility
                .build();
        messageService.sendMessage(player, Messages.RANKUP_SUCCESS, context);
    }

    @Override
    public void sendRankupFailedMessage(@NotNull Player player) {
        messageService.sendMessage(player, Messages.RANKUP_FAILED);
    }

    @Override
    public void notifyUnmetRequirements(@NotNull Player player, @NotNull String targetRank) {
        String currentRank = getCurrentRank(player);
        Map<String, Object> context = TemplateContextBuilder.create()
                .withPlayer(player)
                .withStep("step", currentRank, targetRank, player, ranksService, requirementService)
                .build();
        messageService.sendMessage(player, Messages.UNMET_REQUIREMENTS, context);
    }

    @Override
    public void broadcastRankup(@NotNull Player player, @NotNull String currentRank, @NotNull String targetRank) {
        if (!broadcastRankups) {
            return;
        }

        Map<String, Object> context = TemplateContextBuilder.create()
                .withPlayer(player)
                .withStep("step", currentRank, targetRank, player, ranksService, requirementService)
                .with("playerName", player.getName()) // Keep for backwards compatibility
                .with("currentRank", currentRank)
                .with("targetRank", targetRank)
                .build();
        messageService.broadcastMessage(Messages.RANKUP_BROADCAST, context);
    }
}
