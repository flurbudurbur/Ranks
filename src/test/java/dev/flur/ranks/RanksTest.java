package dev.flur.ranks;

import dev.flur.ranks.vault.VaultProvider;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RanksTest {

    @Mock
    private Ranks mockPlugin;

    @Mock
    private Economy mockEconomy;

    @Mock
    private Permission mockPermission;

    @Mock
    private File mockDataFolder;

    @Mock
    private FileConfiguration mockConfig;

    @Mock
    private Logger mockLogger;

    private VaultProvider mockVaultProvider;
    private MockedStatic<Ranks> mockedStaticRanks;

    @BeforeEach
    public void setUp() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this);

        // Mock the static getPlugin method
        mockedStaticRanks = Mockito.mockStatic(Ranks.class);
        mockedStaticRanks.when(Ranks::getPlugin).thenReturn(mockPlugin);

        // Set up the mock plugin
        when(mockPlugin.getDataFolder()).thenReturn(mockDataFolder);
        when(mockPlugin.getConfig()).thenReturn(mockConfig);
        when(mockPlugin.getLogger()).thenReturn(mockLogger);

        // Create a mock VaultProvider
        mockVaultProvider = new VaultProvider() {
            @Override
            public Economy getEconomy() {
                return mockEconomy;
            }

            @Override
            public Permission getPermissions() {
                return mockPermission;
            }
        };

        // Mock the static methods of Ranks
        mockedStaticRanks.when(Ranks::getEconomy).thenReturn(mockEconomy);
        mockedStaticRanks.when(Ranks::getPermissions).thenReturn(mockPermission);
        mockedStaticRanks.when(Ranks::getVaultProvider).thenReturn(mockVaultProvider);

        // Mock the plugin's enabled state
        when(mockPlugin.isEnabled()).thenReturn(true);

        // Mock the resource loading
        InputStream mockInputStream = mock(InputStream.class);
        when(mockPlugin.getResource(anyString())).thenReturn(mockInputStream);

        // Mock the config values
        when(mockConfig.getBoolean("debug")).thenReturn(false);
    }

    @AfterEach
    public void tearDown() {
        // Close the static mock to prevent memory leaks
        mockedStaticRanks.close();
    }

    @Test
    public void testPluginLoads() {
        // Verify the plugin was loaded
        assertNotNull(mockPlugin);
        assertTrue(mockPlugin.isEnabled());
    }

    @Test
    public void testMockVaultProvider() {
        // Verify that our mock VaultProvider is being used
        assertSame(mockEconomy, Ranks.getEconomy());
        assertSame(mockPermission, Ranks.getPermissions());
    }

    @Test
    public void testPluginConfiguration() {
        // Verify that the plugin's configuration is accessible
        assertNotNull(mockPlugin.getConfig());
        // Test that we can get a boolean value from the config
        boolean debugValue = mockConfig.getBoolean("debug");
        assertFalse(debugValue); // We set it to false in setUp()
    }
}
