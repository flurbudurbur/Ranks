package dev.flur.ranks.requirement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Factory class for creating requirement instances from string representations.
 * <p>
 * This factory works with the {@link RequirementRegistry} to dynamically create
 * requirement instances based on registered requirement types.
 * </p>
 *
 * @see RequirementRegistry
 * @see AnnotatedRequirement
 * @since 1.0
 */
public class RequirementFactory {

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
    public static Requirement createRequirement(@NotNull String input) {
        String[] token = input.split("\\s+");
        if (token.length == 0) throw new IllegalArgumentException("Invalid requirement input: " + input);

        String key = token[0].toLowerCase();
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName(key);
        if (info == null) {
            throw new IllegalArgumentException("Invalid requirement type: " + key);
        }

        String[] params = new String[token.length - 1];
        System.arraycopy(token, 1, params, 0, params.length);

        return info.constructor().apply(params);
    }

    /**
     * Gets the name of a requirement from its class.
     * <p>
     * This method looks up the requirement in the registry to find its registered name.
     * </p>
     *
     * @param requirement the requirement instance
     * @return The name of the requirement, or null if not found in the registry
     */
    @Nullable
    public static String getRequirementName(@NotNull Requirement requirement) {
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromClass(requirement.getClass());
        return info != null ? info.name() : null;
    }
}
