package dev.flur.ranks;

import dev.flur.ranks.service.ServiceContainer;
import dev.flur.ranks.service.config.TomlConfiguration;
import dev.flur.ranks.vault.VaultProvider;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RanksTest {

    @Mock
    private Server server;

    @Mock
    private PluginManager pluginManager;

    @Mock
    private ServicesManager servicesManager;

    @Mock
    private RegisteredServiceProvider<Economy> economyProvider;

    @Mock
    private RegisteredServiceProvider<Permission> permissionProvider;

    @Mock
    private Economy economy;

    @Mock
    private Permission permission;

    @Mock
    private TomlConfiguration config;

    @Mock
    private Logger logger;

    private TestableRanks plugin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup server mock
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(server.getServicesManager()).thenReturn(servicesManager);

        // Setup plugin manager mock
        when(pluginManager.getPlugin("Vault")).thenReturn(mock(Plugin.class));

        // Setup services manager mock
        when(servicesManager.getRegistration(Economy.class)).thenReturn(economyProvider);
        when(servicesManager.getRegistration(Permission.class)).thenReturn(permissionProvider);

        // Setup providers
        when(economyProvider.getProvider()).thenReturn(economy);
        when(permissionProvider.getProvider()).thenReturn(permission);

        // Setup config mock
        when(config.getBoolean("debug")).thenReturn(false);

        // Create plugin instance with mocked dependencies
        plugin = new TestableRanks(config, logger);
    }

    @Test
    void testOnEnable() {
        // Call onEnable
        plugin.onEnable();

        // Verify that the vault provider was initialized
        assertNotNull(plugin.getVaultProvider());

        // Verify that the service container was initialized
        assertNotNull(plugin.getServiceContainer());

        // Verify debug mode is set from config
        assertFalse(plugin.isDebugEnabled());

        // Verify logger was called
        verify(logger).info(anyString());
    }

    @Test
    void testOnDisable() {
        // First call onEnable to initialize services
        plugin.onEnable();

        // The serviceContainer is already a mock, so we can directly verify it
        ServiceContainer serviceContainer = plugin.getServiceContainer();

        // Call onDisable
        plugin.onDisable();

        // Verify that stop was called on the service container
        verify(serviceContainer).stop();

        // Verify logger was called at least once
        verify(logger, atLeastOnce()).info(anyString());
    }

    @Test
    void testGetVaultProvider() {
        // First call onEnable to initialize the vault provider
        plugin.onEnable();

        // Get the vault provider
        VaultProvider vaultProvider = plugin.getVaultProvider();

        // Verify it's not null
        assertNotNull(vaultProvider);
    }

    @Test
    void testGetServiceContainer() {
        // First call onEnable to initialize the service container
        plugin.onEnable();

        // Get the service container
        ServiceContainer serviceContainer = plugin.getServiceContainer();

        // Verify it's not null
        assertNotNull(serviceContainer);
    }

    @Test
    void testIsDebugEnabled() {
        // Call onEnable to initialize debug field
        plugin.onEnable();

        // Test with debug=false (default)
        assertFalse(plugin.isDebugEnabled());

        // Change mock to return true
        when(config.getBoolean("debug")).thenReturn(true);

        // Reinitialize plugin
        plugin.onEnable();

        // Now debug should be true
        assertTrue(plugin.isDebugEnabled());
    }
}
