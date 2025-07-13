package dev.flur.ranks.service;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.service.config.TomlConfiguration;
import dev.flur.ranks.vault.VaultProvider;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceContainerTest {

    @Mock
    private Ranks plugin;

    @Mock
    private VaultProvider vaultProvider;

    @Mock
    private Permission permission;

    @Mock
    private Economy economy;

    @Mock
    private TomlConfiguration config;

    @Mock
    private TomlConfiguration ranksConfig;

    @Mock
    private Logger logger;

    @Mock
    private Server server;

    @Mock
    private PluginManager pluginManager;

    @Mock
    private ConsoleCommandSender consoleSender;

    @Mock
    private PluginDescriptionFile pluginDescriptionFile;

    private ServiceContainer serviceContainer;

    @Mock
    private MessageService mockMessageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup plugin description file mock
        when(pluginDescriptionFile.getName()).thenReturn("Ranks");
        when(plugin.getDescription()).thenReturn(pluginDescriptionFile);

        // Setup server mocks
        when(server.getConsoleSender()).thenReturn(consoleSender);
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(plugin.getServer()).thenReturn(server);

        // Allow any event registration
        doNothing().when(pluginManager).registerEvent(
                any(Class.class),
                any(org.bukkit.event.Listener.class),
                any(org.bukkit.event.EventPriority.class),
                any(org.bukkit.plugin.EventExecutor.class),
                any(org.bukkit.plugin.Plugin.class),
                anyBoolean()
        );

        // Setup plugin mock
        when(plugin.getVaultProvider()).thenReturn(vaultProvider);
        when(plugin.getLogger()).thenReturn(logger);
        when(plugin.getConfig()).thenReturn(config);

        // Setup vault provider mock
        when(vaultProvider.getPermissions()).thenReturn(permission);
        when(vaultProvider.getEconomy()).thenReturn(economy);

        // Setup config mock
        when(config.getBoolean("broadcast-rankups", true)).thenReturn(true);
        when(config.getString("locale", "en")).thenReturn("en");

        // Create service container
        serviceContainer = new ServiceContainer(plugin);
    }

    @Test
    void testConstructor() {
        // Verify that the plugin was used
        verify(plugin, atLeastOnce()).getVaultProvider();
        verify(plugin, atLeastOnce()).getLogger();

        // Verify that the service container is healthy
        assertTrue(serviceContainer.isHealthy());
    }

    @Test
    void testStart() {
        // Call start method
        serviceContainer.start();

        // Verify that logger was called
        verify(logger, atLeastOnce()).info(anyString());
    }

    @Test
    void testStop() {
        // Get the message service and create a spy to verify shutdown is called
        MessageService messageService = serviceContainer.getMessageService();
        MessageService messageServiceSpy = spy(messageService);

        // Replace the real message service with our spy using reflection
        try {
            Field messageField = ServiceContainer.class.getDeclaredField("messageService");
            messageField.setAccessible(true);
            messageField.set(serviceContainer, messageServiceSpy);
        } catch (Exception e) {
            fail("Failed to set message service spy: " + e.getMessage());
        }

        // Call stop method
        serviceContainer.stop();

        // Verify that shutdown was called on the message service
        verify(messageServiceSpy).shutdown();

        // Verify that logger was called
        verify(logger).info("Service container stopped");
    }


    @Test
    void testIsHealthy() {
        // Verify that the service container is healthy
        assertTrue(serviceContainer.isHealthy());

        // Make the service container unhealthy by setting a required service to null
        try {
            java.lang.reflect.Field field = ServiceContainer.class.getDeclaredField("configurationService");
            field.setAccessible(true);
            field.set(serviceContainer, null);
        } catch (Exception e) {
            fail("Failed to set configuration service to null: " + e.getMessage());
        }

        // Verify that the service container is now unhealthy
        assertFalse(serviceContainer.isHealthy());
    }

    @Test
    void testReload() {
        // Create spies of the services that are reloaded
        ConfigurationService configServiceSpy = spy(serviceContainer.getConfigurationService());
        RanksService ranksServiceSpy = spy(serviceContainer.getRanksService());
        MessageService messageServiceSpy = spy(serviceContainer.getMessageService());

        // Replace the real services with our spies
        try {
            java.lang.reflect.Field configField = ServiceContainer.class.getDeclaredField("configurationService");
            configField.setAccessible(true);
            configField.set(serviceContainer, configServiceSpy);

            java.lang.reflect.Field ranksField = ServiceContainer.class.getDeclaredField("ranksService");
            ranksField.setAccessible(true);
            ranksField.set(serviceContainer, ranksServiceSpy);

            java.lang.reflect.Field messageField = ServiceContainer.class.getDeclaredField("messageService");
            messageField.setAccessible(true);
            messageField.set(serviceContainer, messageServiceSpy);
        } catch (Exception e) {
            fail("Failed to set service spies: " + e.getMessage());
        }

        // Call reload method
        serviceContainer.reload();

        // Verify that reload methods were called on the services
        verify(configServiceSpy).reloadConfigurations();
        verify(messageServiceSpy).reload();
        verify(ranksServiceSpy).reload();
    }

    @Test
    void testGetters() {
        // Verify that all getters return non-null values
        assertNotNull(serviceContainer.getConfigurationService());
        assertNotNull(serviceContainer.getMessageService());
        assertNotNull(serviceContainer.getPermissionService());
        assertNotNull(serviceContainer.getRequirementRegistry());
        assertNotNull(serviceContainer.getRanksService());
        assertNotNull(serviceContainer.getRequirementValidator());
        assertNotNull(serviceContainer.getRankProgressionService());
        assertNotNull(serviceContainer.getPlayerRankService());
        assertNotNull(serviceContainer.getRankupValidator());
        assertNotNull(serviceContainer.getRankupProcessor());
        assertNotNull(serviceContainer.getRankupNotifier());
    }
}