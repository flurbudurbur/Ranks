package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.AbstractRequirementTest;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.requirement.RequirementFactory;
import dev.flur.ranks.requirement.RequirementRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests for the MoneyRequirement class.
 */
@DisplayName("Money Requirement Tests")
public class MoneyRequirementTest extends AbstractRequirementTest {

    @Test
    @DisplayName("Player balance higher than required should pass")
    void playerBalanceHigherThanRequiredShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("money");
        assertNotNull(info, "Money requirement should be registered");

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("money 500");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player with balance 1000.0 should meet requirement of 500.0");
    }

    @Test
    @DisplayName("Player balance equal to required should pass")
    void playerBalanceEqualToRequiredShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("money");
        assertNotNull(info, "Money requirement should be registered");

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("money 1000");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player with balance 1000.0 should meet requirement of 1000.0");
    }

    @Test
    @DisplayName("Player balance meets decimal requirement should pass")
    void playerBalanceMeetsDecimalRequirementShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("money");
        assertNotNull(info, "Money requirement should be registered");

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("money 999.99");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player with balance 1000.0 should meet requirement of 999.99");
    }

    @Test
    @DisplayName("Player balance lower than required should fail")
    void playerBalanceLowerThanRequiredShouldFail() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("money");
        assertNotNull(info, "Money requirement should be registered");

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("money 1500");

        // Verify
        assertFalse(requirement.meetsRequirement(mockPlayer),
                "Player with balance 1000.0 should not meet requirement of 1500.0");
    }

    @Test
    @DisplayName("Negative amount should be invalid")
    void negativeAmountShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("money");
        assertNotNull(info, "Money requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("money -500");
        }, "Negative amount should not be valid");
    }

    @Test
    @DisplayName("Non-numeric amount should be invalid")
    void nonNumericAmountShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("money");
        assertNotNull(info, "Money requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("money abc");
        }, "Non-numeric amount should not be valid");
    }

    @Test
    @DisplayName("Missing parameter should be invalid")
    void missingParameterShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("money");
        assertNotNull(info, "Money requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("money");
        }, "Missing amount parameter should not be valid");
    }

    @Test
    @DisplayName("Too many parameters should be invalid")
    void tooManyParametersShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("money");
        assertNotNull(info, "Money requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("money 1000 2000");
        }, "Too many parameters should not be valid");
    }

    @Test
    @DisplayName("Money requirement should be registered")
    void shouldBeRegistered() {
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("money");
        assertNotNull(info, "Money requirement should be registered");
        assertEquals(MoneyRequirement.class, info.requirementClass(), 
                "Money requirement should be registered with correct class");
    }

    @Test
    @DisplayName("Money requirement should validate parameters")
    void shouldValidateParameters() {
        // Valid parameters
        assertDoesNotThrow(() -> new MoneyRequirement(new String[] { "1000" }), 
                "Valid amount should not throw exception");

        // Valid parameters - decimal
        assertDoesNotThrow(() -> new MoneyRequirement(new String[] { "1000.50" }), 
                "Valid decimal amount should not throw exception");

        // Invalid parameters - missing
        assertThrows(IllegalArgumentException.class, 
                () -> new MoneyRequirement(new String[] {}), 
                "Missing amount should throw exception");

        // Invalid parameters - too many
        assertThrows(IllegalArgumentException.class, 
                () -> new MoneyRequirement(new String[] { "1000", "2000" }), 
                "Too many parameters should throw exception");

        // Invalid parameters - non-numeric
        assertThrows(IllegalArgumentException.class, 
                () -> new MoneyRequirement(new String[] { "abc" }), 
                "Non-numeric amount should throw exception");
    }

    @Test
    @DisplayName("Money requirement should check player balance correctly")
    void shouldCheckPlayerBalanceCorrectly() {
        // Player balance is 1000.0 (set in AbstractRequirementTest.setUp())

        // Amount requirement is 500.0 (less than player balance) - should succeed
        MoneyRequirement lowerRequirement = new MoneyRequirement(new String[] { "500" });
        assertTrue(lowerRequirement.meetsRequirement(mockPlayer), 
                "Player with balance 1000.0 should meet requirement of 500.0");

        // Amount requirement is 1000.0 (equal to player balance) - should succeed
        MoneyRequirement equalRequirement = new MoneyRequirement(new String[] { "1000" });
        assertTrue(equalRequirement.meetsRequirement(mockPlayer), 
                "Player with balance 1000.0 should meet requirement of 1000.0");

        // Amount requirement is 1500.0 (greater than player balance) - should fail
        MoneyRequirement higherRequirement = new MoneyRequirement(new String[] { "1500" });
        assertFalse(higherRequirement.meetsRequirement(mockPlayer), 
                "Player with balance 1000.0 should not meet requirement of 1500.0");

        // Test with different balance
        when(mockEconomy.getBalance(any(org.bukkit.OfflinePlayer.class))).thenReturn(Double.valueOf(2000.0));
        assertTrue(higherRequirement.meetsRequirement(mockPlayer), 
                "Player with balance 2000.0 should meet requirement of 1500.0");
    }
}
