package dev.flur.ranks.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

/**
 * Interface for providing access to Vault services.
 * This interface facilitates mocking in tests.
 */
public interface VaultProvider {
    
    /**
     * Get the Economy service.
     * 
     * @return the Economy service
     */
    Economy getEconomy();
    
    /**
     * Get the Permission service.
     * 
     * @return the Permission service
     */
    Permission getPermissions();
}