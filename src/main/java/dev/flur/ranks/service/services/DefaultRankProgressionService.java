package dev.flur.ranks.service.services;

import dev.flur.ranks.message.Messages;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.service.MessageService;
import dev.flur.ranks.service.PermissionService;
import dev.flur.ranks.service.RankProgressionService;
import dev.flur.ranks.service.RequirementValidator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Default implementation of the RankProgressionService interface.
 */
public class DefaultRankProgressionService implements RankProgressionService {

    private final PermissionService permissionService;
    private final RequirementValidator requirementValidator;
    private final MessageService messageService;
    private final FileConfiguration ranksConfig;
    private final Logger logger;
    private final boolean broadcastRankups;

    public DefaultRankProgressionService(
            @NotNull PermissionService permissionService,
            @NotNull RequirementValidator requirementValidator,
            @NotNull MessageService messageService,
            @NotNull FileConfiguration ranksConfig,
            @NotNull Logger logger,
            boolean broadcastRankups) {
        this.permissionService = permissionService;
        this.requirementValidator = requirementValidator;
        this.messageService = messageService;
        this.ranksConfig = ranksConfig;
        this.logger = logger;
        this.broadcastRankups = broadcastRankups;
    }

    @Override
    public boolean upgradeRank(@NotNull Player player, @NotNull String targetRank) {
        String currentRank = permissionService.getPrimaryGroup(player);

        if (!canUpgradeToRank(player, targetRank)) {
            return false;
        }

        try {
            // Remove from current rank
            if (!currentRank.isEmpty()) {
                permissionService.removeFromGroup(player, currentRank);
            }

            // Add to new rank
            boolean success = permissionService.addToGroup(player, targetRank);

            if (success) {
                // Handle economy cost if applicable
                double cost = getUpgradeCost(targetRank);
                // Economy handling would go here if implemented

                logger.info("Player " + player.getName() + " upgraded from " + currentRank + " to " + targetRank);
            } else {
                // Revert if failed
                if (!currentRank.isEmpty()) {
                    permissionService.addToGroup(player, currentRank);
                }
                logger.warning("Failed to upgrade player " + player.getName() + " to rank " + targetRank);
            }

            return success;
        } catch (Exception e) {
            logger.severe("Error upgrading player " + player.getName() + " to rank " + targetRank + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    @NotNull
    public Map<String, String> getAvailableRanks(@NotNull Player player) {
        String currentRank = permissionService.getPrimaryGroup(player);
        Map<String, String> availableRanks = new HashMap<>();

        ConfigurationSection ranksSection = ranksConfig.getConfigurationSection("ranks");
        if (ranksSection == null) {
            return availableRanks;
        }

        ConfigurationSection currentRankSection = ranksSection.getConfigurationSection(currentRank);
        if (currentRankSection == null) {
            return availableRanks;
        }

        ConfigurationSection nextRanksSection = currentRankSection.getConfigurationSection("next-ranks");
        if (nextRanksSection == null) {
            return availableRanks;
        }

        for (String nextRank : nextRanksSection.getKeys(false)) {
            String displayName = nextRanksSection.getString(nextRank, nextRank);
            availableRanks.put(nextRank, displayName);
        }

        return availableRanks;
    }

    @Override
    public boolean canUpgradeToRank(@NotNull Player player, @NotNull String targetRank) {
        // Check if the rank exists
        if (!permissionService.groupExists(targetRank)) {
            logger.warning("Rank " + targetRank + " does not exist");
            return false;
        }

        // Check if the rank is available from the player's current rank
        Map<String, String> availableRanks = getAvailableRanks(player);
        if (!availableRanks.containsKey(targetRank)) {
            logger.warning("Rank " + targetRank + " is not available for player " + player.getName());
            return false;
        }

        // Check if the player meets all requirements
        List<Requirement> requirements = getRequirementsForRank(player, targetRank);
        return requirementValidator.meetsAllRequirements(player, requirements);
    }

    @Override
    public double getUpgradeCost(@NotNull String targetRank) {
        ConfigurationSection ranksSection = ranksConfig.getConfigurationSection("ranks");
        if (ranksSection == null) {
            return 0;
        }

        ConfigurationSection rankSection = ranksSection.getConfigurationSection(targetRank);
        if (rankSection == null) {
            return 0;
        }

        return rankSection.getDouble("cost", 0);
    }

    @Override
    public void broadcastRankUpgrade(@NotNull Player player, @NotNull String currentRank, @NotNull String targetRank) {
        if (!broadcastRankups) {
            return;
        }

        Map<String, Object> context = new HashMap<>();
        context.put("playerName", player.getName());
        context.put("currentRank", currentRank);
        context.put("targetRank", targetRank);
        messageService.broadcastMessage(Messages.RANKUP_BROADCAST, context);
    }

    /**
     * Gets the requirements for a specific rank.
     *
     * @param player     The player to get requirements for
     * @param targetRank The rank to get requirements for
     * @return A list of requirements for the rank
     */
    private List<Requirement> getRequirementsForRank(@NotNull Player player, @NotNull String targetRank) {
        // This would need to be implemented based on how requirements are stored
        // For now, returning an empty list as a placeholder
        return List.of();
    }
}