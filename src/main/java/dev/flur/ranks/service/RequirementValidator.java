package dev.flur.ranks.service;

import dev.flur.ranks.requirement.Requirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Service interface for validating rank requirements.
 */
public interface RequirementValidator {

    /**
     * Checks if a player meets all requirements for a rank.
     *
     * @param player       The player to check
     * @param requirements The list of requirements to check
     * @return True if the player meets all requirements, false otherwise
     */
    boolean meetsAllRequirements(@NotNull Player player, @NotNull List<Requirement> requirements);

    /**
     * Gets a list of requirements that a player does not meet.
     *
     * @param player       The player to check
     * @param requirements The list of requirements to check
     * @return A list of requirements that the player does not meet
     */
    @NotNull
    List<Requirement> getUnmetRequirements(@NotNull Player player, @NotNull List<Requirement> requirements);

    /**
     * Gets a map of requirements with their status (met or not met).
     *
     * @param player       The player to check
     * @param requirements The list of requirements to check
     * @return A map of requirements to their status (true if met, false if not met)
     */
    @NotNull
    Map<Requirement, Boolean> getRequirementStatus(@NotNull Player player, @NotNull List<Requirement> requirements);

    /**
     * Gets a formatted description of a requirement.
     *
     * @param requirement The requirement to describe
     * @return A formatted description of the requirement
     */
    @NotNull
    String getRequirementDescription(@NotNull Requirement requirement);
}