package dev.flur.ranks.message;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.service.ConfigurationService;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for loading and managing message templates from configuration files.
 */
public class MessageLoader {
    private final Ranks plugin;
    private final Map<String, Map<String, String>> localeMessages;
    private final ConfigurationService configurationService;

    public MessageLoader(@NotNull Ranks plugin, @NotNull ConfigurationService configurationService) {
        this.plugin = plugin;
        this.localeMessages = new HashMap<>();
        this.configurationService = configurationService;
        loadLocales();
    }

    /**
     * Loads all locale files from the locale directory.
     */
    public void loadLocales() {
        localeMessages.clear();

        // Load default locale first
        String defaultLocale = plugin.getConfig().getString("locale", "en");
        loadLocale(defaultLocale);

        // Load other locales if configured
        String[] additionalLocales = plugin.getConfig().getStringList("additional-locales").toArray(new String[0]);
        for (String locale : additionalLocales) {
            if (!locale.equals(defaultLocale)) {
                loadLocale(locale);
            }
        }
    }

    /**
     * Loads a specific locale file.
     *
     * @param locale The locale code to load
     */
    private void loadLocale(@NotNull String locale) {
        FileConfiguration localeConfig = configurationService.getConfiguration("locale/" + locale);
        Map<String, String> messages = new HashMap<>();

        // Flatten the configuration to get all message keys
        flattenConfig(localeConfig, "", messages);

        localeMessages.put(locale.toLowerCase(), messages);

        if (plugin.isDebugEnabled()) {
            plugin.getLogger().info("Loaded " + messages.size() + " messages for locale: " + locale);
        }
    }

    /**
     * Flattens a nested configuration into a map with dot-separated keys.
     *
     * @param config The configuration to flatten
     * @param prefix The current key prefix
     * @param result The map to store results in
     */
    private void flattenConfig(org.bukkit.configuration.ConfigurationSection config, String prefix, Map<String, String> result) {
        if (config == null) return;

        for (String key : config.getKeys(false)) {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;

            if (config.isConfigurationSection(key)) {
                flattenConfig(config.getConfigurationSection(key), fullKey, result);
            } else {
                String value = config.getString(key);
                if (value != null) {
                    result.put(fullKey, value);
                }
            }
        }
    }

    /**
     * Gets a raw message template for the given key and locale.
     *
     * @param key           The message key
     * @param locale        The locale to use
     * @param defaultLocale The fallback locale
     * @return The raw message template, or a default message if not found
     */
    public String getRawMessage(String key, String locale, String defaultLocale) {
        Map<String, String> messages = localeMessages.get(locale.toLowerCase());

        if (messages != null && messages.containsKey(key)) {
            return messages.get(key);
        }

        // Fall back to default locale if not found
        if (!locale.equalsIgnoreCase(defaultLocale)) {
            messages = localeMessages.get(defaultLocale.toLowerCase());
            if (messages != null && messages.containsKey(key)) {
                return messages.get(key);
            }
        }

        // Return a default message if not found
        return "Missing message: " + key;
    }
}
