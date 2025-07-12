package dev.flur.ranks.message;

import dev.flur.ranks.Ranks;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * High-level message manager that coordinates message loading, templating, and delivery.
 * Delegates specific responsibilities to specialized components.
 */
public class MessageManager {
    private static final String DEFAULT_LOCALE = "en";

    private final MessageLoader messageLoader;
    private final TemplateProcessor templateProcessor;
    private final BukkitAudiences audiences;
    private final String defaultLocale;

    public MessageManager(@NotNull Ranks plugin, @NotNull dev.flur.ranks.service.ConfigurationService configurationService) {
        this.messageLoader = new MessageLoader(plugin, configurationService);
        this.templateProcessor = new TemplateProcessor(plugin);
        this.audiences = BukkitAudiences.create(plugin);

        // Load default locale from config
        String configLocale = plugin.getConfig().getString("locale", DEFAULT_LOCALE);
        this.defaultLocale = configLocale.isEmpty() ? DEFAULT_LOCALE : configLocale;
    }

    /**
     * Reloads all message configurations.
     */
    public void reload() {
        messageLoader.loadLocales();
    }

    /**
     * Gets a formatted message for the given key, locale, and context.
     *
     * @param key     The message key
     * @param locale  The locale to use
     * @param context The context variables for templating
     * @return The formatted message as a Component
     */
    public Component getMessage(String key, String locale, Map<String, Object> context) {
        String rawTemplate = messageLoader.getRawMessage(key, locale, defaultLocale);
        return templateProcessor.processTemplate(rawTemplate, context);
    }

    /**
     * Gets a formatted message for the given message enum, locale, and context.
     *
     * @param message The message enum
     * @param locale  The locale to use
     * @param context The context variables for templating
     * @return The formatted message as a Component
     */
    public Component getMessage(Messages message, String locale, Map<String, Object> context) {
        return getMessage(message.getKey(), locale, context);
    }

    /**
     * Sends a message to a command sender.
     *
     * @param sender  The command sender to send the message to
     * @param message The message enum
     * @param context The context variables for templating
     */
    public void sendMessage(@NotNull CommandSender sender, @NotNull Messages message, Map<String, Object> context) {
        String locale = getLocaleForSender(sender);
        Component component = getMessage(message, locale, context);

        Audience audience = audiences.sender(sender);
        audience.sendMessage(component);
    }

    /**
     * Sends a message to a command sender with an empty context.
     *
     * @param sender  The command sender to send the message to
     * @param message The message enum
     */
    public void sendMessage(@NotNull CommandSender sender, @NotNull Messages message) {
        sendMessage(sender, message, new HashMap<>());
    }

    /**
     * Broadcasts a message to all players on the server.
     *
     * @param message The message enum
     * @param context The context variables for templating
     */
    public void broadcastMessage(@NotNull Messages message, Map<String, Object> context) {
        Component component = getMessage(message, defaultLocale, context);
        Audience audience = audiences.all();
        audience.sendMessage(component);
    }

    /**
     * Broadcasts a message to all players on the server with an empty context.
     *
     * @param message The message enum
     */
    public void broadcastMessage(@NotNull Messages message) {
        broadcastMessage(message, new HashMap<>());
    }

    /**
     * Gets the appropriate locale for a command sender.
     *
     * @param sender The command sender
     * @return The locale to use for this sender
     */
    private String getLocaleForSender(@NotNull CommandSender sender) {
        if (sender instanceof Player player) {
            String locale = player.getLocale();
            if (locale != null && !locale.isEmpty()) {
                // Convert to just the language part (e.g., "en_US" -> "en")
                return locale.split("_")[0].toLowerCase(Locale.ROOT);
            }
        }
        return defaultLocale;
    }

    /**
     * Shuts down the message manager and releases resources.
     */
    public void shutdown() {
        if (audiences != null) {
            audiences.close();
        }
    }
}
