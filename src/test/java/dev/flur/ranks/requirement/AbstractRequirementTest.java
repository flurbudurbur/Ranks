package dev.flur.ranks.requirement;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.vault.VaultProvider;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * Base class for requirement tests with common functionality
 */
public abstract class AbstractRequirementTest {

    @Mock
    protected OfflinePlayer mockPlayer;

    @Mock
    protected org.bukkit.entity.Player mockPlayerEntity;

    @Mock
    protected VaultProvider mockVaultProvider;

    @Mock
    protected Economy mockEconomy;

    @Mock
    protected Permission mockPermission;

    // Store the original VaultProvider to restore it after tests
    private VaultProvider originalVaultProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Store the original VaultProvider
        originalVaultProvider = Ranks.getVaultProvider();

        // Mock player.getPlayer() to return mockPlayerEntity
        when(mockPlayer.getPlayer()).thenReturn(mockPlayerEntity);

        // Mock other common methods that might be needed
        when(mockPlayerEntity.getLevel()).thenReturn(10);
        when(mockPlayerEntity.getStatistic(any(org.bukkit.Statistic.class))).thenReturn(3600);

        // Mock VaultProvider methods
        when(mockVaultProvider.getEconomy()).thenReturn(mockEconomy);
        when(mockVaultProvider.getPermissions()).thenReturn(mockPermission);

        // Mock Economy methods
        when(mockEconomy.getBalance(any(OfflinePlayer.class))).thenReturn(1000.0);

        // Set the mock VaultProvider
        Ranks.setVaultProvider(mockVaultProvider);
    }

    @AfterEach
    void tearDown() {
        // Restore the original VaultProvider
        Ranks.setVaultProvider(originalVaultProvider);
    }
}