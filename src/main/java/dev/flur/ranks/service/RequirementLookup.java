package dev.flur.ranks.service;

import dev.flur.ranks.requirement.Requirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Interface for looking up requirements.
 */
public interface RequirementLookup {

    /**
     * Gets a requirement class by name.
     *
     * @param name The name of the requirement
     * @return The requirement class, or null if not found
     */
    @Nullable
    Class<? extends Requirement> getRequirementClass(@NotNull String name);

    /**
     * Creates a new requirement instance.
     *
     * @param name   The name of the requirement
     * @param params The parameters for the requirement
     * @return The new requirement instance, or null if the requirement could not be created
     */
    @Nullable
    Requirement createRequirement(@NotNull String name, @NotNull Map<String, String> params);

    /**
     * Creates a new requirement instance.
     *
     * @param name   The name of the requirement
     * @param params The parameters for the requirement as a string
     * @return The new requirement instance, or null if the requirement could not be created
     */
    @Nullable
    Requirement createRequirement(@NotNull String name, @NotNull String params);

    /**
     * Gets the minimum number of parameters required for a requirement.
     *
     * @param name The name of the requirement
     * @return The minimum number of parameters, or -1 if the requirement is not found
     */
    int getMinParams(@NotNull String name);

    /**
     * Gets the maximum number of parameters allowed for a requirement.
     *
     * @param name The name of the requirement
     * @return The maximum number of parameters, or -1 if the requirement is not found
     */
    int getMaxParams(@NotNull String name);

    /**
     * Gets the parameter names for a requirement.
     *
     * @param name The name of the requirement
     * @return The parameter names, or an empty list if the requirement is not found
     */
    @NotNull
    List<String> getParamNames(@NotNull String name);

    /**
     * Checks if a requirement with the given name exists.
     *
     * @param name The name of the requirement
     * @return True if the requirement exists, false otherwise
     */
    boolean hasRequirement(@NotNull String name);
}