package dev.flur.ranks.requirement;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class AnnotatedRequirement implements Requirement {

    protected final String[] params;
    protected final double amount;

    public AnnotatedRequirement(String @NotNull [] params) {
        validateParametersFromAnnotation(params);
        this.amount = validateRequirementAmount(params);
        this.params = params;
    }

    private void validateParametersFromAnnotation(String[] params) {
        RequirementParams annotation = getClass().getAnnotation(RequirementParams.class);
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
            double amount = Double.parseDouble(params[params.length -1]);
            if (amount > 0) {
                return amount;
            } else {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid amount. Must be a zero-inclusive positive value");
        }
    }

    @Contract(pure = true)
    public static RequirementParams getParameterInfo(@NotNull Class<? extends Requirement> clazz) {
        return clazz.getAnnotation(RequirementParams.class);
    }

    public String getParameterDescription() {
        RequirementParams annotation = getClass().getAnnotation(RequirementParams.class);
        return annotation != null ? annotation.usage() : "No description available";
    }
}
