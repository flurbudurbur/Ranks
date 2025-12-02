package dev.flur.ranks.template.extension.function;

import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Server;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Pebble function for getting player data.
 * <p>
 * Usage in templates:
 * <ul>
 *   <li>{@code {{ player('Steve') }}} - Get player by name</li>
 *   <li>{@code {{ player('Steve').health }}} - Get player health</li>
 *   <li>{@code {{ player('Steve').level }}} - Get player XP level</li>
 * </ul>
 * <p>
 * Returns a map with player properties for easy access in templates.
 */
public class PlayerFunction implements Function {

    private final Server server;

    public PlayerFunction(@NotNull Server server) {
        this.server = server;
    }

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self,
                          EvaluationContext context, int lineNumber) {
        Object nameArg = args.get("name");
        if (nameArg == null) {
            return null;
        }

        String name = nameArg.toString();
        Player player = server.getPlayer(name);

        if (player == null) {
            // Try by UUID
            try {
                UUID uuid = UUID.fromString(name);
                player = server.getPlayer(uuid);
            } catch (IllegalArgumentException ignored) {
                // Not a UUID
            }
        }

        if (player == null) {
            return null;
        }

        return createPlayerMap(player);
    }

    @Override
    public List<String> getArgumentNames() {
        return List.of("name");
    }

    /**
     * Creates a map of player properties for template access.
     */
    private Map<String, Object> createPlayerMap(Player player) {
        Map<String, Object> map = new HashMap<>();

        // Basic info
        map.put("name", player.getName());
        map.put("displayName", PlainTextComponentSerializer.plainText().serialize(player.displayName()));
        map.put("uuid", player.getUniqueId().toString());

        // Stats
        map.put("health", player.getHealth());
        var maxHealthAttr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        map.put("maxHealth", maxHealthAttr != null ? maxHealthAttr.getValue() : 20.0);
        map.put("foodLevel", player.getFoodLevel());
        map.put("level", player.getLevel());
        map.put("exp", player.getExp());
        map.put("totalExperience", player.getTotalExperience());

        // State
        map.put("gameMode", player.getGameMode().name());
        map.put("isFlying", player.isFlying());
        map.put("isSneaking", player.isSneaking());
        map.put("isSprinting", player.isSprinting());
        map.put("isOnline", player.isOnline());
        map.put("isOp", player.isOp());

        // Location
        map.put("world", player.getWorld().getName());
        map.put("x", player.getLocation().getBlockX());
        map.put("y", player.getLocation().getBlockY());
        map.put("z", player.getLocation().getBlockZ());
        map.put("location", player.getLocation());

        // Timestamps
        map.put("firstPlayed", player.getFirstPlayed());
        map.put("lastPlayed", player.getLastPlayed());

        // Keep reference to actual player for filters
        map.put("_player", player);

        return map;
    }
}
