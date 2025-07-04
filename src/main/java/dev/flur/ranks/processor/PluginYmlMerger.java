package dev.flur.ranks.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class PluginYmlMerger {
    private static final Logger logger = LoggerFactory.getLogger(PluginYmlMerger.class);

    public static void main(String[] args) {
        try {
            mergePluginYml();
        } catch (Exception e) {
            logger.error("Failed to merge plugin.yml", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void mergePluginYml() throws IOException {
        Yaml yaml = new Yaml();

        // Read base plugin.yml
        Path basePluginYml = Paths.get("src/main/resources/plugin.yml");
        Map<String, Object> baseConfig = new HashMap<>();

        if (Files.exists(basePluginYml)) {
            try (InputStream is = Files.newInputStream(basePluginYml)) {
                Map<String, Object> loaded = yaml.load(is);
                if (loaded != null) {
                    baseConfig.putAll(loaded);
                }
            }
        }

        // Read generated commands.yml
        Path generatedCommands = Paths.get("target/classes/commands.yml");
        if (Files.exists(generatedCommands)) {
            logger.info("Found generated commands.yml");
            try (InputStream is = Files.newInputStream(generatedCommands)) {
                Map<String, Object> commandsConfig = yaml.load(is);
                if (commandsConfig != null && commandsConfig.containsKey("commands")) {
                    Map<String, Object> commands = (Map<String, Object>) commandsConfig.get("commands");

                    // Merge with existing commands
                    Map<String, Object> existingCommands = (Map<String, Object>) baseConfig.get("commands");
                    if (existingCommands == null) {
                        existingCommands = new HashMap<>();
                        baseConfig.put("commands", existingCommands);
                    }

                    existingCommands.putAll(commands);
                    logger.info("Merged {} commands", commands.size());
                }
            }
        } else {
            logger.info("No generated commands.yml found");
        }

        // Write merged plugin.yml
        Path outputPath = Paths.get("target/classes/plugin.yml");
        try (Writer writer = Files.newBufferedWriter(outputPath)) {
            yaml.dump(baseConfig, writer);
        }

        logger.info("Successfully merged plugin.yml with auto-generated commands");
    }
}