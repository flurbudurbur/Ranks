package dev.flur.ranks.message;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum containing all message keys used in the plugin.
 * Each enum constant represents a message that can be retrieved from the locale files.
 */
public enum Messages {
    // General messages
    PLAYER_ONLY("player-only", "player.only"),
    ERROR_OCCURRED("error-occurred", "error.occurred"),
    NO_PERMISSION("no-permission", "permission.denied"),

    // Admin command messages
    RELOAD_COMPLETED("reload.completed", "reload.success"),
    RELOAD_FULL_COMPLETED("reload.full-completed", "reload.full.success"),

    // Rankup command messages
    HIGHEST_RANK("rank.highest-rank", "rank.highest", "rank.reached-highest"),
    MULTIPLE_RANKS("rank.multiple-ranks", "multiple.ranks"),
    RANK_LIST_ITEM("rank.rank-list-item", "rank.list.item"),
    RANKUP_SUCCESS("rank.success", "success"),
    UNMET_REQUIREMENTS("rank.unmet-requirements"),
    REQUIREMENT_ITEM("rank.requirement-item"),
    INVALID_RANK("rank.invalid-rank"),
    CURRENT_RANK_ERROR("rank.current-rank-error", "current.rank.error"),
    RANKUP_FAILED("rank.failed", "rank.failed"),
    RANKUP_BROADCAST("rank.broadcast", "rank.broadcast");

    private static final Map<String, Messages> KEY_MAP;

    static {
        // Build the key mapping including all aliases
        Map<String, Messages> keyMap = new HashMap<>();
        for (Messages message : values()) {
            // Add primary key
            keyMap.put(message.primaryKey, message);

            // Add all aliases
            for (String alias : message.aliases) {
                keyMap.put(alias, message);
            }
        }
        // Make immutable for thread safety
        KEY_MAP = Map.copyOf(keyMap);
    }

    private final String primaryKey;
    private final String[] aliases;

    Messages(String primaryKey, String... aliases) {
        this.primaryKey = primaryKey;
        this.aliases = aliases;
    }

    // Find enum by any key (primary or alias)
    public static @NotNull Messages fromKey(String key) {
        Messages message = KEY_MAP.get(key);
        if (message == null) {
            throw new IllegalArgumentException("Unknown key: " + key);
        }
        return message;
    }

    // Check if a key exists (primary or alias)
    public static boolean hasKey(String key) {
        return KEY_MAP.containsKey(key);
    }

    // Get all possible keys for debugging
    public static String @NotNull [] getAllPossibleKeys() {
        return KEY_MAP.keySet().toArray(new String[0]);
    }

    public String getKey() {
        return primaryKey;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public String[] getAliases() {
        return aliases.clone(); // Return copy to prevent modification
    }

    public String @NotNull [] getAllKeys() {
        String[] allKeys = new String[aliases.length + 1];
        allKeys[0] = primaryKey;
        System.arraycopy(aliases, 0, allKeys, 1, aliases.length);
        return allKeys;
    }
}
