package dev.flur.ranks.service.services;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.message.MessageLoader;
import dev.flur.ranks.message.Locale;
import dev.flur.ranks.message.TemplateProcessor;
import dev.flur.ranks.service.ConfigurationService;
import dev.flur.ranks.service.MessageService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Default implementation of the MessageService interface.
 */
public class DefaultMessageService implements MessageService {
    private static final String DEFAULT_LOCALE = "en";

    private final MessageLoader messageLoader;
    private final TemplateProcessor templateProcessor;
    private final BukkitAudiences audiences;
    private final String defaultLocale;
    private final Logger logger;

    public DefaultMessageService(@NotNull Ranks plugin, @NotNull ConfigurationService configurationService) {
        this(plugin, configurationService, BukkitAudiences.create(plugin));
    }

    /**
     * Constructor with injectable BukkitAudiences for testing.
     *
     * @param plugin The plugin instance
     * @param configurationService The configuration service
     * @param audiences The BukkitAudiences instance
     */
    public DefaultMessageService(@NotNull Ranks plugin, @NotNull ConfigurationService configurationService, @NotNull BukkitAudiences audiences) {
        this.messageLoader = new MessageLoader(plugin, configurationService);
        this.templateProcessor = new TemplateProcessor(plugin);
        this.audiences = audiences;
        this.logger = plugin.getLogger();

        // Load default locale from config
        String configLocale = plugin.getConfig().getString("locale", DEFAULT_LOCALE);
        this.defaultLocale = configLocale.isEmpty() ? DEFAULT_LOCALE : configLocale;
    }

    @Override
    public void reload() {
        messageLoader.loadLocales();
    }

    @Override
    public Component getMessage(String key, String locale, Map<String, Object> context) {
        String rawTemplate = messageLoader.getRawMessage(key, locale, defaultLocale);
        return templateProcessor.processTemplate(rawTemplate, context);
    }

    @Override
    public Component getMessage(Locale message, String locale, Map<String, Object> context) {
        return getMessage(message.getKey(), locale, context);
    }

    @Override
    public void sendMessage(@NotNull CommandSender sender, @NotNull Locale message, Map<String, Object> context) {
        String locale = getLocaleForSender(sender);
        Component component = getMessage(message, locale, context);

        Audience audience = audiences.sender(sender);
        audience.sendMessage(component);
    }

    @Override
    public void sendMessage(@NotNull CommandSender sender, @NotNull Locale message) {
        sendMessage(sender, message, new HashMap<>());
    }

    @Override
    public void broadcastMessage(@NotNull Locale message, Map<String, Object> context) {
        Component component = getMessage(message, defaultLocale, context);
        Audience audience = audiences.all();
        audience.sendMessage(component);
    }

    @Override
    public void broadcastMessage(@NotNull Locale message) {
        broadcastMessage(message, new HashMap<>());
    }

    @Override
    public void shutdown() {
        if (audiences != null) {
            audiences.close();
        }
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
            if (!locale.isEmpty()) {
                // Convert to just the language part (e.g., "en_US" -> "en")
                return locale.split("_")[0].toLowerCase(java.util.Locale.ROOT);
            }
        }
        return defaultLocale;
    }
}
