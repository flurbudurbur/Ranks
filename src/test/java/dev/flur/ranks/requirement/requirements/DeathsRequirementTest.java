package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.AbstractRequirementTest;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.requirement.RequirementFactory;
import dev.flur.ranks.requirement.RequirementRegistry;
import org.bukkit.Statistic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Tests for the DeathsRequirement class.
 */
@DisplayName("Deaths Requirement Tests")
public class DeathsRequirementTest extends AbstractRequirementTest {

    @Test
    @DisplayName("Player has more deaths than required should pass")
    void playerHasMoreDeathsThanRequiredShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("deaths");
        assertNotNull(info, "Deaths requirement should be registered");
        when(mockPlayer.getStatistic(Statistic.DEATHS)).thenReturn(10);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("deaths 5");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player with 10 deaths should meet requirement of 5 deaths");
    }

    @Test
    @DisplayName("Player has exactly the required number of deaths should pass")
    void playerHasExactlyRequiredNumberOfDeathsShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("deaths");
        assertNotNull(info, "Deaths requirement should be registered");
        when(mockPlayer.getStatistic(Statistic.DEATHS)).thenReturn(10);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("deaths 10");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player with 10 deaths should meet requirement of 10 deaths");
    }

    @Test
    @DisplayName("Player has fewer deaths than required should fail")
    void playerHasFewerDeathsThanRequiredShouldFail() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("deaths");
        assertNotNull(info, "Deaths requirement should be registered");
        when(mockPlayer.getStatistic(Statistic.DEATHS)).thenReturn(10);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("deaths 15");

        // Verify
        assertFalse(requirement.meetsRequirement(mockPlayer),
                "Player with 10 deaths should not meet requirement of 15 deaths");
    }

    @Test
    @DisplayName("Non-numeric deaths should throw exception")
    void nonNumericDeathsShouldThrowException() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("deaths");
        assertNotNull(info, "Deaths requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("deaths abc");
        }, "Non-numeric deaths should throw exception");
    }

    @Test
    @DisplayName("Missing parameter should throw exception")
    void missingParameterShouldThrowException() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("deaths");
        assertNotNull(info, "Deaths requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("deaths");
        }, "Missing deaths parameter should throw exception");
    }

    @Test
    @DisplayName("Deaths requirement should be registered")
    void shouldBeRegistered() {
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("deaths");
        assertNotNull(info, "Deaths requirement should be registered");
        assertEquals(DeathsRequirement.class, info.requirementClass(), 
                "Deaths requirement should be registered with correct class");
    }

    @Test
    @DisplayName("Deaths requirement should handle parameters correctly")
    void shouldHandleParametersCorrectly() {
        // Setup mock statistics to ensure requirement is met
        when(mockPlayer.getStatistic(Statistic.DEATHS)).thenReturn(20);

        // Valid parameters
        DeathsRequirement validRequirement = new DeathsRequirement(new String[] { "10" });
        assertTrue(validRequirement.meetsRequirement(mockPlayer), 
                "Valid deaths parameter should be parsed correctly");

        // Invalid parameters - non-numeric
        assertThrows(IllegalArgumentException.class, 
                () -> new DeathsRequirement(new String[] { "abc" }), 
                "Invalid deaths parameter should throw exception");

        // Invalid parameters - missing
        assertThrows(IllegalArgumentException.class, 
                () -> new DeathsRequirement(new String[] {}), 
                "Missing deaths parameter should throw exception");

        // Invalid parameters - negative
        assertThrows(IllegalArgumentException.class, 
                () -> new DeathsRequirement(new String[] { "-5" }), 
                "Negative deaths parameter should throw exception");
    }

    @Test
    @DisplayName("Deaths requirement should check player deaths correctly")
    void shouldCheckPlayerDeathsCorrectly() {
        // Setup mock statistics
        when(mockPlayer.getStatistic(Statistic.DEATHS)).thenReturn(10);

        // Deaths requirement is 5 (less than player deaths) - should succeed
        DeathsRequirement lowerRequirement = new DeathsRequirement(new String[] { "5" });
        assertTrue(lowerRequirement.meetsRequirement(mockPlayer), 
                "Player with 10 deaths should meet requirement of 5 deaths");

        // Deaths requirement is 10 (equal to player deaths) - should succeed
        DeathsRequirement equalRequirement = new DeathsRequirement(new String[] { "10" });
        assertTrue(equalRequirement.meetsRequirement(mockPlayer), 
                "Player with 10 deaths should meet requirement of 10 deaths");

        // Deaths requirement is 15 (greater than player deaths) - should fail
        DeathsRequirement higherRequirement = new DeathsRequirement(new String[] { "15" });
        assertFalse(higherRequirement.meetsRequirement(mockPlayer), 
                "Player with 10 deaths should not meet requirement of 15 deaths");

        // Test with different deaths count
        when(mockPlayer.getStatistic(Statistic.DEATHS)).thenReturn(20);
        assertTrue(higherRequirement.meetsRequirement(mockPlayer), 
                "Player with 20 deaths should meet requirement of 15 deaths");
    }
}
