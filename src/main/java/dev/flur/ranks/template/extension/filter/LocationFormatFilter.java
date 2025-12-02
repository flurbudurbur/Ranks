package dev.flur.ranks.template.extension.filter;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;

/**
 * Pebble filter that formats a Location for display.
 * <p>
 * Usage:
 * <ul>
 *   <li>{@code {{ location | formatLoc }}} - Full format: "world: 100, 64, 200"</li>
 *   <li>{@code {{ location | formatLoc('short') }}} - Short format: "100, 64, 200"</li>
 *   <li>{@code {{ location | formatLoc('full') }}} - Full with decimals: "world: 100.50, 64.00, 200.25"</li>
 * </ul>
 */
public class LocationFormatFilter implements Filter {

    private static final String FORMAT_DEFAULT = "default";
    private static final String FORMAT_SHORT = "short";
    private static final String FORMAT_FULL = "full";
    private static final String FORMAT_BLOCK = "block";

    @Override
    public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                        EvaluationContext context, int lineNumber) {
        if (input == null) {
            return "";
        }

        if (!(input instanceof Location location)) {
            return input.toString();
        }

        String format = FORMAT_DEFAULT;
        if (args.containsKey("format")) {
            format = String.valueOf(args.get("format"));
        } else if (!args.isEmpty()) {
            // First unnamed argument
            Object firstArg = args.values().iterator().next();
            if (firstArg != null) {
                format = firstArg.toString();
            }
        }

        return formatLocation(location, format);
    }

    @Override
    public List<String> getArgumentNames() {
        return List.of("format");
    }

    /**
     * Formats a location based on the format type.
     */
    private String formatLocation(Location location, String format) {
        String worldName = location.getWorld() != null ? location.getWorld().getName() : "unknown";

        return switch (format.toLowerCase()) {
            case FORMAT_SHORT -> String.format("%d, %d, %d",
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ());

            case FORMAT_FULL -> String.format("%s: %.2f, %.2f, %.2f",
                    worldName,
                    location.getX(),
                    location.getY(),
                    location.getZ());

            case FORMAT_BLOCK -> String.format("%s: %d, %d, %d",
                    worldName,
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ());

            default -> String.format("%s: %d, %d, %d",
                    worldName,
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ());
        };
    }
}
