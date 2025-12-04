package dev.flur.ranks.service.services;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.message.Messages;
import dev.flur.ranks.result.Result;
import dev.flur.ranks.service.MessageService;
import dev.flur.ranks.template.service.TemplateService;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Default implementation of the MessageService interface.
 * Delegates template rendering to TemplateService while managing message delivery via Paper's native Adventure API.
 */
public class DefaultMessageService implements MessageService {
    private static final String DEFAULT_LOCALE = "en";

    private final TemplateService templateService;
    private final String defaultLocale;
    private final Logger logger;

    /**
     * Constructor with injectable dependencies for testing.
     *
     * @param plugin          The plugin instance
     * @param templateService The template service for rendering messages
     */
    public DefaultMessageService(@NotNull Ranks plugin,
                                 @NotNull TemplateService templateService) {
        this.templateService = templateService;
        this.logger = plugin.getLogger();

        // Load default locale from config
        String configLocale = plugin.getConfig().getString("locale", DEFAULT_LOCALE);
        this.defaultLocale = configLocale.isEmpty() ? DEFAULT_LOCALE : configLocale;
    }

    @Override
    public void reload() {
        templateService.reload();
    }

    @Override
    public Component getMessage(String key, String locale, Map<String, Object> context) {
        Result<Component> result = templateService.renderMessage(key, locale, context);
        if (result.isSuccess()) {
            return result.getValue();
        }
        logger.warning("Failed to render message '" + key + "': " + result.getErrorMessage());
        return Component.text("Missing: " + key);
    }

    @Override
    public Component getMessage(Messages message, String locale, Map<String, Object> context) {
        return getMessage(message.getKey(), locale, context);
    }

    @Override
    public void sendMessage(@NotNull CommandSender sender, @NotNull Messages message, Map<String, Object> context) {
        String locale = getLocaleForSender(sender);
        Component component = getMessage(message, locale, context);
        sender.sendMessage(component);
    }

    @Override
    public void sendMessage(@NotNull CommandSender sender, @NotNull Messages message) {
        sendMessage(sender, message, Collections.emptyMap());
    }

    @Override
    public void broadcastMessage(@NotNull Messages message, Map<String, Object> context) {
        Component component = getMessage(message, defaultLocale, context);
        Bukkit.getServer().broadcast(component);
    }

    @Override
    public void broadcastMessage(@NotNull Messages message) {
        broadcastMessage(message, Collections.emptyMap());
    }

    @Override
    public void shutdown() {
        // No cleanup needed - Paper manages Adventure lifecycle
    }

    /**
     * Gets the appropriate locale for a command sender.
     *
     * @param sender The command sender
     * @return The locale to use for this sender
     */
    private String getLocaleForSender(@NotNull CommandSender sender) {
        if (sender instanceof Player player) {
            Locale locale = player.locale();
            if (locale != null) {
                return locale.getLanguage();
            }
        }
        return defaultLocale;
    }
}
