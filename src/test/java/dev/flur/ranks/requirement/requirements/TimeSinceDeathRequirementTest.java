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
 * Tests for the TimeSinceDeathRequirement class.
 */
@DisplayName("Time Since Death Requirement Tests")
public class TimeSinceDeathRequirementTest extends AbstractRequirementTest {

    @Test
    @DisplayName("Player alive longer than required should pass")
    void playerAliveLongerThanRequiredShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("time-since-death");
        assertNotNull(info, "Time Since Death requirement should be registered");
        when(mockPlayerEntity.getStatistic(Statistic.TIME_SINCE_DEATH)).thenReturn(3600 * 1000);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("time-since-death h1");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player alive for 3600 seconds should meet requirement of 1 hour");
    }

    @Test
    @DisplayName("Player alive exactly the required time should pass")
    void playerAliveExactlyRequiredTimeShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("time-since-death");
        assertNotNull(info, "Time Since Death requirement should be registered");
        when(mockPlayerEntity.getStatistic(Statistic.TIME_SINCE_DEATH)).thenReturn(3600 * 1000);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("time-since-death m1");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player alive for 3600 seconds should meet requirement of 3600 seconds");
    }

    @Test
    @DisplayName("Player alive longer than required with multiple units should pass")
    void playerAliveLongerThanRequiredWithMultipleUnitsShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("time-since-death");
        assertNotNull(info, "Time Since Death requirement should be registered");
        when(mockPlayerEntity.getStatistic(Statistic.TIME_SINCE_DEATH)).thenReturn(3600 * 1000);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("time-since-death m30 s30");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player alive for 3600 seconds should meet requirement of 30 minutes and 30 seconds");
    }

    @Test
    @DisplayName("Player alive less than required should fail")
    void playerAliveLessThanRequiredShouldFail() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("time-since-death");
        assertNotNull(info, "Time Since Death requirement should be registered");
        when(mockPlayerEntity.getStatistic(Statistic.TIME_SINCE_DEATH)).thenReturn(3600 * 1000);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("time-since-death h2");

        // Verify
        assertFalse(requirement.meetsRequirement(mockPlayer),
                "Player alive for 3600 seconds should not meet requirement of 2 hours");
    }

    @Test
    @DisplayName("Invalid format with no units should be invalid")
    void invalidFormatWithNoUnitsShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("time-since-death");
        assertNotNull(info, "Time Since Death requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("time-since-death 3600");
        }, "Invalid format (no units) should not be valid");
    }

    @Test
    @DisplayName("Invalid format with invalid unit should be invalid")
    void invalidFormatWithInvalidUnitShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("time-since-death");
        assertNotNull(info, "Time Since Death requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("time-since-death x3600");
        }, "Invalid format (invalid unit) should not be valid");
    }

    @Test
    @DisplayName("Invalid format with negative value should be invalid")
    void invalidFormatWithNegativeValueShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("time-since-death");
        assertNotNull(info, "Time Since Death requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("time-since-death h-1");
        }, "Invalid format (negative value) should not be valid");
    }

    @Test
    @DisplayName("Missing parameter should be invalid")
    void missingParameterShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("time-since-death");
        assertNotNull(info, "Time Since Death requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("time-since-death");
        }, "Missing parameter should not be valid");
    }

    @Test
    @DisplayName("Time Since Death requirement should be registered")
    void shouldBeRegistered() {
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("time-since-death");
        assertNotNull(info, "Time Since Death requirement should be registered");
        assertEquals(TimeSinceDeathRequirement.class, info.requirementClass(), 
                "Time Since Death requirement should be registered with correct class");
    }

    @Test
    @DisplayName("Time Since Death requirement should validate parameters")
    void shouldValidateParameters() {
        // Valid parameters - single unit
        assertDoesNotThrow(() -> new TimeSinceDeathRequirement(new String[] { "h1" }), 
                "Valid duration with single unit should not throw exception");

        // Valid parameters - multiple units
        assertDoesNotThrow(() -> new TimeSinceDeathRequirement(new String[] { "h1 m30 s30" }), 
                "Valid duration with multiple units should not throw exception");

        // Valid parameters - all units
        assertDoesNotThrow(() -> new TimeSinceDeathRequirement(new String[] { "M1 w1 d1 h1 m1 s1" }), 
                "Valid duration with all units should not throw exception");

        // Invalid parameters - missing
        assertThrows(IllegalArgumentException.class, 
                () -> new TimeSinceDeathRequirement(new String[] {}), 
                "Missing duration should throw exception");

        // Invalid parameters - invalid format (no units)
        assertThrows(IllegalArgumentException.class, 
                () -> new TimeSinceDeathRequirement(new String[] { "3600" }), 
                "Invalid format (no units) should throw exception");

        // Invalid parameters - invalid format (invalid unit)
        assertThrows(IllegalArgumentException.class, 
                () -> new TimeSinceDeathRequirement(new String[] { "x3600" }), 
                "Invalid format (invalid unit) should throw exception");

        // Invalid parameters - invalid format (out of range)
        assertThrows(IllegalArgumentException.class, 
                () -> new TimeSinceDeathRequirement(new String[] { "w4" }), 
                "Invalid format (week out of range) should throw exception");

        // Invalid parameters - invalid format (out of range)
        assertThrows(IllegalArgumentException.class, 
                () -> new TimeSinceDeathRequirement(new String[] { "d7" }), 
                "Invalid format (day out of range) should throw exception");

        // Invalid parameters - invalid format (out of range)
        assertThrows(IllegalArgumentException.class, 
                () -> new TimeSinceDeathRequirement(new String[] { "h24" }), 
                "Invalid format (hour out of range) should throw exception");

        // Invalid parameters - invalid format (out of range)
        assertThrows(IllegalArgumentException.class, 
                () -> new TimeSinceDeathRequirement(new String[] { "m60" }), 
                "Invalid format (minute out of range) should throw exception");

        // Invalid parameters - invalid format (out of range)
        assertThrows(IllegalArgumentException.class, 
                () -> new TimeSinceDeathRequirement(new String[] { "s60" }), 
                "Invalid format (second out of range) should throw exception");
    }

    @Test
    @DisplayName("Time Since Death requirement should check time since death correctly")
    void shouldCheckTimeSinceDeathCorrectly() {
        // Setup mock statistics - 3600 seconds (1 hour) since death
        when(mockPlayerEntity.getStatistic(Statistic.TIME_SINCE_DEATH)).thenReturn(3600 * 1000);

        // Time requirement is 30 minutes (less than player's time) - should succeed
        TimeSinceDeathRequirement lowerRequirement = new TimeSinceDeathRequirement(new String[] { "m30" });
        assertTrue(lowerRequirement.meetsRequirement(mockPlayer), 
                "Player alive for 1 hour should meet requirement of 30 minutes");

        // Time requirement is 1 hour (equal to player's time) - should succeed
        TimeSinceDeathRequirement equalRequirement = new TimeSinceDeathRequirement(new String[] { "h1" });
        assertTrue(equalRequirement.meetsRequirement(mockPlayer), 
                "Player alive for 1 hour should meet requirement of 1 hour");

        // Time requirement is 2 hours (greater than player's time) - should fail
        TimeSinceDeathRequirement higherRequirement = new TimeSinceDeathRequirement(new String[] { "h2" });
        assertFalse(higherRequirement.meetsRequirement(mockPlayer), 
                "Player alive for 1 hour should not meet requirement of 2 hours");

        // Time requirement with multiple units - should succeed
        TimeSinceDeathRequirement multipleUnitsRequirement = new TimeSinceDeathRequirement(new String[] { "m30 s30" });
        assertTrue(multipleUnitsRequirement.meetsRequirement(mockPlayer), 
                "Player alive for 1 hour should meet requirement of 30 minutes and 30 seconds");

        // Test with different time since death
        when(mockPlayerEntity.getStatistic(Statistic.TIME_SINCE_DEATH)).thenReturn(7200 * 1000);
        assertTrue(higherRequirement.meetsRequirement(mockPlayer), 
                "Player alive for 2 hours should meet requirement of 2 hours");
    }

    @Test
    @DisplayName("Time Since Death requirement should convert duration correctly")
    void shouldConvertDurationCorrectly() {
        // Test months conversion
        TimeSinceDeathRequirement monthsRequirement = new TimeSinceDeathRequirement(new String[] { "M1" });
        long expectedMonthsMillis = 30L * 24 * 60 * 60 * 1000;
        assertEquals(expectedMonthsMillis, getTimeSinceDeathField(monthsRequirement), 
                "1 month should be converted to " + expectedMonthsMillis + " milliseconds");

        // Test weeks conversion
        TimeSinceDeathRequirement weeksRequirement = new TimeSinceDeathRequirement(new String[] { "w1" });
        long expectedWeeksMillis = 7L * 24 * 60 * 60 * 1000;
        assertEquals(expectedWeeksMillis, getTimeSinceDeathField(weeksRequirement), 
                "1 week should be converted to " + expectedWeeksMillis + " milliseconds");

        // Test days conversion
        TimeSinceDeathRequirement daysRequirement = new TimeSinceDeathRequirement(new String[] { "d1" });
        long expectedDaysMillis = 24L * 60 * 60 * 1000;
        assertEquals(expectedDaysMillis, getTimeSinceDeathField(daysRequirement), 
                "1 day should be converted to " + expectedDaysMillis + " milliseconds");

        // Test hours conversion
        TimeSinceDeathRequirement hoursRequirement = new TimeSinceDeathRequirement(new String[] { "h1" });
        long expectedHoursMillis = 60L * 60 * 1000;
        assertEquals(expectedHoursMillis, getTimeSinceDeathField(hoursRequirement), 
                "1 hour should be converted to " + expectedHoursMillis + " milliseconds");

        // Test minutes conversion
        TimeSinceDeathRequirement minutesRequirement = new TimeSinceDeathRequirement(new String[] { "m1" });
        long expectedMinutesMillis = 60L * 1000;
        assertEquals(expectedMinutesMillis, getTimeSinceDeathField(minutesRequirement), 
                "1 minute should be converted to " + expectedMinutesMillis + " milliseconds");

        // Test seconds conversion
        TimeSinceDeathRequirement secondsRequirement = new TimeSinceDeathRequirement(new String[] { "s1" });
        long expectedSecondsMillis = 1000L;
        assertEquals(expectedSecondsMillis, getTimeSinceDeathField(secondsRequirement), 
                "1 second should be converted to " + expectedSecondsMillis + " milliseconds");

        // Test multiple units
        TimeSinceDeathRequirement multipleUnitsRequirement = new TimeSinceDeathRequirement(new String[] { "h1 m30 s30" });
        long expectedMultipleUnitsMillis = 60L * 60 * 1000 + 30L * 60 * 1000 + 30L * 1000;
        assertEquals(expectedMultipleUnitsMillis, getTimeSinceDeathField(multipleUnitsRequirement), 
                "1 hour, 30 minutes, and 30 seconds should be converted to " + expectedMultipleUnitsMillis + " milliseconds");
    }

    /**
     * Helper method to access the private timeSinceDeath field for testing
     */
    private long getTimeSinceDeathField(TimeSinceDeathRequirement requirement) {
        try {
            java.lang.reflect.Field field = TimeSinceDeathRequirement.class.getDeclaredField("timeSinceDeath");
            field.setAccessible(true);
            return (long) field.get(requirement);
        } catch (Exception e) {
            fail("Failed to access timeSinceDeath field: " + e.getMessage());
            return -1;
        }
    }
}
