package dev.flur.ranks.service.services;

import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.service.PlayerRankService;
import dev.flur.ranks.service.RankupValidator;
import dev.flur.ranks.service.RequirementValidator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Default implementation of the RankupValidator interface.
 */
public class DefaultRankupValidator implements RankupValidator {

    private final PlayerRankService playerRankService;
    private final RequirementValidator requirementValidator;
    private final Logger logger;

    public DefaultRankupValidator(
            @NotNull PlayerRankService playerRankService,
            @NotNull RequirementValidator requirementValidator,
            @NotNull Logger logger) {
        this.playerRankService = playerRankService;
        this.requirementValidator = requirementValidator;
        this.logger = logger;
    }

    @Override
    public boolean hasValidCurrentRank(@NotNull Player player) {
        String currentRank = getCurrentRank(player);
        return currentRank.isEmpty();
    }

    @Override
    @NotNull
    public String getCurrentRank(@NotNull Player player) {
        try {
            return playerRankService.getCurrentRank(player);
        } catch (Exception e) {
            logger.severe("Failed to get current rank for player " + player.getName());
            return "";
        }
    }

    @Override
    public boolean shouldShowRankOptions(@NotNull Map<String, String> availableRanks, @NotNull String[] args) {
        return availableRanks.size() > 1 && args.length == 0;
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
    public boolean meetsRequirementsForRank(@NotNull Player player, @NotNull String targetRank) {
        List<Requirement> requirements = playerRankService.getRequirementsForRank(player, targetRank);
        return requirementValidator.meetsAllRequirements(player, requirements);
    }

    @Override
    @NotNull
    public List<Requirement> getUnmetRequirementsForRank(@NotNull Player player, @NotNull String targetRank) {
        List<Requirement> requirements = playerRankService.getRequirementsForRank(player, targetRank);
        return requirementValidator.getUnmetRequirements(player, requirements);
    }
}