package dev.flur.ranks.requirement.requirements;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemUseRequirementTest {

    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        // Initialize MockBukkit server
        MockBukkit.mock();

        // Create mocks
        mockPlayer = mock(Player.class);
    }

    @AfterEach
    void tearDown() {
        // Clean up MockBukkit
        MockBukkit.unmock();
    }

    @Test
    void testConstructor_ValidParams() {
        // Arrange
        String[] params = {"DIAMOND_SWORD", "100"};
        when(mockPlayer.getStatistic(Statistic.USE_ITEM, Material.DIAMOND_SWORD)).thenReturn(100);

        // Act
        ItemUseRequirement requirement = new ItemUseRequirement(params);

        // Assert
        assertTrue(requirement.meetsRequirement(mockPlayer));

        // Also verify with more uses
        when(mockPlayer.getStatistic(Statistic.USE_ITEM, Material.DIAMOND_SWORD)).thenReturn(150);
        assertTrue(requirement.meetsRequirement(mockPlayer));

        // And verify with fewer uses
        when(mockPlayer.getStatistic(Statistic.USE_ITEM, Material.DIAMOND_SWORD)).thenReturn(99);
        assertFalse(requirement.meetsRequirement(mockPlayer));
    }

    @Test
    void testConstructor_InvalidMaterial() {
        // Arrange
        String[] params = {"INVALID_MATERIAL", "100"};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new ItemUseRequirement(params));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-10", "abc", ""})
    void testConstructor_InvalidAmount(String invalidAmount) {
        // Arrange
        String[] params = {"DIAMOND_SWORD", invalidAmount};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new ItemUseRequirement(params));
    }

    @Test
    void testConstructor_TooManyParams() {
        // Arrange
        String[] params = {"DIAMOND_SWORD", "param2", "100"};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new ItemUseRequirement(params));
    }

    @Test
    void testConstructor_TooFewParams() {
        // Arrange
        String[] params = {"DIAMOND_SWORD"};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new ItemUseRequirement(params));
    }

    @Test
    void testMeetsRequirement_EnoughUses() {
        // Arrange
        String[] params = {"DIAMOND_SWORD", "100"};
        ItemUseRequirement requirement = new ItemUseRequirement(params);
        when(mockPlayer.getStatistic(Statistic.USE_ITEM, Material.DIAMOND_SWORD)).thenReturn(150);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertTrue(result);
        verify(mockPlayer).getStatistic(Statistic.USE_ITEM, Material.DIAMOND_SWORD);
    }

    @Test
    void testMeetsRequirement_NotEnoughUses() {
        // Arrange
        String[] params = {"DIAMOND_SWORD", "100"};
        ItemUseRequirement requirement = new ItemUseRequirement(params);
        when(mockPlayer.getStatistic(Statistic.USE_ITEM, Material.DIAMOND_SWORD)).thenReturn(50);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertFalse(result);
        verify(mockPlayer).getStatistic(Statistic.USE_ITEM, Material.DIAMOND_SWORD);
    }

    @Test
    void testMeetsRequirement_ExactAmount() {
        // Arrange
        String[] params = {"DIAMOND_SWORD", "100"};
        ItemUseRequirement requirement = new ItemUseRequirement(params);
        when(mockPlayer.getStatistic(Statistic.USE_ITEM, Material.DIAMOND_SWORD)).thenReturn(100);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertTrue(result);
        verify(mockPlayer).getStatistic(Statistic.USE_ITEM, Material.DIAMOND_SWORD);
    }

    @Test
    void testToString() {
        // Arrange
        String[] params = {"DIAMOND_SWORD", "100"};
        ItemUseRequirement requirement = new ItemUseRequirement(params);

        // Act
        String result = requirement.toString();

        // Assert
        assertTrue(result.contains("100"));
        assertTrue(result.contains("DIAMOND_SWORD"));
        assertTrue(result.contains("item-use"));
    }
}