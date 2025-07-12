package dev.flur.ranks.requirement.requirements;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlockBreakRequirementTest {

    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        // Create mocks
        mockPlayer = mock(Player.class);
    }

    @Test
    void testConstructor_ValidParams() {
        // Arrange
        String[] params = {"STONE", "100"};
        when(mockPlayer.getStatistic(Statistic.MINE_BLOCK, Material.STONE)).thenReturn(100); // Exact amount

        // Act
        BlockBreakRequirement requirement = new BlockBreakRequirement(params);

        // Assert - Verify indirectly through meetsRequirement
        assertTrue(requirement.meetsRequirement(mockPlayer));
        
        // Also verify with more breaks
        when(mockPlayer.getStatistic(Statistic.MINE_BLOCK, Material.STONE)).thenReturn(150);
        assertTrue(requirement.meetsRequirement(mockPlayer));
        
        // And verify with fewer breaks
        when(mockPlayer.getStatistic(Statistic.MINE_BLOCK, Material.STONE)).thenReturn(99);
        assertFalse(requirement.meetsRequirement(mockPlayer));
    }

    @Test
    void testConstructor_MultipleBlocks() {
        // Arrange
        String[] params = {"STONE", "DIRT", "100"};
        
        // Stone has enough breaks
        when(mockPlayer.getStatistic(Statistic.MINE_BLOCK, Material.STONE)).thenReturn(150);
        // Dirt doesn't have enough breaks
        when(mockPlayer.getStatistic(Statistic.MINE_BLOCK, Material.DIRT)).thenReturn(50);

        // Act
        BlockBreakRequirement requirement = new BlockBreakRequirement(params);

        // Assert - Should fail because one block doesn't meet the requirement
        assertFalse(requirement.meetsRequirement(mockPlayer));
        
        // Now make both blocks have enough breaks
        when(mockPlayer.getStatistic(Statistic.MINE_BLOCK, Material.DIRT)).thenReturn(150);
        assertTrue(requirement.meetsRequirement(mockPlayer));
    }

    @Test
    void testConstructor_InvalidMaterial() {
        // Arrange
        String[] params = {"INVALID_MATERIAL", "100"};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new BlockBreakRequirement(params));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-10", "abc", ""})
    void testConstructor_InvalidAmount(String invalidAmount) {
        // Arrange
        String[] params = {"STONE", invalidAmount};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new BlockBreakRequirement(params));
    }

    @Test
    void testConstructor_TooFewParams() {
        // Arrange
        String[] params = {"STONE"};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new BlockBreakRequirement(params));
    }

    @Test
    void testMeetsRequirement_EnoughBreaks() {
        // Arrange
        String[] params = {"STONE", "100"};
        BlockBreakRequirement requirement = new BlockBreakRequirement(params);
        when(mockPlayer.getStatistic(Statistic.MINE_BLOCK, Material.STONE)).thenReturn(150);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertTrue(result);
        verify(mockPlayer).getStatistic(Statistic.MINE_BLOCK, Material.STONE);
    }

    @Test
    void testMeetsRequirement_NotEnoughBreaks() {
        // Arrange
        String[] params = {"STONE", "100"};
        BlockBreakRequirement requirement = new BlockBreakRequirement(params);
        when(mockPlayer.getStatistic(Statistic.MINE_BLOCK, Material.STONE)).thenReturn(50);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertFalse(result);
        verify(mockPlayer).getStatistic(Statistic.MINE_BLOCK, Material.STONE);
    }

    @Test
    void testMeetsRequirement_ExactAmount() {
        // Arrange
        String[] params = {"STONE", "100"};
        BlockBreakRequirement requirement = new BlockBreakRequirement(params);
        when(mockPlayer.getStatistic(Statistic.MINE_BLOCK, Material.STONE)).thenReturn(100);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertTrue(result);
        verify(mockPlayer).getStatistic(Statistic.MINE_BLOCK, Material.STONE);
    }

    @Test
    void testToString() {
        // Arrange
        String[] params = {"STONE", "DIRT", "100"};
        BlockBreakRequirement requirement = new BlockBreakRequirement(params);

        // Act
        String result = requirement.toString();

        // Assert
        assertTrue(result.contains("100"));
        assertTrue(result.contains("STONE"));
        assertTrue(result.contains("DIRT"));
        assertTrue(result.contains("block-break"));
    }
}