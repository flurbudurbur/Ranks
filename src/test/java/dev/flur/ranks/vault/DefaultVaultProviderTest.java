package dev.flur.ranks.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultVaultProviderTest {

    @Mock
    private JavaPlugin plugin;
    
    @Mock
    private Server server;
    
    @Mock
    private PluginManager pluginManager;
    
    @Mock
    private ServicesManager servicesManager;
    
    @Mock
    private Plugin vaultPlugin;
    
    @Mock
    private RegisteredServiceProvider<Economy> economyProvider;
    
    @Mock
    private RegisteredServiceProvider<Permission> permissionProvider;
    
    @Mock
    private Economy economy;
    
    @Mock
    private Permission permission;
    
    private DefaultVaultProvider vaultProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup plugin mock
        when(plugin.getServer()).thenReturn(server);
        
        // Setup server mock
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(server.getServicesManager()).thenReturn(servicesManager);
    }

    @Test
    void testConstructor_VaultPresent() {
        // Setup Vault plugin mock
        when(pluginManager.getPlugin("Vault")).thenReturn(vaultPlugin);
        
        // Setup economy provider mock
        when(servicesManager.getRegistration(Economy.class)).thenReturn(economyProvider);
        when(economyProvider.getProvider()).thenReturn(economy);
        
        // Setup permission provider mock
        when(servicesManager.getRegistration(Permission.class)).thenReturn(permissionProvider);
        when(permissionProvider.getProvider()).thenReturn(permission);
        
        // Create vault provider
        vaultProvider = new DefaultVaultProvider(plugin);
        
        // Verify that the providers were initialized
        assertNotNull(vaultProvider.getEconomy());
        assertNotNull(vaultProvider.getPermissions());
        
        // Verify that the expected methods were called
        verify(pluginManager).getPlugin("Vault");
        verify(servicesManager).getRegistration(Economy.class);
        verify(servicesManager).getRegistration(Permission.class);
    }

    @Test
    void testConstructor_VaultMissing() {
        // Setup Vault plugin mock to be missing
        when(pluginManager.getPlugin("Vault")).thenReturn(null);
        
        // Create vault provider
        vaultProvider = new DefaultVaultProvider(plugin);
        
        // Verify that the economy provider is null
        assertNull(vaultProvider.getEconomy());
        
        // Verify that the expected methods were called
        verify(pluginManager).getPlugin("Vault");
        verify(servicesManager, never()).getRegistration(Economy.class);
    }

    @Test
    void testConstructor_EconomyProviderMissing() {
        // Setup Vault plugin mock
        when(pluginManager.getPlugin("Vault")).thenReturn(vaultPlugin);
        
        // Setup economy provider mock to be missing
        when(servicesManager.getRegistration(Economy.class)).thenReturn(null);
        
        // Setup permission provider mock
        when(servicesManager.getRegistration(Permission.class)).thenReturn(permissionProvider);
        when(permissionProvider.getProvider()).thenReturn(permission);
        
        // Create vault provider
        vaultProvider = new DefaultVaultProvider(plugin);
        
        // Verify that the economy provider is null
        assertNull(vaultProvider.getEconomy());
        
        // Verify that the permission provider is not null
        assertNotNull(vaultProvider.getPermissions());
        
        // Verify that the expected methods were called
        verify(pluginManager).getPlugin("Vault");
        verify(servicesManager).getRegistration(Economy.class);
        verify(servicesManager).getRegistration(Permission.class);
    }

    @Test
    void testConstructor_PermissionProviderMissing() {
        // Setup Vault plugin mock
        when(pluginManager.getPlugin("Vault")).thenReturn(vaultPlugin);
        
        // Setup economy provider mock
        when(servicesManager.getRegistration(Economy.class)).thenReturn(economyProvider);
        when(economyProvider.getProvider()).thenReturn(economy);
        
        // Setup permission provider mock to be missing
        when(servicesManager.getRegistration(Permission.class)).thenReturn(null);
        
        // Create vault provider
        vaultProvider = new DefaultVaultProvider(plugin);
        
        // Verify that the economy provider is not null
        assertNotNull(vaultProvider.getEconomy());
        
        // Verify that the permission provider is null
        assertNull(vaultProvider.getPermissions());
        
        // Verify that the expected methods were called
        verify(pluginManager).getPlugin("Vault");
        verify(servicesManager).getRegistration(Economy.class);
        verify(servicesManager).getRegistration(Permission.class);
    }

    @Test
    void testGetEconomy() {
        // Setup for a successful initialization
        when(pluginManager.getPlugin("Vault")).thenReturn(vaultPlugin);
        when(servicesManager.getRegistration(Economy.class)).thenReturn(economyProvider);
        when(economyProvider.getProvider()).thenReturn(economy);
        when(servicesManager.getRegistration(Permission.class)).thenReturn(permissionProvider);
        when(permissionProvider.getProvider()).thenReturn(permission);
        
        // Create vault provider
        vaultProvider = new DefaultVaultProvider(plugin);
        
        // Verify that getEconomy returns the expected economy
        assertEquals(economy, vaultProvider.getEconomy());
    }

    @Test
    void testGetPermissions() {
        // Setup for a successful initialization
        when(pluginManager.getPlugin("Vault")).thenReturn(vaultPlugin);
        when(servicesManager.getRegistration(Economy.class)).thenReturn(economyProvider);
        when(economyProvider.getProvider()).thenReturn(economy);
        when(servicesManager.getRegistration(Permission.class)).thenReturn(permissionProvider);
        when(permissionProvider.getProvider()).thenReturn(permission);
        
        // Create vault provider
        vaultProvider = new DefaultVaultProvider(plugin);
        
        // Verify that getPermissions returns the expected permission
        assertEquals(permission, vaultProvider.getPermissions());
    }
}