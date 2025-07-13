package dev.flur.ranks;

import dev.flur.ranks.command.CommandManager;
import dev.flur.ranks.service.ConfigurationService;
import dev.flur.ranks.service.ServiceContainer;
import dev.flur.ranks.service.config.TomlConfiguration;
import dev.flur.ranks.vault.DefaultVaultProvider;
import dev.flur.ranks.vault.VaultProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Ranks extends JavaPlugin {

    // Instance variables
    private VaultProvider vaultProvider;
    private ServiceContainer serviceContainer;
    private boolean debug = false;

    @Override
    public void onEnable() {
        // Initialize service container
        serviceContainer = new ServiceContainer(this);

        ConfigurationService configService = serviceContainer.getConfigurationService();

        TomlConfiguration mainConfig = configService.getConfiguration("config");
        debug = mainConfig.getBoolean("debug", false);

        // Initialize core services
        vaultProvider = new DefaultVaultProvider(this);


        // Start services
        serviceContainer.start();

        // Initialize the command manager
        new CommandManager(this, serviceContainer);

        getLogger().info("Ranks plugin enabled");
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
     * Gets the main configuration.
     *
     * @return The main TomlConfiguration instance
     */
    public @NotNull TomlConfiguration getMainConfig() {
        return serviceContainer.getConfigurationService().getConfiguration("config");
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
