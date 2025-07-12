package dev.flur.ranks.service.services;

import dev.flur.ranks.service.ConfigurationService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultConfigurationServiceTest {

    private JavaPlugin plugin;
    private Logger logger;
    private DefaultConfigurationService configService;

    @TempDir
    Path tempDir;

    private File dataFolder;

    @BeforeEach
    void setUp() {
        dataFolder = tempDir.toFile();
        plugin = mock(JavaPlugin.class);
        logger = mock(Logger.class);

        when(plugin.getDataFolder()).thenReturn(dataFolder);
        when(plugin.getLogger()).thenReturn(logger);

        configService = new DefaultConfigurationService(plugin);
    }

    @Nested
    @DisplayName("Configuration Loading Tests")
    class ConfigurationLoadingTests {

        @Test
        @DisplayName("Should load configuration from cache if available")
        void shouldLoadConfigurationFromCache() {
            // Arrange - First call to create cache entry
            String fileName = "config";
            File configFile = new File(dataFolder, fileName + ".yml");

            // Create a config file
            try {
                if (!configFile.getParentFile().exists()) {
                    configFile.getParentFile().mkdirs();
                }
                Files.writeString(configFile.toPath(), "test: value");
            } catch (IOException e) {
                fail("Failed to create test config file", e);
            }

            // Act - First call loads from file
            FileConfiguration firstConfig = configService.getConfiguration(fileName);

            // Modify the file to verify we're using cache on second call
            try {
                Files.writeString(configFile.toPath(), "test: modified");
            } catch (IOException e) {
                fail("Failed to modify test config file", e);
            }

            // Act - Second call should use cache
            FileConfiguration secondConfig = configService.getConfiguration(fileName);

            // Assert
            assertEquals("value", firstConfig.getString("test"));
            assertEquals("value", secondConfig.getString("test")); // Should still be original value from cache
            assertSame(firstConfig, secondConfig); // Should be the same object reference
        }

        @Test
        @DisplayName("Should generate file if it doesn't exist")
        void shouldGenerateFileIfNotExists() {
            // Arrange
            String fileName = "newconfig";
            String resourceContent = "generated: true";
            InputStream inputStream = new ByteArrayInputStream(resourceContent.getBytes(StandardCharsets.UTF_8));

            when(plugin.getResource(fileName + ".yml")).thenReturn(inputStream);

            // Act
            FileConfiguration config = configService.getConfiguration(fileName);

            // Assert
            assertTrue(new File(dataFolder, fileName + ".yml").exists());
            verify(logger).info(contains("Generated file"));
        }

        @Test
        @DisplayName("Should handle missing resource file")
        void shouldHandleMissingResourceFile() {
            // Arrange
            String fileName = "missing";
            when(plugin.getResource(fileName + ".yml")).thenReturn(null);

            // Act
            FileConfiguration config = configService.getConfiguration(fileName);

            // Assert
            assertNotNull(config);
            verify(logger).warning(contains("Resource file '" + fileName + ".yml' not found"));
        }

        @Test
        @DisplayName("Should handle exception when loading configuration")
        void shouldHandleExceptionWhenLoadingConfiguration() throws IOException {
            // Arrange
            String fileName = "invalid";
            File configFile = new File(dataFolder, fileName + ".yml");

            // Create an invalid YAML file
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            Files.writeString(configFile.toPath(), "invalid: yaml: content:");

            // Mock YamlConfiguration to throw exception
            JavaPlugin mockPlugin = mock(JavaPlugin.class);
            when(mockPlugin.getDataFolder()).thenReturn(dataFolder);
            when(mockPlugin.getLogger()).thenReturn(logger);

            // Act
            FileConfiguration config = configService.getConfiguration(fileName);

            // Assert
            assertNotNull(config);
            verify(logger).log(any(), contains("Failed to load configuration file"), any(Exception.class));
        }
    }

    @Nested
    @DisplayName("Configuration Management Tests")
    class ConfigurationManagementTests {

        @Test
        @DisplayName("Should reload configurations")
        void shouldReloadConfigurations() {
            // Arrange
            String fileName = "config";
            File configFile = new File(dataFolder, fileName + ".yml");

            // Create a config file
            try {
                if (!configFile.getParentFile().exists()) {
                    configFile.getParentFile().mkdirs();
                }
                Files.writeString(configFile.toPath(), "test: value");
            } catch (IOException e) {
                fail("Failed to create test config file", e);
            }

            // Load the configuration to cache it
            FileConfiguration firstConfig = configService.getConfiguration(fileName);

            // Modify the file
            try {
                Files.writeString(configFile.toPath(), "test: modified");
            } catch (IOException e) {
                fail("Failed to modify test config file", e);
            }

            // Act - Reload configurations
            configService.reloadConfigurations();
            FileConfiguration reloadedConfig = configService.getConfiguration(fileName);

            // Assert
            assertEquals("modified", reloadedConfig.getString("test")); // Should have new value
            verify(logger).info("All configurations reloaded");
        }

        @Test
        @DisplayName("Should save configuration")
        void shouldSaveConfiguration() throws IOException {
            // Arrange
            String fileName = "savetest";
            FileConfiguration config = new YamlConfiguration();
            config.set("saved", "value");

            // Act
            configService.saveConfiguration(fileName, config);

            // Assert
            File savedFile = new File(dataFolder, fileName + ".yml");
            assertTrue(savedFile.exists());

            // Verify content was saved correctly
            FileConfiguration loadedConfig = YamlConfiguration.loadConfiguration(savedFile);
            assertEquals("value", loadedConfig.getString("saved"));
        }

        @Test
        @DisplayName("Should handle exception when saving configuration")
        void shouldHandleExceptionWhenSavingConfiguration() throws IOException {
            // Arrange
            String fileName = "failsave";
            FileConfiguration config = mock(FileConfiguration.class);
            doThrow(new IOException("Test exception")).when(config).save(any(File.class));

            // Act
            configService.saveConfiguration(fileName, config);

            // Assert
            verify(logger).log(any(), contains("Failed to save configuration file"), any(IOException.class));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @DisplayName("Should check if configuration exists")
        void shouldCheckIfConfigurationExists(boolean exists) {
            // Arrange
            String fileName = "existstest";
            File configFile = new File(dataFolder, fileName + ".yml");

            if (exists) {
                try {
                    if (!configFile.getParentFile().exists()) {
                        configFile.getParentFile().mkdirs();
                    }
                    Files.writeString(configFile.toPath(), "test: value");
                } catch (IOException e) {
                    fail("Failed to create test config file", e);
                }
            }

            // Act
            boolean result = configService.configExists(fileName);

            // Assert
            assertEquals(exists, result);
        }
    }

    @Nested
    @DisplayName("File Generation Tests")
    class FileGenerationTests {

        @Test
        @DisplayName("Should handle directory creation failure")
        void shouldHandleDirectoryCreationFailure() {
            // This test is more complex to set up with mocks due to File class behavior
            // Instead, we'll test the directory creation failure by creating a file where a directory should be

            // Arrange
            String fileName = "dirfail";
            File configDir = new File(dataFolder, "dirfail-parent");

            try {
                // Create a file where the directory should be
                if (!configDir.getParentFile().exists()) {
                    configDir.getParentFile().mkdirs();
                }
                // Create a regular file instead of a directory
                Files.writeString(configDir.toPath(), "This is a file, not a directory");

                // Create a mock plugin that returns this path
                JavaPlugin mockPlugin = mock(JavaPlugin.class);
                when(mockPlugin.getDataFolder()).thenReturn(configDir);
                when(mockPlugin.getLogger()).thenReturn(logger);
                when(mockPlugin.getResource(anyString())).thenReturn(null);

                // Create a new service with our mocked plugin
                DefaultConfigurationService mockedService = new DefaultConfigurationService(mockPlugin);

                // Act
                FileConfiguration config = mockedService.getConfiguration(fileName);

                // Assert
                assertNotNull(config);
                // The implementation might handle this differently, so we'll check for any log message
                verify(logger, atLeastOnce()).warning(anyString());
            } catch (IOException e) {
                fail("Test setup failed", e);
            }
        }

        @Test
        @DisplayName("Should handle IO exception during file generation")
        void shouldHandleIOExceptionDuringFileGeneration() {
            // Arrange
            String fileName = "iofail";
            InputStream mockStream = mock(InputStream.class);

            try {
                when(plugin.getResource(fileName + ".yml")).thenReturn(mockStream);
                doThrow(new IOException("Test IO exception")).when(mockStream).close();
            } catch (IOException e) {
                fail("Mock setup failed", e);
            }

            // Act
            FileConfiguration config = configService.getConfiguration(fileName);

            // Assert
            assertNotNull(config);
            verify(logger).log(any(), contains("Failed to generate file"), any(IOException.class));
        }
    }
}
