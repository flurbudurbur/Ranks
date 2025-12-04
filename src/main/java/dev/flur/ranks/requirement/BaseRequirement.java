package dev.flur.ranks.requirement;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for requirement implementations.
 * Provides common parameter parsing and amount extraction.
 * <p>
 * Validation is handled by {@link RequirementType} before construction.
 */
public abstract class BaseRequirement implements Requirement {

    protected final String[] params;
    protected final double amount;

    protected BaseRequirement(String @NotNull [] params) {
        this.params = params;
        this.amount = parseAmount(params);
    }

    /**
     * Parses the amount from the last parameter.
     * Subclasses can override this for custom parsing (e.g., time durations).
     */
    protected double parseAmount(String[] params) {
        try {
            double value = Double.parseDouble(params[params.length - 1]);
            if (value < 0) {
                throw new IllegalArgumentException("Amount must be non-negative");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount: must be a number");
        }
    }

    @Override
    public void consume(@NotNull Player player) {
        // Default: no consumption. Override in subclasses if needed.
    }

    @Override
    public double getTarget() {
        return amount;
    }
}
