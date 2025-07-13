package dev.flur.ranks.service.services;

import dev.flur.ranks.service.ConfigurationService;
import dev.flur.ranks.service.config.TomlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of ConfigurationService using TOML.
 */
public class DefaultConfigurationService implements ConfigurationService {

    private final JavaPlugin plugin;
    private final Logger logger;
    private final Map<String, TomlConfiguration> configCache;

    public DefaultConfigurationService(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.configCache = new HashMap<>();
    }

    @Override
    @NotNull
    public TomlConfiguration getConfiguration(@NotNull String fileName) {
        if (configCache.containsKey(fileName)) {
            return configCache.get(fileName);
        }

        File configFile = new File(plugin.getDataFolder(), fileName + ".toml");
        if (!configFile.exists()) {
            generateFile(fileName + ".toml");
        }

        try {
            TomlConfiguration config = new TomlConfiguration(configFile);
            configCache.put(fileName, config);
            return config;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load configuration file: " + fileName + ".toml", e);
            // Return empty configuration as fallback
            File fallbackFile = new File(plugin.getDataFolder(), fileName + "_fallback.toml");
            TomlConfiguration fallback = new TomlConfiguration(fallbackFile);
            configCache.put(fileName, fallback);
            return fallback;
        }
    }

    @Override
    public void reloadConfigurations() {
        configCache.clear();
        logger.info("All configurations reloaded");
    }

    @Override
    public void saveConfiguration(@NotNull String fileName, @NotNull TomlConfiguration config) {
        try {
            config.save();
            configCache.put(fileName, config);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save configuration file: " + fileName + ".toml", e);
        }
    }

    @Override
    public boolean configExists(@NotNull String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName + ".toml");
        return configFile.exists();
    }

    private void generateFile(@NotNull String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (file.exists()) {
            return;
        }

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            logger.severe("Failed to create directory: " + parentDir.getAbsolutePath());
            return;
        }

        try (InputStream inputStream = plugin.getResource(fileName)) {
            if (inputStream == null) {
                logger.warning("Resource file '" + fileName + "' not found in plugin JAR");
                return;
            }

            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.info("Generated file: " + fileName);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to generate file: " + fileName, e);
        }
    }
}