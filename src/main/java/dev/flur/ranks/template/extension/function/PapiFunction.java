package dev.flur.ranks.template.extension.function;

import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Pebble function for PlaceholderAPI integration.
 * Uses reflection to avoid hard dependency on PlaceholderAPI.
 * <p>
 * Usage in templates:
 * <ul>
 *   <li>{@code {{ papi('vault_eco_balance') }}} - Uses player from context</li>
 *   <li>{@code {{ papi('player_health', player) }}} - Explicit player</li>
 * </ul>
 */
public class PapiFunction implements Function {

    private static final String PAPI_CLASS = "me.clip.placeholderapi.PlaceholderAPI";
    private static Method setPlaceholdersMethod;
    private static boolean papiAvailable;

    static {
        try {
            Class<?> papiClass = Class.forName(PAPI_CLASS);
            setPlaceholdersMethod = papiClass.getMethod("setPlaceholders", OfflinePlayer.class, String.class);
            papiAvailable = true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            papiAvailable = false;
        }
    }

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self,
                          EvaluationContext context, int lineNumber) {
        // Get placeholder name
        Object placeholderArg = args.get("placeholder");
        if (placeholderArg == null) {
            return "";
        }

        String placeholder = placeholderArg.toString();

        // Get player from args or context
        OfflinePlayer player = getPlayer(args, context);

        if (player == null) {
            // Return raw placeholder if no player available
            return "%" + placeholder + "%";
        }

        // Ensure placeholder has % delimiters
        String fullPlaceholder;
        if (placeholder.startsWith("%") && placeholder.endsWith("%")) {
            fullPlaceholder = placeholder;
        } else {
            fullPlaceholder = "%" + placeholder + "%";
        }

        if (!papiAvailable || setPlaceholdersMethod == null) {
            return fullPlaceholder;
        }

        try {
            return setPlaceholdersMethod.invoke(null, player, fullPlaceholder);
        } catch (Exception e) {
            // Return raw placeholder on error
            return fullPlaceholder;
        }
    }

    @Override
    public List<String> getArgumentNames() {
        return List.of("placeholder", "player");
    }

    /**
     * Checks if PlaceholderAPI is available.
     *
     * @return true if PAPI is available
     */
    public static boolean isPapiAvailable() {
        return papiAvailable;
    }

    /**
     * Gets the player from args or context.
     */
    private OfflinePlayer getPlayer(Map<String, Object> args, EvaluationContext context) {
        // First check explicit player argument
        Object playerArg = args.get("player");
        if (playerArg instanceof OfflinePlayer) {
            return (OfflinePlayer) playerArg;
        }

        // Then check context for player
        Object contextPlayer = context.getVariable("player");
        if (contextPlayer instanceof OfflinePlayer) {
            return (OfflinePlayer) contextPlayer;
        }

        // Check for PlayerContext wrapper
        if (contextPlayer != null) {
            // Try to get wrapped player via reflection or known wrapper
            try {
                var method = contextPlayer.getClass().getMethod("getPlayer");
                Object result = method.invoke(contextPlayer);
                if (result instanceof OfflinePlayer) {
                    return (OfflinePlayer) result;
                }
            } catch (Exception ignored) {
                // Not a wrapper
            }
        }

        return null;
    }
}
