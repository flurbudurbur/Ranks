package dev.flur.ranks.requirement;

import dev.flur.ranks.requirement.records.RequirementRecord;
import dev.flur.ranks.service.services.DefaultRequirementRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Factory class for creating requirement instances from string representations.
 * <p>
 * This factory works with the {@link DefaultRequirementRegistry} to dynamically create
 * requirement instances based on registered requirement types.
 * </p>
 *
 * @see DefaultRequirementRegistry
 * @see AnnotatedRequirement
 * @since 1.0
 */
public class RequirementFactory {

    private final DefaultRequirementRegistry registry;

    /**
     * Creates a new RequirementFactory with the specified registry.
     *
     * @param registry the requirement registry to use
     */
    public RequirementFactory(@NotNull DefaultRequirementRegistry registry) {
        this.registry = registry;
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
        if (input.trim().isEmpty()) throw new IllegalArgumentException("Invalid requirement input: " + input);
        String[] token = input.split("\\s+");

        String key = token[0].toLowerCase();
        RequirementRecord info = registry.fromName(key);
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
    public String getRequirementName(@NotNull Requirement requirement) {
        RequirementRecord info = registry.fromClass(requirement.getClass());
        return info != null ? info.name() : null;
    }
}
