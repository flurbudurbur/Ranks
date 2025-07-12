package dev.flur.ranks.requirement;

import dev.flur.ranks.requirement.annotations.RequirementAnnotation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class AnnotatedRequirement implements dev.flur.ranks.requirement.Requirement {

    protected final String[] params;
    protected final double amount;

    public AnnotatedRequirement(String @NotNull [] params) {
        validateParametersFromAnnotation(params);
        this.amount = validateRequirementAmount(params);
        this.params = params;
    }

    @Contract(pure = true)
    public static RequirementAnnotation getParameterInfo(@NotNull Class<? extends dev.flur.ranks.requirement.Requirement> clazz) {
        return clazz.getAnnotation(RequirementAnnotation.class);
    }

    private void validateParametersFromAnnotation(String[] params) {
        RequirementAnnotation annotation = getClass().getAnnotation(RequirementAnnotation.class);
        if (annotation != null) {
            if (params.length < annotation.minimum()) {
                throw new IllegalArgumentException("Too few arguments: " + annotation.usage());
            }
            if (params.length > annotation.maximum()) {
                throw new IllegalArgumentException("Too many arguments: " + annotation.usage());
            }
        }
    }

    protected double validateRequirementAmount(String[] params) {
        try {
            double amount = Double.parseDouble(params[params.length - 1]);
            if (amount >= 0) {
                return amount;
            } else {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid amount. Must be a zero-inclusive positive value");
        }
    }

    public final String getParameterDescription() {
        RequirementAnnotation annotation = getClass().getAnnotation(RequirementAnnotation.class);
        return annotation != null ? annotation.usage() : "No description available";
    }

    @Override
    public void consume(@NotNull org.bukkit.entity.Player player) {
        // Default implementation does nothing
        // Subclasses can override this method to implement consumption logic
    }
}
