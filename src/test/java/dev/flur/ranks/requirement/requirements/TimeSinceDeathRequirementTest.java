package dev.flur.ranks.requirement.requirements;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimeSinceDeathRequirementTest {

    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        // Create mocks
        mockPlayer = mock(Player.class);
        // Mock the getPlayer() method to return itself
        when(mockPlayer.getPlayer()).thenReturn(mockPlayer);
    }

    @Test
    void testConstructor_ValidFormat() {
        // Arrange
        String[] params = {"m60"}; // 60 minutes

        // Act
        TimeSinceDeathRequirement requirement = new TimeSinceDeathRequirement(params);

        // Assert - We'll verify indirectly through meetsRequirement in other tests
        assertNotNull(requirement);
    }

    @Test
    void testConstructor_ComplexFormat() {
        // Arrange
        String[] params = {"M1w2d3h4m5s6"}; // Complex format with all units

        // Act
        TimeSinceDeathRequirement requirement = new TimeSinceDeathRequirement(params);

        // Assert
        assertNotNull(requirement);
    }

    @Test
    void testConstructor_InvalidFormat() {
        // Arrange
        String[] params = {"60"}; // Just a number, no units

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new TimeSinceDeathRequirement(params));
    }

    @ParameterizedTest
    @ValueSource(strings = {"m-10", "invalid", ""})
    void testConstructor_InvalidValues(String invalidValue) {
        // Arrange
        String[] params = {invalidValue};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new TimeSinceDeathRequirement(params));
    }

    @Test
    void testConstructor_TooManyParams() {
        // Arrange
        String[] params = {"m60", "extra"};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new TimeSinceDeathRequirement(params));
    }

    @Test
    void testConstructor_TooFewParams() {
        // Arrange
        String[] params = {};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new TimeSinceDeathRequirement(params));
    }

    @Test
    void testMeetsRequirement_EnoughTimeSinceDeath() {
        // Arrange
        String[] params = {"m60"}; // 60 minutes = 72000 ticks (60 * 60 * 20)
        TimeSinceDeathRequirement requirement = new TimeSinceDeathRequirement(params);

        // Mock the player's time since death to be 90 minutes (108000 ticks)
        when(mockPlayer.getStatistic(Statistic.TIME_SINCE_DEATH)).thenReturn(108000);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertTrue(result);
        verify(mockPlayer).getStatistic(Statistic.TIME_SINCE_DEATH);
    }

    @Test
    void testMeetsRequirement_NotEnoughTimeSinceDeath() {
        // Arrange
        String[] params = {"m60"}; // 60 minutes = 72000 ticks
        TimeSinceDeathRequirement requirement = new TimeSinceDeathRequirement(params);

        // Mock the player's time since death to be 30 minutes (36000 ticks)
        when(mockPlayer.getStatistic(Statistic.TIME_SINCE_DEATH)).thenReturn(36000);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertFalse(result);
        verify(mockPlayer).getStatistic(Statistic.TIME_SINCE_DEATH);
    }

    @Test
    void testMeetsRequirement_ExactAmount() {
        // Arrange
        String[] params = {"m60"}; // 60 minutes = 72000 ticks
        TimeSinceDeathRequirement requirement = new TimeSinceDeathRequirement(params);

        // Mock the player's time since death to be exactly 60 minutes (72000 ticks)
        when(mockPlayer.getStatistic(Statistic.TIME_SINCE_DEATH)).thenReturn(72000);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertTrue(result);
        verify(mockPlayer).getStatistic(Statistic.TIME_SINCE_DEATH);
    }

    @Test
    void testMeetsRequirement_VeryLargeRequirement() {
        // Arrange - Create a requirement that exceeds Integer.MAX_VALUE
        String[] params = {"M12"}; // 12 months, which should be a very large number of ticks
        TimeSinceDeathRequirement requirement = new TimeSinceDeathRequirement(params);

        // Mock the player to have less time since death than required
        // Use a value smaller than 12 months worth of ticks
        when(mockPlayer.getStatistic(Statistic.TIME_SINCE_DEATH)).thenReturn(300_000_000); // ~5 months worth


        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert - The implementation should handle this case
        assertFalse(result);

        // Now test with max value
        when(mockPlayer.getStatistic(Statistic.TIME_SINCE_DEATH)).thenReturn(Integer.MAX_VALUE);
        assertTrue(requirement.meetsRequirement(mockPlayer));
    }

    @Test
    void testToString() {
        // Arrange
        String[] params = {"m60"};
        TimeSinceDeathRequirement requirement = new TimeSinceDeathRequirement(params);

        // Act
        String result = requirement.toString();

        // Assert
        assertTrue(result.contains("time-since-death"));
    }
}
