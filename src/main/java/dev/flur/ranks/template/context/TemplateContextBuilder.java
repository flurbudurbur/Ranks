package dev.flur.ranks.template.context;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Fluent builder for creating template contexts.
 * <p>
 * Example usage:
 * <pre>{@code
 * Map<String, Object> context = TemplateContextBuilder.create()
 *     .withPlayer(player)
 *     .with("targetRank", "Knight")
 *     .with("cost", 5000)
 *     .build();
 * }</pre>
 */
public class TemplateContextBuilder {

    private final Map<String, Object> context;

    private TemplateContextBuilder() {
        this.context = new HashMap<>();
    }

    /**
     * Creates a new context builder.
     *
     * @return A new builder instance
     */
    @NotNull
    public static TemplateContextBuilder create() {
        return new TemplateContextBuilder();
    }

    /**
     * Adds a value to the context.
     *
     * @param key   The context key
     * @param value The value
     * @return This builder
     */
    @NotNull
    public TemplateContextBuilder with(@NotNull String key, Object value) {
        context.put(key, value);
        return this;
    }

    /**
     * Adds a player to the context with common properties.
     * Sets "player" to a PlayerContext wrapper and adds convenience properties.
     *
     * @param player The player
     * @return This builder
     */
    @NotNull
    public TemplateContextBuilder withPlayer(@NotNull Player player) {
        MinecraftContext.PlayerContext playerContext = new MinecraftContext.PlayerContext(player);
        context.put("player", playerContext);
        context.put("playerName", player.getName());
        context.put("playerDisplayName", PlainTextComponentSerializer.plainText().serialize(player.displayName()));
        context.put("playerUuid", player.getUniqueId().toString());
        return this;
    }

    /**
     * Adds a player with a custom key.
     *
     * @param key    The context key
     * @param player The player
     * @return This builder
     */
    @NotNull
    public TemplateContextBuilder withPlayer(@NotNull String key, @NotNull Player player) {
        context.put(key, new MinecraftContext.PlayerContext(player));
        return this;
    }

    /**
     * Adds a location to the context.
     *
     * @param key      The context key
     * @param location The location
     * @return This builder
     */
    @NotNull
    public TemplateContextBuilder withLocation(@NotNull String key, @NotNull Location location) {
        context.put(key, new MinecraftContext.LocationContext(location));
        return this;
    }

    /**
     * Adds a location with common property names.
     * Sets the key to a LocationContext and adds world, x, y, z with prefixed keys.
     *
     * @param key      The base key
     * @param location The location
     * @return This builder
     */
    @NotNull
    public TemplateContextBuilder withLocationExpanded(@NotNull String key, @NotNull Location location) {
        MinecraftContext.LocationContext locContext = new MinecraftContext.LocationContext(location);
        context.put(key, locContext);
        context.put(key + "World", locContext.getWorld());
        context.put(key + "X", locContext.getBlockX());
        context.put(key + "Y", locContext.getBlockY());
        context.put(key + "Z", locContext.getBlockZ());
        return this;
    }

    /**
     * Adds an item to the context.
     *
     * @param key  The context key
     * @param item The item
     * @return This builder
     */
    @NotNull
    public TemplateContextBuilder withItem(@NotNull String key, @NotNull ItemStack item) {
        context.put(key, new MinecraftContext.ItemContext(item));
        return this;
    }

    /**
     * Adds a rank name to the context.
     *
     * @param key      The context key
     * @param rankName The rank name
     * @return This builder
     */
    @NotNull
    public TemplateContextBuilder withRank(@NotNull String key, @NotNull String rankName) {
        context.put(key, rankName);
        return this;
    }

    /**
     * Adds the locale to the context.
     *
     * @param locale The locale code (e.g., "en", "es")
     * @return This builder
     */
    @NotNull
    public TemplateContextBuilder withLocale(@NotNull String locale) {
        context.put("locale", locale);
        return this;
    }

    /**
     * Adds all entries from an existing map.
     *
     * @param values The values to add
     * @return This builder
     */
    @NotNull
    public TemplateContextBuilder withAll(@NotNull Map<String, Object> values) {
        context.putAll(values);
        return this;
    }

    /**
     * Adds a value only if it's not null.
     *
     * @param key   The context key
     * @param value The value (can be null)
     * @return This builder
     */
    @NotNull
    public TemplateContextBuilder withIfPresent(@NotNull String key, Object value) {
        if (value != null) {
            context.put(key, value);
        }
        return this;
    }

    /**
     * Builds the context map.
     *
     * @return An unmodifiable copy of the context
     */
    @NotNull
    public Map<String, Object> build() {
        return new HashMap<>(context);
    }

    /**
     * Gets the current context (mutable view).
     * Prefer using {@link #build()} for immutability.
     *
     * @return The context map
     */
    @NotNull
    public Map<String, Object> getContext() {
        return context;
    }
}
