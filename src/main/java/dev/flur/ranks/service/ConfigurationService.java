package dev.flur.ranks.service;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * Service interface for managing plugin configurations.
 */
public interface ConfigurationService {

    /**
     * Gets a configuration file by name.
     */
    @NotNull
    FileConfiguration getConfiguration(@NotNull String fileName);

    /**
     * Reloads all configurations.
     */
    void reloadConfigurations();

    /**
     * Saves a configuration file.
     */
    void saveConfiguration(@NotNull String fileName, @NotNull FileConfiguration config);

    /**
     * Checks if a configuration file exists.
     */
    boolean configExists(@NotNull String fileName);
}