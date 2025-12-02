package dev.flur.ranks.template.extension.filter;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Pebble filter that formats durations in a human-readable format.
 * <p>
 * Usage:
 * <ul>
 *   <li>{@code {{ seconds | formatTime }}} - From seconds: "2h 30m 15s"</li>
 *   <li>{@code {{ duration | formatTime }}} - From Duration object</li>
 *   <li>{@code {{ seconds | formatTime('short') }}} - Short format: "2:30:15"</li>
 *   <li>{@code {{ seconds | formatTime('long') }}} - Long format: "2 hours 30 minutes 15 seconds"</li>
 * </ul>
 */
public class TimeFormatFilter implements Filter {

    private static final String FORMAT_DEFAULT = "default";
    private static final String FORMAT_SHORT = "short";
    private static final String FORMAT_LONG = "long";
    private static final String FORMAT_COMPACT = "compact";

    @Override
    public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                        EvaluationContext context, int lineNumber) {
        if (input == null) {
            return "";
        }

        long totalSeconds;

        if (input instanceof Duration duration) {
            totalSeconds = duration.getSeconds();
        } else if (input instanceof Number number) {
            totalSeconds = number.longValue();
        } else {
            try {
                totalSeconds = Long.parseLong(input.toString());
            } catch (NumberFormatException e) {
                return input.toString();
            }
        }

        // Get format style
        String format = FORMAT_DEFAULT;
        if (args.containsKey("style")) {
            format = String.valueOf(args.get("style"));
        } else if (!args.isEmpty()) {
            Object firstArg = args.values().iterator().next();
            if (firstArg != null) {
                format = firstArg.toString();
            }
        }

        return formatDuration(totalSeconds, format);
    }

    @Override
    public List<String> getArgumentNames() {
        return List.of("style");
    }

    /**
     * Formats seconds into a human-readable duration string.
     */
    private String formatDuration(long totalSeconds, String format) {
        if (totalSeconds < 0) {
            totalSeconds = 0;
        }

        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return switch (format.toLowerCase()) {
            case FORMAT_SHORT -> formatShort(days, hours, minutes, seconds);
            case FORMAT_LONG -> formatLong(days, hours, minutes, seconds);
            case FORMAT_COMPACT -> formatCompact(days, hours, minutes, seconds);
            default -> formatDefault(days, hours, minutes, seconds);
        };
    }

    /**
     * Default format: 2d 5h 30m 15s
     */
    private String formatDefault(long days, long hours, long minutes, long seconds) {
        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days).append("d ");
        }
        if (hours > 0 || days > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0 || days > 0) {
            sb.append(minutes).append("m ");
        }
        sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    /**
     * Short format: 2:05:30:15 or 5:30:15 or 30:15
     */
    private String formatShort(long days, long hours, long minutes, long seconds) {
        if (days > 0) {
            return String.format("%d:%02d:%02d:%02d", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    /**
     * Long format: 2 days 5 hours 30 minutes 15 seconds
     */
    private String formatLong(long days, long hours, long minutes, long seconds) {
        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days).append(days == 1 ? " day " : " days ");
        }
        if (hours > 0) {
            sb.append(hours).append(hours == 1 ? " hour " : " hours ");
        }
        if (minutes > 0) {
            sb.append(minutes).append(minutes == 1 ? " minute " : " minutes ");
        }
        if (seconds > 0 || sb.length() == 0) {
            sb.append(seconds).append(seconds == 1 ? " second" : " seconds");
        }

        return sb.toString().trim();
    }

    /**
     * Compact format: Shows only the two largest units
     */
    private String formatCompact(long days, long hours, long minutes, long seconds) {
        if (days > 0) {
            return String.format("%dd %dh", days, hours);
        } else if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
}
