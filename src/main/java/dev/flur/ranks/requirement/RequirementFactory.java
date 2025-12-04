package dev.flur.ranks.requirement;

import dev.flur.ranks.service.RequirementService;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class for creating requirement instances from string representations.
 * <p>
 * This factory works with the {@link RequirementService} to create
 * requirement instances based on registered requirement types.
 * </p>
 *
 * @see RequirementService
 * @see RequirementType
 */
@SuppressWarnings("ClassCanBeRecord")
public class RequirementFactory {

    private final RequirementService requirementService;

    /**
     * Creates a new RequirementFactory with the specified service.
     *
     * @param requirementService the requirement service to use
     */
    public RequirementFactory(@NotNull RequirementService requirementService) {
        this.requirementService = requirementService;
    }

    /**
     * Creates a requirement instance from a string representation.
     * <p>
     * The input string should be in the format: "requirement-name param1 param2 ..."
     * where the first token is the requirement name and subsequent tokens are parameters.
     * </p>
     *
     * @param input the string representation of the requirement
     * @return a new requirement instance
     * @throws IllegalArgumentException if the input is invalid or the requirement type is not found
     */
    @NotNull
    public Requirement createRequirement(@NotNull String input) {
        if (input.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid requirement input: empty string");
        }

        String[] tokens = input.split("\\s+");
        String key = tokens[0].toLowerCase();

        String[] params = new String[tokens.length - 1];
        System.arraycopy(tokens, 1, params, 0, params.length);

        return requirementService.create(key, params);
    }

    /**
     * Gets a human-readable name for a requirement instance.
     *
     * @param requirement the requirement to get the name for
     * @return the requirement name, or null if not found
     */
    @org.jetbrains.annotations.Nullable
    public String getRequirementName(@NotNull Requirement requirement) {
        // Try to get the class name and format it nicely
        String className = requirement.getClass().getSimpleName();
        // Remove "Requirement" suffix if present
        if (className.endsWith("Requirement")) {
            className = className.substring(0, className.length() - "Requirement".length());
        }
        // Convert CamelCase to readable format
        return formatCamelCase(className);
    }

    /**
     * Formats a CamelCase string to a readable format.
     * Example: "BlockBreak" -> "Block Break"
     */
    private String formatCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) {
                result.append(' ');
            }
            result.append(c);
        }
        return result.toString();
    }
}
