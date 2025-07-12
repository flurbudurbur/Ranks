package dev.flur.ranks.requirement.requirements;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class XpLevelRequirementTest {

    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        // Create mocks
        mockPlayer = mock(Player.class);
    }

    @Test
    void testConstructor_ValidAmount() {
        // Arrange
        String[] params = {"30"};
        when(mockPlayer.getLevel()).thenReturn(30); // Exact amount

        // Act
        XpLevelRequirement requirement = new XpLevelRequirement(params);

        // Assert - Verify indirectly through meetsRequirement
        assertTrue(requirement.meetsRequirement(mockPlayer));
        
        // Also verify with more levels
        when(mockPlayer.getLevel()).thenReturn(35);
        assertTrue(requirement.meetsRequirement(mockPlayer));
        
        // And verify with fewer levels
        when(mockPlayer.getLevel()).thenReturn(25);
        assertFalse(requirement.meetsRequirement(mockPlayer));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-10", "abc", ""})
    void testConstructor_InvalidAmount(String invalidAmount) {
        // Arrange
        String[] params = {invalidAmount};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new XpLevelRequirement(params));
    }

    @Test
    void testConstructor_ZeroAmount() {
        // Arrange
        String[] params = {"0"};
        
        // Act
        XpLevelRequirement requirement = new XpLevelRequirement(params);
        
        // Assert - Any level should meet a zero requirement
        when(mockPlayer.getLevel()).thenReturn(0);
        assertTrue(requirement.meetsRequirement(mockPlayer));
    }

    @Test
    void testConstructor_TooManyParams() {
        // Arrange
        String[] params = {"param1", "30"};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new XpLevelRequirement(params));
    }

    @Test
    void testConstructor_TooFewParams() {
        // Arrange
        String[] params = {};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new XpLevelRequirement(params));
    }

    @Test
    void testMeetsRequirement_EnoughLevels() {
        // Arrange
        String[] params = {"30"};
        XpLevelRequirement requirement = new XpLevelRequirement(params);
        when(mockPlayer.getLevel()).thenReturn(40);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertTrue(result);
        verify(mockPlayer).getLevel();
    }

    @Test
    void testMeetsRequirement_NotEnoughLevels() {
        // Arrange
        String[] params = {"30"};
        XpLevelRequirement requirement = new XpLevelRequirement(params);
        when(mockPlayer.getLevel()).thenReturn(20);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertFalse(result);
        verify(mockPlayer).getLevel();
    }

    @Test
    void testMeetsRequirement_ExactAmount() {
        // Arrange
        String[] params = {"30"};
        XpLevelRequirement requirement = new XpLevelRequirement(params);
        when(mockPlayer.getLevel()).thenReturn(30);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertTrue(result);
        verify(mockPlayer).getLevel();
    }

    @Test
    void testToString() {
        // Arrange
        String[] params = {"30"};
        XpLevelRequirement requirement = new XpLevelRequirement(params);

        // Act
        String result = requirement.toString();

        // Assert
        assertTrue(result.contains("30"));
        assertTrue(result.contains("xp-level"));
    }
}