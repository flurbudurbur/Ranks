package dev.flur.ranks.service.services;

import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.service.RequirementValidator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Default implementation of the RequirementValidator interface.
 */
public class DefaultRequirementValidator implements RequirementValidator {

    private final Logger logger;

    /**
     * Creates a new DefaultRequirementValidator with the specified logger.
     *
     * @param logger the logger to use
     * @param registry the requirement registry (unused, kept for API compatibility)
     */
    public DefaultRequirementValidator(@NotNull Logger logger, @NotNull DefaultRequirementRegistry registry) {
        this.logger = logger;
    }

    @Override
    public boolean meetsAllRequirements(@NotNull Player player, @NotNull List<Requirement> requirements) {
        for (Requirement req : requirements) {
            try {
                if (!req.meetsRequirement(player)) {
                    return false;
                }
            } catch (Exception e) {
                logger.severe("Error checking requirement: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    @Override
    @NotNull
    public List<Requirement> getUnmetRequirements(@NotNull Player player, @NotNull List<Requirement> requirements) {
        return requirements.stream()
                .filter(req -> {
                    try {
                        return !req.meetsRequirement(player);
                    } catch (Exception e) {
                        logger.severe("Error checking requirement: " + e.getMessage());
                        return true; // Assume requirement not met if error occurs
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    @NotNull
    public Map<Requirement, Boolean> getRequirementStatus(@NotNull Player player, @NotNull List<Requirement> requirements) {
        Map<Requirement, Boolean> statusMap = new HashMap<>();

        for (Requirement requirement : requirements) {
            try {
                boolean meets = requirement.meetsRequirement(player);
                statusMap.put(requirement, meets);
            } catch (Exception e) {
                logger.severe("Error checking requirement status: " + e.getMessage());
                statusMap.put(requirement, false); // Assume requirement not met if error occurs
            }
        }

        return statusMap;
    }

    @Override
    @NotNull
    public String getRequirementDescription(@NotNull Requirement requirement) {
        return requirement.toString();
    }
}
