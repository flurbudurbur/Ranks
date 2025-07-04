package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.AbstractRequirementTest;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.requirement.RequirementFactory;
import dev.flur.ranks.requirement.RequirementRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the XpLevelRequirement class.
 */
@DisplayName("XP Level Requirement Tests")
public class XpLevelRequirementTest extends AbstractRequirementTest {

    @Test
    @DisplayName("Player level higher than required should pass")
    void playerLevelHigherThanRequiredShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("xp-level");
        assertNotNull(info, "XP Level requirement should be registered");

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("xp-level 5");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player with level 10 should meet requirement of level 5");
    }

    @Test
    @DisplayName("Player level equal to required should pass")
    void playerLevelEqualToRequiredShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("xp-level");
        assertNotNull(info, "XP Level requirement should be registered");

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("xp-level 10");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player with level 10 should meet requirement of level 10");
    }

    @Test
    @DisplayName("Player level lower than required should fail")
    void playerLevelLowerThanRequiredShouldFail() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("xp-level");
        assertNotNull(info, "XP Level requirement should be registered");

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("xp-level 15");

        // Verify
        assertFalse(requirement.meetsRequirement(mockPlayer),
                "Player with level 10 should not meet requirement of level 15");
    }

    @Test
    @DisplayName("Negative level should be invalid")
    void negativeLevelShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("xp-level");
        assertNotNull(info, "XP Level requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("xp-level -5");
        }, "Negative level should not be valid");
    }

    @Test
    @DisplayName("Non-numeric level should be invalid")
    void nonNumericLevelShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("xp-level");
        assertNotNull(info, "XP Level requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("xp-level abc");
        }, "Non-numeric level should not be valid");
    }

    @Test
    @DisplayName("Missing level parameter should be invalid")
    void missingLevelParameterShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("xp-level");
        assertNotNull(info, "XP Level requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("xp-level");
        }, "Missing level parameter should not be valid");
    }

    @Test
    @DisplayName("Too many parameters should be invalid")
    void tooManyParametersShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("xp-level");
        assertNotNull(info, "XP Level requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("xp-level 10 20");
        }, "Too many parameters should not be valid");
    }

    @Test
    @DisplayName("XP Level requirement should be registered")
    void shouldBeRegistered() {
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("xp-level");
        assertNotNull(info, "XP Level requirement should be registered");
        assertEquals(XpLevelRequirement.class, info.requirementClass(), 
                "XP Level requirement should be registered with correct class");
    }

    @Test
    @DisplayName("XP Level requirement should validate parameters")
    void shouldValidateParameters() {
        // Valid parameters
        assertDoesNotThrow(() -> new XpLevelRequirement(new String[] { "10" }), 
                "Valid level should not throw exception");

        // Invalid parameters - missing
        assertThrows(IllegalArgumentException.class, 
                () -> new XpLevelRequirement(new String[] {}), 
                "Missing level should throw exception");

        // Invalid parameters - too many
        assertThrows(IllegalArgumentException.class, 
                () -> new XpLevelRequirement(new String[] { "10", "20" }), 
                "Too many parameters should throw exception");

        // Invalid parameters - non-numeric
        assertThrows(IllegalArgumentException.class, 
                () -> new XpLevelRequirement(new String[] { "abc" }), 
                "Non-numeric level should throw exception");
    }

    @Test
    @DisplayName("XP Level requirement should check player level correctly")
    void shouldCheckPlayerLevelCorrectly() {
        // Player level is 10 (set in AbstractRequirementTest.setUp())

        // Level requirement is 5 (less than player level) - should succeed
        XpLevelRequirement lowerRequirement = new XpLevelRequirement(new String[] { "5" });
        assertTrue(lowerRequirement.meetsRequirement(mockPlayer), 
                "Player with level 10 should meet requirement of level 5");

        // Level requirement is 10 (equal to player level) - should succeed
        XpLevelRequirement equalRequirement = new XpLevelRequirement(new String[] { "10" });
        assertTrue(equalRequirement.meetsRequirement(mockPlayer), 
                "Player with level 10 should meet requirement of level 10");

        // Level requirement is 15 (greater than player level) - should fail
        XpLevelRequirement higherRequirement = new XpLevelRequirement(new String[] { "15" });
        assertFalse(higherRequirement.meetsRequirement(mockPlayer), 
                "Player with level 10 should not meet requirement of level 15");
    }
}
