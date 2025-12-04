package dev.flur.ranks.template.service;

import dev.flur.ranks.result.Result;
import dev.flur.ranks.service.ConfigurationService;
import dev.flur.ranks.template.TemplateConfiguration;
import dev.flur.ranks.template.TemplateEngine;
import dev.flur.ranks.template.config.DefaultTemplateConfiguration;
import dev.flur.ranks.template.context.TemplateContextBuilder;
import dev.flur.ranks.template.engine.DefaultTemplateEngine;
import dev.flur.ranks.template.extension.RanksExtension;
import dev.flur.ranks.template.extension.function.TranslateFunction;
import dev.flur.ranks.template.loader.CompositeTemplateLoader;
import dev.flur.ranks.template.loader.FileTemplateLoader;
import dev.flur.ranks.template.loader.TemplateLoader;
import io.pebbletemplates.pebble.extension.Extension;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Default implementation of TemplateService.
 */
public class DefaultTemplateService implements TemplateService {

    private final Plugin plugin;
    private final ConfigurationService configurationService;
    private final Logger logger;
    private final TemplateConfiguration config;
    private final TemplateEngine engine;
    private final Map<String, Map<String, String>> localeMessages;

    /**
     * Creates a new template service.
     *
     * @param plugin               The plugin instance
     * @param configurationService The configuration service
     */
    public DefaultTemplateService(@NotNull Plugin plugin,
                                  @NotNull ConfigurationService configurationService) {
        this.plugin = plugin;
        this.configurationService = configurationService;
        this.logger = plugin.getLogger();
        this.localeMessages = new HashMap<>();

        // Load configuration
        this.config = loadConfiguration();

        // Create template engine
        this.engine = createEngine();

        // Load locale messages
        loadLocaleMessages();
    }

    @Override
    @NotNull
    public TemplateEngine getEngine() {
        return engine;
    }

    @Override
    @NotNull
    public TemplateContextBuilder newContext() {
        return TemplateContextBuilder.create();
    }

    @Override
    @NotNull
    public Result<Component> renderMessage(@NotNull String messageKey,
                                           @NotNull Player player,
                                           @NotNull Map<String, Object> context) {
        String locale = extractLocale(player);
        return renderMessage(messageKey, locale, context);
    }

    @Override
    @NotNull
    public Result<Component> renderMessage(@NotNull String messageKey,
                                           @NotNull String locale,
                                           @NotNull Map<String, Object> context) {
        // Get the raw message template
        String template = getRawMessage(messageKey, locale);

        if (template == null) {
            // Try default locale
            template = getRawMessage(messageKey, config.getDefaultLocale());
        }

        if (template == null) {
            return Result.failure("Message not found: " + messageKey);
        }

        // Add locale to context
        Map<String, Object> fullContext = new HashMap<>(context);
        fullContext.put("locale", locale);

        // Check if template is a file reference (starts with @)
        if (template.startsWith("@")) {
            String templateName = template.substring(1).trim();
            return engine.render(templateName, fullContext);
        }

        return engine.renderInline(template, fullContext);
    }

    @Override
    @NotNull
    public Result<Component> renderLayout(@NotNull String layoutName,
                                          @NotNull Map<String, Object> context) {
        String path = layoutName;
        return engine.render(path, context);
    }

    @Override
    @NotNull
    public Result<Component> renderInline(@NotNull String template,
                                          @NotNull Map<String, Object> context) {
        return engine.renderInline(template, context);
    }

    @Override
    @Nullable
    public String getRawMessage(@NotNull String messageKey, @NotNull String locale) {
        Map<String, String> messages = localeMessages.get(locale);
        if (messages != null) {
            return messages.get(messageKey);
        }
        return null;
    }

    @Override
    public boolean messageExists(@NotNull String messageKey) {
        for (Map<String, String> messages : localeMessages.values()) {
            if (messages.containsKey(messageKey)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void reload() {
        engine.reload();
        localeMessages.clear();
        loadLocaleMessages();
        logger.info("Template service reloaded");
    }

    @Override
    public void shutdown() {
        engine.clearCache();
        localeMessages.clear();
    }

    // ==================== Private Methods ====================

    /**
     * Loads configuration from config.yml.
     */
    private TemplateConfiguration loadConfiguration() {
        Path dataFolder = plugin.getDataFolder().toPath();
        ConfigurationSection templatesSection = plugin.getConfig().getConfigurationSection("templates");

        if (templatesSection != null) {
            return DefaultTemplateConfiguration.fromConfig(templatesSection, dataFolder);
        }

        return DefaultTemplateConfiguration.defaults(dataFolder);
    }

    /**
     * Creates the template engine with all extensions.
     */
    private TemplateEngine createEngine() {
        // Create loaders for each path
        List<TemplateLoader> loaders = new ArrayList<>();

        for (Path path : config.getTemplatePaths()) {
            // Use .peb for templates folder, .yml for locale folder
            String suffix = path.endsWith(Path.of("locale")) ? ".yml" : "";
            loaders.add(new FileTemplateLoader(path, suffix));
        }

        CompositeTemplateLoader compositeLoader = new CompositeTemplateLoader(loaders);

        // Create extensions
        List<Extension> extensions = new ArrayList<>();

        // Create translation provider
        TranslateFunction.TranslationProvider translationProvider = this::getRawMessage;

        // Add Ranks extension
        RanksExtension ranksExtension = new RanksExtension(
                plugin.getServer(),
                config.isPapiEnabled(),
                translationProvider
        );
        extensions.add(ranksExtension);

        return new DefaultTemplateEngine(config, compositeLoader, extensions, logger);
    }

    /**
     * Loads all locale messages from YAML files.
     */
    private void loadLocaleMessages() {
        Path localeDir = plugin.getDataFolder().toPath().resolve("locale");

        if (!localeDir.toFile().exists()) {
            return;
        }

        java.io.File[] localeFiles = localeDir.toFile().listFiles((dir, name) -> name.endsWith(".yml"));

        if (localeFiles == null) {
            return;
        }

        for (java.io.File file : localeFiles) {
            String locale = file.getName().replace(".yml", "");
            loadLocaleFile(locale, file);
        }
    }

    /**
     * Loads a single locale file.
     */
    private void loadLocaleFile(String locale, java.io.File file) {
        try {
            org.bukkit.configuration.file.YamlConfiguration yaml =
                    org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);

            Map<String, String> messages = new HashMap<>();
            flattenYaml(yaml, "", messages);
            localeMessages.put(locale, messages);

            logger.fine("Loaded " + messages.size() + " messages for locale: " + locale);
        } catch (Exception e) {
            logger.warning("Failed to load locale file: " + file.getName() + " - " + e.getMessage());
        }
    }

    /**
     * Flattens a YAML configuration to dot-separated keys.
     */
    private void flattenYaml(ConfigurationSection section, String prefix, Map<String, String> result) {
        for (String key : section.getKeys(false)) {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;

            if (section.isConfigurationSection(key)) {
                flattenYaml(section.getConfigurationSection(key), fullKey, result);
            } else {
                Object value = section.get(key);
                if (value != null) {
                    result.put(fullKey, value.toString());
                }
            }
        }
    }

    /**
     * Extracts locale from player.
     */
    private String extractLocale(Player player) {
        java.util.Locale loc = player.locale();
        if (loc != null) {
            return loc.getLanguage();
        }
        return config.getDefaultLocale();
    }
}
