package dev.flur.ranks.service;

import dev.flur.ranks.message.Messages;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Service interface for managing messages and localization.
 */
public interface MessageService {

    /**
     * Reloads all message configurations.
     */
    void reload();

    /**
     * Gets a formatted message for the given key, locale, and context.
     *
     * @param key     The message key
     * @param locale  The locale to use
     * @param context The context variables for templating
     * @return The formatted message as a Component
     */
    Component getMessage(String key, String locale, Map<String, Object> context);

    /**
     * Gets a formatted message for the given message enum, locale, and context.
     *
     * @param message The message enum
     * @param locale  The locale to use
     * @param context The context variables for templating
     * @return The formatted message as a Component
     */
    Component getMessage(Messages message, String locale, Map<String, Object> context);

    /**
     * Sends a message to a command sender.
     *
     * @param sender  The command sender to send the message to
     * @param message The message enum
     * @param context The context variables for templating
     */
    void sendMessage(@NotNull CommandSender sender, @NotNull Messages message, Map<String, Object> context);

    /**
     * Sends a message to a command sender with an empty context.
     *
     * @param sender  The command sender to send the message to
     * @param message The message enum
     */
    void sendMessage(@NotNull CommandSender sender, @NotNull Messages message);

    /**
     * Broadcasts a message to all players on the server.
     *
     * @param message The message enum
     * @param context The context variables for templating
     */
    void broadcastMessage(@NotNull Messages message, Map<String, Object> context);

    /**
     * Broadcasts a message to all players on the server with an empty context.
     *
     * @param message The message enum
     */
    void broadcastMessage(@NotNull Messages message);

    /**
     * Shuts down the message service and releases resources.
     */
    void shutdown();
}