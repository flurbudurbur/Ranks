package dev.flur.ranks.template.service;

import dev.flur.ranks.result.Result;
import dev.flur.ranks.template.TemplateEngine;
import dev.flur.ranks.template.context.TemplateContextBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Service interface for template operations.
 * Provides high-level access to the template engine for use in the IoC container.
 */
public interface TemplateService {

    /**
     * Gets the underlying template engine.
     *
     * @return The template engine
     */
    @NotNull
    TemplateEngine getEngine();

    /**
     * Creates a new context builder.
     *
     * @return A new TemplateContextBuilder
     */
    @NotNull
    TemplateContextBuilder newContext();

    /**
     * Renders a locale message for a player.
     * Automatically resolves the message path based on the player's locale.
     *
     * @param messageKey The message key (e.g., "rankup.success")
     * @param player     The player (used for locale detection)
     * @param context    Additional context variables
     * @return Result containing the rendered Component or error
     */
    @NotNull
    Result<Component> renderMessage(@NotNull String messageKey,
                                    @NotNull Player player,
                                    @NotNull Map<String, Object> context);

    /**
     * Renders a locale message with a specific locale.
     *
     * @param messageKey The message key
     * @param locale     The locale code (e.g., "en", "es")
     * @param context    Context variables
     * @return Result containing the rendered Component or error
     */
    @NotNull
    Result<Component> renderMessage(@NotNull String messageKey,
                                    @NotNull String locale,
                                    @NotNull Map<String, Object> context);

    /**
     * Renders a layout template.
     *
     * @param layoutName The layout name (e.g., "layouts/default")
     * @param context    Context variables
     * @return Result containing the rendered Component or error
     */
    @NotNull
    Result<Component> renderLayout(@NotNull String layoutName,
                                   @NotNull Map<String, Object> context);

    /**
     * Renders an inline template string.
     *
     * @param template The template string
     * @param context  Context variables
     * @return Result containing the rendered Component or error
     */
    @NotNull
    Result<Component> renderInline(@NotNull String template,
                                   @NotNull Map<String, Object> context);

    /**
     * Gets the raw message template string for a key.
     * Useful for checking if a message exists.
     *
     * @param messageKey The message key
     * @param locale     The locale code
     * @return The raw template string, or null if not found
     */
    @org.jetbrains.annotations.Nullable
    String getRawMessage(@NotNull String messageKey, @NotNull String locale);

    /**
     * Checks if a message exists for the given key.
     *
     * @param messageKey The message key
     * @return true if the message exists in at least one locale
     */
    boolean messageExists(@NotNull String messageKey);

    /**
     * Reloads all templates and clears caches.
     */
    void reload();

    /**
     * Shuts down the template service.
     */
    void shutdown();
}
