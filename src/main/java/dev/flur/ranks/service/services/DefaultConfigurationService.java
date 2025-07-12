package dev.flur.ranks.service.services;

import dev.flur.ranks.service.ConfigurationService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
 * Default implementation of ConfigurationService.
 */
public class DefaultConfigurationService implements ConfigurationService {

    private final JavaPlugin plugin;
    private final Logger logger;
    private final Map<String, FileConfiguration> configCache;

    public DefaultConfigurationService(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.configCache = new HashMap<>();
    }

    @Override
    @NotNull
    public FileConfiguration getConfiguration(@NotNull String fileName) {
        if (configCache.containsKey(fileName)) {
            return configCache.get(fileName);
        }

        File configFile = new File(plugin.getDataFolder(), fileName + ".yml");
        if (!configFile.exists()) {
            generateFile(fileName + ".yml");
        }

        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            configCache.put(fileName, config);
            return config;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load configuration file: " + fileName + ".yml", e);
            // Return empty configuration as fallback
            FileConfiguration fallback = new YamlConfiguration();
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
    public void saveConfiguration(@NotNull String fileName, @NotNull FileConfiguration config) {
        try {
            File configFile = new File(plugin.getDataFolder(), fileName + ".yml");
            config.save(configFile);
            configCache.put(fileName, config);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save configuration file: " + fileName + ".yml", e);
        }
    }

    @Override
    public boolean configExists(@NotNull String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName + ".yml");
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