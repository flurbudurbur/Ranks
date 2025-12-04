package dev.flur.ranks.template.context;

import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.service.RequirementService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper for Requirement that provides template-friendly property access.
 * <p>
 * Template usage:
 * <pre>
 * {{ req.type }}              → "money"
 * {{ req.label }}             → "Money: $5,000"
 * {{ req.satisfied }}         → true/false
 * {{ req.target }}            → 5000.0
 * {{ req.current }}           → 3500.0
 * {{ req.remaining }}         → 1500.0
 * {{ req.progressPercent }}   → 70
 * </pre>
 */
public class RequirementContext {

    private final Requirement requirement;
    private final Player player;
    private final RequirementService requirementService;

    public RequirementContext(
            @NotNull Requirement requirement,
            @NotNull Player player,
            @NotNull RequirementService requirementService) {
        this.requirement = requirement;
        this.player = player;
        this.requirementService = requirementService;
    }

    /**
     * Gets the requirement type identifier (e.g., "money", "xplevel").
     */
    @NotNull
    public String getType() {
        return requirement.getType();
    }

    /**
     * Gets a human-readable description of the requirement.
     */
    @NotNull
    public String getLabel() {
        return requirementService.getRequirementDescription(requirement);
    }

    /**
     * Checks if the player has satisfied this requirement.
     */
    public boolean isSatisfied() {
        return requirement.meetsRequirement(player);
    }

    /**
     * Gets the target amount required.
     */
    public double getTarget() {
        return requirement.getTarget();
    }

    /**
     * Gets the player's current progress value.
     */
    public double getCurrent() {
        return requirement.getCurrent(player);
    }

    /**
     * Gets the remaining amount needed (0 if already satisfied).
     */
    public double getRemaining() {
        return Math.max(0, getTarget() - getCurrent());
    }

    /**
     * Gets the progress percentage (0-100).
     */
    public int getProgressPercent() {
        double target = getTarget();
        if (target <= 0) {
            return isSatisfied() ? 100 : 0;
        }
        return (int) Math.min(100, (getCurrent() / target) * 100);
    }

    /**
     * Gets the underlying requirement.
     */
    @NotNull
    public Requirement getRequirement() {
        return requirement;
    }

    @Override
    public String toString() {
        return requirement.toString();
    }
}
