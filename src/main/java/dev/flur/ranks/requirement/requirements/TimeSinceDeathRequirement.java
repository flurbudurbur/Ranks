package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.AnnotatedRequirement;
import dev.flur.ranks.requirement.annotations.RequirementAnnotation;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequirementAnnotation(
        name = "time-since-death",
        maximum = 6,
        usage = "Format: M1 w2 d3 h4 m5 s6 (any order, but at least 1)"
)
public final class TimeSinceDeathRequirement extends AnnotatedRequirement {

    private final long ticksSinceDeath;

    public TimeSinceDeathRequirement(String[] params) {
        super(params);

        // Check if the params array is empty
        if (params.length == 0) {
            throw new IllegalArgumentException("At least one time unit must be specified");
        }

        // Check if there are too many parameters
        if (params.length > 1) {
            throw new IllegalArgumentException("Too many parameters: " + getParameterDescription());
        }

        // Check if the duration is just a number (no units)
        if (params[0].matches("^\\d+$")) {
            throw new IllegalArgumentException("Invalid format (no units)");
        }

        String duration = params[0];

        // Try to parse the duration with the flexible pattern
        long totalTicks = parseDuration(duration);

        if (totalTicks == 0) {
            throw new IllegalArgumentException("At least one time unit must be specified");
        }

        this.ticksSinceDeath = totalTicks;
    }

    private long parseDuration(String duration) {
        // Simple patterns for each time unit
        Pattern monthPattern = Pattern.compile("^M(\\d+)$");
        Pattern weekPattern = Pattern.compile("^w(\\d+)$");
        Pattern dayPattern = Pattern.compile("^d(\\d+)$");
        Pattern hourPattern = Pattern.compile("^h(\\d+)$");
        Pattern minutePattern = Pattern.compile("^m(\\d+)$");
        Pattern secondPattern = Pattern.compile("^s(\\d+)$");

        // Complex pattern for combined format
        Pattern complexPattern = Pattern.compile(
                "^(?:M(\\d+))?" +
                "(?:w(\\d+))?" +
                "(?:d(\\d+))?" +
                "(?:h(\\d+))?" +
                "(?:m(\\d+))?" +
                "(?:s(\\d+))?$"
        );

        Matcher matcher;

        // Try to match with the complex pattern first
        matcher = complexPattern.matcher(duration);
        if (matcher.matches()) {
            return calculateTotalTicks(matcher);
        }

        // Try to match with individual patterns
        matcher = monthPattern.matcher(duration);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group(1)) * 30L * 24 * 60 * 60 * 20; // months to ticks
        }

        matcher = weekPattern.matcher(duration);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group(1)) * 7L * 24 * 60 * 60 * 20; // weeks to ticks
        }

        matcher = dayPattern.matcher(duration);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group(1)) * 24L * 60 * 60 * 20; // days to ticks
        }

        matcher = hourPattern.matcher(duration);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group(1)) * 60L * 60 * 20; // hours to ticks
        }

        matcher = minutePattern.matcher(duration);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group(1)) * 60L * 20; // minutes to ticks
        }

        matcher = secondPattern.matcher(duration);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group(1)) * 20; // seconds to ticks
        }

        throw new IllegalArgumentException("Invalid duration format");
    }

    private long calculateTotalTicks(Matcher matcher) {
        long totalTicks = 0;

        // Validate time values
        for (int i = 1; i <= 6; i++) {
            String value = matcher.group(i);
            if (value != null && Long.parseLong(value) < 0) {
                throw new IllegalArgumentException("Time values cannot be negative");
            }
        }

        // Convert to ticks (20 ticks per second)
        if (matcher.group(1) != null) {
            totalTicks += Long.parseLong(matcher.group(1)) * 30L * 24 * 60 * 60 * 20; // months to ticks
        }
        if (matcher.group(2) != null) {
            totalTicks += Long.parseLong(matcher.group(2)) * 7L * 24 * 60 * 60 * 20; // weeks to ticks
        }
        if (matcher.group(3) != null) {
            totalTicks += Long.parseLong(matcher.group(3)) * 24L * 60 * 60 * 20; // days to ticks
        }
        if (matcher.group(4) != null) {
            totalTicks += Long.parseLong(matcher.group(4)) * 60L * 60 * 20; // hours to ticks
        }
        if (matcher.group(5) != null) {
            totalTicks += Long.parseLong(matcher.group(5)) * 60L * 20; // minutes to ticks
        }
        if (matcher.group(6) != null) {
            totalTicks += Long.parseLong(matcher.group(6)) * 20; // seconds to ticks
        }
        return totalTicks;
    }

    @Contract(pure = true)
    private int @NotNull [] deconstructLong(long value) {
        if (value == 0) {
            return new int[]{0};
        }

        if (value < 0) {
            throw new IllegalArgumentException("Negative values not supported");
        }

        int fullChunks = (int) (value / Integer.MAX_VALUE);
        int remainder = (int) (value % Integer.MAX_VALUE);

        int arraySize = fullChunks + (remainder > 0 ? 1 : 0);
        int[] result = new int[arraySize];

        for (int i = 0; i < fullChunks; i++) {
            result[i] = Integer.MAX_VALUE;
        }

        if (remainder > 0) {
            result[fullChunks] = remainder;
        }

        return result;
    }

    @Override
    public boolean meetsRequirement(@NotNull Player player) {
        int actualTicks = Objects.requireNonNull(player.getPlayer()).getStatistic(Statistic.TIME_SINCE_DEATH);

        // If requirement exceeds int range, we need to handle it specially
        if (this.ticksSinceDeath > Integer.MAX_VALUE) {
            // For very large requirements, we need to check if the player's ticks
            // are at least as large as what can be represented in an int
            if (actualTicks < 0) {
                // If actualTicks is negative, it means it has overflowed,
                // which means it's actually a very large positive number in unsigned context
                return true;
            }

            // If the requirement is more than twice the max int value,
            // then even Integer.MAX_VALUE wouldn't be enough
            if (this.ticksSinceDeath > 2L * Integer.MAX_VALUE) {
                return false;
            }

            // For requirements between MAX_VALUE and 2*MAX_VALUE,
            // we need to check if actualTicks is at least (requirement - MAX_VALUE)
            long minRequired = this.ticksSinceDeath - Integer.MAX_VALUE;
            return actualTicks >= minRequired;
        }

        return actualTicks >= (int) this.ticksSinceDeath;
    }

    @Override
    protected double validateRequirementAmount(String[] params) {
        return 1; // to not fail during runtime.
    }

    @Override
    public String toString() {
        return "time-since-death: " + this.ticksSinceDeath + " ticks";
    }
}
