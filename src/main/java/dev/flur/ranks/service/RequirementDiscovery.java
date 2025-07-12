package dev.flur.ranks.service;

import dev.flur.ranks.requirement.Requirement;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Interface for discovering requirements.
 */
public interface RequirementDiscovery {

    /**
     * Registers a requirement class.
     *
     * @param requirementClass The requirement class to register
     */
    void registerRequirement(@NotNull Class<? extends Requirement> requirementClass);

    /**
     * Discovers and registers all requirement classes in the given package.
     *
     * @param packageName The package to scan for requirement classes
     * @return The number of requirement classes discovered
     */
    int discoverRequirements(@NotNull String packageName);

    /**
     * Gets all registered requirement classes.
     *
     * @return A list of all registered requirement classes
     */
    @NotNull
    List<Class<? extends Requirement>> getRegisteredRequirementClasses();

    /**
     * Gets all registered requirement names.
     *
     * @return A list of all registered requirement names
     */
    @NotNull
    List<String> getRegisteredRequirementNames();

    /**
     * Gets all registered requirement information.
     *
     * @return A map of requirement names to requirement information
     */
    @NotNull
    Map<String, dev.flur.ranks.requirement.records.RequirementRecord> getRequirementInfo();
}
