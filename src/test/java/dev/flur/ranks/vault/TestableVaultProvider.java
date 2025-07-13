package dev.flur.ranks.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

/**
 * A testable implementation of VaultProvider that doesn't require a JavaPlugin instance.
 * This class is used for testing purposes only.
 */
public class TestableVaultProvider implements VaultProvider {

    private final Economy economy;
    private final Permission permission;

    /**
     * Constructs a new TestableVaultProvider with the given Economy and Permission instances.
     *
     * @param economy the Economy instance
     * @param permission the Permission instance
     */
    public TestableVaultProvider(Economy economy, Permission permission) {
        this.economy = economy;
        this.permission = permission;
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