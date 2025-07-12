package dev.flur.ranks;

import dev.flur.ranks.service.ServiceContainer;
import dev.flur.ranks.vault.TestableVaultProvider;
import dev.flur.ranks.vault.VaultProvider;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.file.FileConfiguration;
import org.mockito.Mockito;

import java.util.logging.Logger;

/**
 * A testable version of the Ranks plugin that doesn't extend JavaPlugin.
 * This class is used for testing purposes only.
 */
public class TestableRanks {

    private VaultProvider vaultProvider;
    private ServiceContainer serviceContainer;
    private boolean debug = false;
    private FileConfiguration config;
    private Logger logger;

    /**
     * Creates a new TestableRanks instance with the given configuration and logger.
     *
     * @param config The configuration to use
     * @param logger The logger to use
     */
    public TestableRanks(FileConfiguration config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    /**
     * Simulates the onEnable method of the Ranks plugin.
     */
    public void onEnable() {
        // Load configuration
        debug = config.getBoolean("debug");

        // Create mock Economy and Permission for the TestableVaultProvider
        Economy mockEconomy = Mockito.mock(Economy.class);
        Permission mockPermission = Mockito.mock(Permission.class);

        // Initialize core services with mocks
        vaultProvider = new TestableVaultProvider(mockEconomy, mockPermission);

        // Initialize service container with a mock
        serviceContainer = Mockito.mock(ServiceContainer.class);

        // Configure the mock to return true for isHealthy
        Mockito.when(serviceContainer.isHealthy()).thenReturn(true);

        // Start services
        serviceContainer.start();

        logger.info("Ranks plugin enabled with dependency injection");
    }

    /**
     * Simulates the onDisable method of the Ranks plugin.
     */
    public void onDisable() {
        // Shutdown services
        if (serviceContainer != null) {
            serviceContainer.stop();
        }

        logger.info("Ranks plugin disabled");
    }

    /**
     * Gets the VaultProvider instance.
     *
     * @return The VaultProvider instance
     */
    public VaultProvider getVaultProvider() {
        return vaultProvider;
    }

    /**
     * Gets the ServiceContainer instance.
     *
     * @return The ServiceContainer instance
     */
    public ServiceContainer getServiceContainer() {
        return serviceContainer;
    }

    /**
     * Checks if debug mode is enabled.
     *
     * @return True if debug mode is enabled, false otherwise
     */
    public boolean isDebugEnabled() {
        return debug;
    }
}
