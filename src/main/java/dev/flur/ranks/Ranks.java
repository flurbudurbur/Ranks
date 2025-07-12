package dev.flur.ranks;

import dev.flur.ranks.command.CommandManager;
import dev.flur.ranks.service.ServiceContainer;
import dev.flur.ranks.vault.DefaultVaultProvider;
import dev.flur.ranks.vault.VaultProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Ranks extends JavaPlugin {

    // Instance variables
    private VaultProvider vaultProvider;
    private ServiceContainer serviceContainer;
    private boolean debug = false;

    @Override
    public void onEnable() {
        // Load configuration
        saveDefaultConfig();
        debug = getConfig().getBoolean("debug");

        // Initialize core services
        vaultProvider = new DefaultVaultProvider(this);

        // Initialize service container
        serviceContainer = new ServiceContainer(this);

        // Start services
        serviceContainer.start();

        // Initialize command manager with dependency injection
        new CommandManager(this, serviceContainer);

        getLogger().info("Ranks plugin enabled with dependency injection");
    }

    @Override
    public void onDisable() {
        // Shutdown services
        if (serviceContainer != null) {
            serviceContainer.stop();
        }

        getLogger().info("Ranks plugin disabled");
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
