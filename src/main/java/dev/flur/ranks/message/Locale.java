package dev.flur.ranks.message;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum containing all message keys used in the plugin.
 * Each enum constant represents a message that can be retrieved from the locale files.
 */
public enum Locale {
    // General messages
    PLAYER_ONLY("general.player-only", "player.only"),
    ERROR_OCCURRED("general.error-occurred", "error.occurred"),
    NO_PERMISSION("general.no-permission", "permission.denied"),

    // Rankup command messages
    HIGHEST_RANK("rankup.highest-rank", "highest.rank"),
    MULTIPLE_RANKS("rankup.multiple-ranks", "multiple.ranks"),
    RANK_LIST_ITEM("rankup.rank-list-item", "rank.list.item"),
    RANKUP_SUCCESS("rankup.success", "success"),
    RANKUP_FAILURE("rankup.requirements-not-met", "requirements.not.met"),
    REQUIREMENT_ITEM("rankup.requirement-item", "requirement.item"),
    INVALID_RANK("rankup.invalid-rank", "invalid.rank"),
    CURRENT_RANK_ERROR("rankup.current-rank-error", "current.rank.error"),
    RANKUP_FAILED("rankup.failed", "rankup.failed"),
    RANKUP_BROADCAST("rankup.broadcast", "rankup.broadcast"),

    // Admin command messages
    RELOAD_COMPLETED("admin.reload.completed", "reload.success"),
    RELOAD_FULL_COMPLETED("admin.reload.full-completed", "reload.full.success");

    private static final Map<String, Locale> KEY_MAP = new HashMap<>();

    static {
        // Build the key mapping including all aliases
        for (Locale message : values()) {
            // Add primary key
            KEY_MAP.put(message.primaryKey, message);

            // Add all aliases
            for (String alias : message.aliases) {
                KEY_MAP.put(alias, message);
            }
        }
    }

    private final String primaryKey;
    private final String[] aliases;

    Locale(String primaryKey, String... aliases) {
        this.primaryKey = primaryKey;
        this.aliases = aliases;
    }

    // Find enum by any key (primary or alias)
    public static @NotNull Locale fromKey(String key) {
        Locale message = KEY_MAP.get(key);
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
