package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.AnnotatedRequirement;
import dev.flur.ranks.requirement.RequirementName;
import dev.flur.ranks.requirement.RequirementParams;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequirementName("time-since-death")
@RequirementParams(maximum = 6, usage = "Format: M1 w2 d3 h4 m5 s6 (any order, but at least 1)")
public final class TimeSinceDeathRequirement extends AnnotatedRequirement {

    private final long timeSinceDeath;

    private final Pattern DURATION_PATTERN = Pattern.compile(
            "^(?:M([0-9]+)\\s*)?" +                 // M: 0-any number
            "(?:w([0-3])\\s*)?" +                   // w: 0–3
            "(?:d([0-6])\\s*)?" +                   // d: 0–6
            "(?:h([0-9]|1[0-9]|2[0-3])\\s*)?" +     // h: 0–23
            "(?:m([0-9]|[1-5][0-9])\\s*)?" +        // m: 0–59
            "(?:s([0-9]|[1-5][0-9]))?$"             // s: 0–59
    );

    public TimeSinceDeathRequirement(String[] params) {
        super(params);

        // Check if the params array is empty
        if (params.length == 0) {
            throw new IllegalArgumentException("At least one time unit must be specified");
        }

        // Check if the duration is just a number (no units)
        if (params[0].matches("^\\d+$")) {
            throw new IllegalArgumentException("Invalid format (no units)");
        }

        String duration = params[0];
        Matcher matcher = DURATION_PATTERN.matcher(duration);

        long totalMillis = 0;

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid duration format");
        }

        // Validate time values
        for (int i = 1; i <= 6; i++) {
            String value = matcher.group(i);
            if (value != null && Long.parseLong(value) < 0) {
                throw new IllegalArgumentException("Time values cannot be negative");
            }
        }

        if (matcher.group(1) != null) {
            totalMillis += Long.parseLong(matcher.group(1)) * 30L * 24 * 60 * 60 * 1000; // months
        }
        if (matcher.group(2) != null) {
            totalMillis += Long.parseLong(matcher.group(2)) * 7L * 24 * 60 * 60 * 1000; // weeks
        }
        if (matcher.group(3) != null) {
            totalMillis += Long.parseLong(matcher.group(3)) * 24L * 60 * 60 * 1000; // days
        }
        if (matcher.group(4) != null) {
            totalMillis += Long.parseLong(matcher.group(4)) * 60L * 60 * 1000; // hours
        }
        if (matcher.group(5) != null) {
            totalMillis += Long.parseLong(matcher.group(5)) * 60L * 1000; // minutes
        }
        if (matcher.group(6) != null) {
            totalMillis += Long.parseLong(matcher.group(6)) * 1000; // seconds
        }

        if (totalMillis == 0) {
            throw new IllegalArgumentException("At least one time unit must be specified");
        }

        this.timeSinceDeath = totalMillis;
    }

    @Override
    public boolean meetsRequirement(@NotNull OfflinePlayer player) {
        return Objects.requireNonNull(player.getPlayer()).getStatistic(org.bukkit.Statistic.TIME_SINCE_DEATH) >= this.timeSinceDeath;
    }

    @Override
    protected double validateRequirementAmount(String[] params) {
        return 1; // to not fail during runtime.
    }
}
