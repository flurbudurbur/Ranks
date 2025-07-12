package dev.flur.ranks.service.services;

import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.service.PermissionService;
import dev.flur.ranks.service.PlayerRankService;
import dev.flur.ranks.service.RequirementValidator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Default implementation of the PlayerRankService interface.
 */
public class DefaultPlayerRankService implements PlayerRankService {

    private final PermissionService permissionService;
    private final RequirementValidator requirementValidator;
    private final FileConfiguration ranksConfig;
    private final Logger logger;

    public DefaultPlayerRankService(
            @NotNull PermissionService permissionService,
            @NotNull RequirementValidator requirementValidator,
            @NotNull FileConfiguration ranksConfig,
            @NotNull Logger logger) {
        this.permissionService = permissionService;
        this.requirementValidator = requirementValidator;
        this.ranksConfig = ranksConfig;
        this.logger = logger;
    }

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
    public List<Requirement> getRequirementsForRank(@NotNull Player player, @NotNull String targetRank) {
        // This would need to be implemented based on how requirements are stored
        // For now, returning an empty list as a placeholder
        return new ArrayList<>();
    }

    @Override
    @NotNull
    public List<Requirement> getUnmetRequirements(@NotNull Player player, @NotNull String targetRank) {
        List<Requirement> requirements = getRequirementsForRank(player, targetRank);
        return requirementValidator.getUnmetRequirements(player, requirements);
    }

    @Override
    @NotNull
    public Map<String, Integer> getRequirementProgress(@NotNull Player player, @NotNull String targetRank) {
        List<Requirement> requirements = getRequirementsForRank(player, targetRank);
        Map<String, Integer> progress = new HashMap<>();

        for (Requirement requirement : requirements) {
            try {
                // This is a placeholder - actual implementation would depend on how requirements track progress
                String description = requirementValidator.getRequirementDescription(requirement);
                int progressValue = requirement.meetsRequirement(player) ? 100 : 0;
                progress.put(description, progressValue);
            } catch (Exception e) {
                logger.warning("Error getting progress for requirement: " + e.getMessage());
                progress.put("Unknown requirement", 0);
            }
        }

        return progress;
    }

    @Override
    public boolean hasRankCommandPermission(@NotNull Player player, @NotNull String command) {
        String permission = "ranks." + command.toLowerCase();
        return permissionService.hasPermission(player, permission);
    }
}