package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.vault.VaultProvider;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoneyRequirementTest {

    private Player mockPlayer;
    private Economy mockEconomy;
    private VaultProvider mockVaultProvider;

    @BeforeEach
    void setUp() {
        // Create mocks
        mockPlayer = mock(Player.class);
        mockEconomy = mock(Economy.class);
        mockVaultProvider = mock(VaultProvider.class);

        // Set up mock behavior
        when(mockVaultProvider.getEconomy()).thenReturn(mockEconomy);

        // Use the static setter method instead of mocking Ranks.getPlugin
        MoneyRequirement.setTestVaultProvider(mockVaultProvider);
    }

    @AfterEach
    void tearDown() {
        // Clean up by resetting the test provider
        MoneyRequirement.setTestVaultProvider(null);
    }

    @Test
    void testConstructor_ValidAmount() {
        // Arrange
        String[] params = {"100.5"};
        when(mockEconomy.getBalance(mockPlayer)).thenReturn(100.5); // Exact amount

        // Act
        MoneyRequirement requirement = new MoneyRequirement(params);

        // Assert - Verify indirectly through meetsRequirement
        assertTrue(requirement.meetsRequirement(mockPlayer));

        // Also verify with slightly more money
        when(mockEconomy.getBalance(mockPlayer)).thenReturn(101.0);
        assertTrue(requirement.meetsRequirement(mockPlayer));

        // And verify with slightly less money
        when(mockEconomy.getBalance(mockPlayer)).thenReturn(100.4);
        assertFalse(requirement.meetsRequirement(mockPlayer));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-10", "abc", ""})
    void testConstructor_InvalidAmount(String invalidAmount) {
        // Arrange
        String[] params = {invalidAmount};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new MoneyRequirement(params));
    }

    @Test
    void testConstructor_TooManyParams() {
        // Arrange
        String[] params = {"param1", "100"};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new MoneyRequirement(params));
    }

    @Test
    void testConstructor_TooFewParams() {
        // Arrange
        String[] params = {};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new MoneyRequirement(params));
    }

    @Test
    void testMeetsRequirement_EnoughMoney() {
        // Arrange
        String[] params = {"100"};
        MoneyRequirement requirement = new MoneyRequirement(params);
        when(mockEconomy.getBalance(mockPlayer)).thenReturn(150.0);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertTrue(result);
        verify(mockEconomy).getBalance(mockPlayer);
    }

    @Test
    void testMeetsRequirement_NotEnoughMoney() {
        // Arrange
        String[] params = {"100"};
        MoneyRequirement requirement = new MoneyRequirement(params);
        when(mockEconomy.getBalance(mockPlayer)).thenReturn(50.0);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertFalse(result);
        verify(mockEconomy).getBalance(mockPlayer);
    }

    @Test
    void testMeetsRequirement_ExactAmount() {
        // Arrange
        String[] params = {"100"};
        MoneyRequirement requirement = new MoneyRequirement(params);
        when(mockEconomy.getBalance(mockPlayer)).thenReturn(100.0);

        // Act
        boolean result = requirement.meetsRequirement(mockPlayer);

        // Assert
        assertTrue(result);
        verify(mockEconomy).getBalance(mockPlayer);
    }

    @Test
    void testToString() {
        // Arrange
        String[] params = {"100"};
        MoneyRequirement requirement = new MoneyRequirement(params);

        // Act
        String result = requirement.toString();

        // Assert
        assertTrue(result.contains("100"));
        assertTrue(result.contains("money"));
    }
}
