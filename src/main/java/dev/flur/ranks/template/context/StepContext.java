package dev.flur.ranks.template.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Wrapper representing a rank step (transition) that provides template-friendly access.
 * <p>
 * Template usage:
 * <pre>
 * {{ step.from }}              → "second"
 * {{ step.to }}                → "third"
 * {{ step.isRootRank }}        → true/false
 * {{ step.isTerminalRank }}    → true/false
 * {{ step.requirements }}      → List of RequirementContext
 * {{ step.get('money') }}      → RequirementContext for 'money'
 * {{ step.allSatisfied }}      → true/false
 * {{ step.satisfiedCount }}    → 2
 * {{ step.progressPercent }}   → 66
 * </pre>
 */
public class StepContext {

    private final String fromRank;
    private final String toRank;
    private final boolean isRootRank;
    private final boolean isTerminalRank;
    private final List<RequirementContext> requirements;

    public StepContext(
            @NotNull String fromRank,
            @NotNull String toRank,
            boolean isRootRank,
            boolean isTerminalRank,
            @NotNull List<RequirementContext> requirements) {
        this.fromRank = fromRank;
        this.toRank = toRank;
        this.isRootRank = isRootRank;
        this.isTerminalRank = isTerminalRank;
        this.requirements = List.copyOf(requirements);
    }

    /**
     * Gets the source rank ID.
     */
    @NotNull
    public String getFrom() {
        return fromRank;
    }

    /**
     * Gets the target rank ID.
     */
    @NotNull
    public String getTo() {
        return toRank;
    }

    /**
     * Checks if the source rank is a root rank (has no incoming steps).
     */
    public boolean isRootRank() {
        return isRootRank;
    }

    /**
     * Checks if the target rank is a terminal rank (has no outgoing steps).
     */
    public boolean isTerminalRank() {
        return isTerminalRank;
    }

    /**
     * Gets all requirements for this step.
     */
    @NotNull
    public List<RequirementContext> getRequirements() {
        return requirements;
    }

    /**
     * Gets a specific requirement by type name.
     *
     * @param type the requirement type (e.g., "money")
     * @return the requirement context, or null if not found
     */
    @Nullable
    public RequirementContext get(@NotNull String type) {
        String normalizedType = type.toLowerCase();
        for (RequirementContext req : requirements) {
            if (req.getType().equalsIgnoreCase(normalizedType)) {
                return req;
            }
        }
        return null;
    }

    /**
     * Checks if all requirements are satisfied.
     */
    public boolean isAllSatisfied() {
        for (RequirementContext req : requirements) {
            if (!req.isSatisfied()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the count of satisfied requirements.
     */
    public int getSatisfiedCount() {
        int count = 0;
        for (RequirementContext req : requirements) {
            if (req.isSatisfied()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the overall progress percentage (0-100).
     * Calculated as the percentage of requirements satisfied.
     */
    public int getProgressPercent() {
        if (requirements.isEmpty()) {
            return 100;
        }
        return (int) ((getSatisfiedCount() * 100.0) / requirements.size());
    }

    @Override
    public String toString() {
        return fromRank + " -> " + toRank;
    }
}
