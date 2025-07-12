package dev.flur.ranks.requirement.requirements;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeathsRequirementTest {

    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        // Create mocks
        mockPlayer = mock(Player.class);
    }

    @Test
    void testConstructor_ValidAmount() {
        // Arrange
        String[] params = {"100"};
        when(mockPlayer.getStatistic(Statistic.DEATHS)).thenReturn(100); // Exact amount

        // Act
        DeathsRequirement requirement = new DeathsRequirement(params);

        // Assert - Verify indirectly through meetsRequirement
        assertTrue(requirement.meetsRequirement(mockPlayer));
        
        // Also verify with more deaths
        when(mockPlayer.getStatistic(Statistic.DEATHS)).thenReturn(150);
        assertTrue(requirement.meetsRequirement(mockPlayer));
        
        // And verify with fewer deaths
        when(mockPlayer.getStatistic(Statistic.DEATHS)).thenReturn(99);
        assertFalse(requirement.meetsRequirement(mockPlayer));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-10", "abc", ""})
    void testConstructor_InvalidAmount(String invalidAmount) {
        // Arrange
        String[] params = {invalidAmount};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new DeathsRequirement(params));
    }

    @Test
    void testConstructor_TooManyParams() {
        // Arrange
        String[] params = {"param1", "100"};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new DeathsRequirement(params));
    }

    @Test
    void testConstructor_TooFewParams() {
        // Arrange
        String[] params = {};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new DeathsRequirement(params));
    }

    @Test
    void testMeetsRequirement_EnoughDeaths() {
        // Arrange
        String[] params = {"100"};
        DeathsRequirement requirement = new DeathsRequirement(params);
        when(mockPlayer.getStatistic(Statistic.DEATHS)).thenReturn(150);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertTrue(result);
        verify(mockPlayer).getStatistic(Statistic.DEATHS);
    }

    @Test
    void testMeetsRequirement_NotEnoughDeaths() {
        // Arrange
        String[] params = {"100"};
        DeathsRequirement requirement = new DeathsRequirement(params);
        when(mockPlayer.getStatistic(Statistic.DEATHS)).thenReturn(50);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertFalse(result);
        verify(mockPlayer).getStatistic(Statistic.DEATHS);
    }

    @Test
    void testMeetsRequirement_ExactAmount() {
        // Arrange
        String[] params = {"100"};
        DeathsRequirement requirement = new DeathsRequirement(params);
        when(mockPlayer.getStatistic(Statistic.DEATHS)).thenReturn(100);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertTrue(result);
        verify(mockPlayer).getStatistic(Statistic.DEATHS);
    }

    @Test
    void testToString() {
        // Arrange
        String[] params = {"100"};
        DeathsRequirement requirement = new DeathsRequirement(params);

        // Act
        String result = requirement.toString();

        // Assert
        assertTrue(result.contains("100"));
        assertTrue(result.contains("deaths"));
    }
}