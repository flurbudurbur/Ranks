package dev.flur.ranks.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Default implementation of VaultProvider that uses the actual Vault services.
 */
public class DefaultVaultProvider implements VaultProvider {

    private final Plugin plugin;
    private Economy economy;
    private Permission permission;

    /**
     * Constructs a new DefaultVaultProvider.
     *
     * @param plugin the Plugin instance
     */
    public DefaultVaultProvider(Plugin plugin) {
        this.plugin = plugin;
        setupEconomy();
        setupPermissions();
    }

    /**
     * Sets up the Economy service.
     */
    private void setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        economy = rsp.getProvider();
    }

    /**
     * Sets up the Permission service.
     */
    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return;
        }
        permission = rsp.getProvider();
    }

    @Override
    public Economy getEconomy() {
        return economy;
    }

    @Override
    public Permission getPermissions() {
        return permission;
    }
}