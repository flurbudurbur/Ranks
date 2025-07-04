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
            "^(?:M([0-9]|1[01])\\s*)?" +         // M: 0–11
            "(?:w([0-3])\\s*)?" +                // w: 0–3
            "(?:d([0-6])\\s*)?" +                // d: 0–6
            "(?:h([0-9]|1[0-9]|2[0-3])\\s*)?" +  // h: 0–23
            "(?:m([0-9]|[1-5][0-9])\\s*)?" +     // m: 0–59
            "(?:s([0-9]|[1-5][0-9]))?$"          // s: 0–59
    );

    public TimeSinceDeathRequirement(String[] params) {
        super(params);
        
        try {
            String duration = params[0];
            Matcher matcher = DURATION_PATTERN.matcher(duration);

            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid duration format");
            }

            long totalMillis = 0;

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

            this.timeSinceDeath = totalMillis;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean meetsRequirement(@NotNull OfflinePlayer player) {
        return Objects.requireNonNull(player.getPlayer()).getStatistic(org.bukkit.Statistic.TIME_SINCE_DEATH) >= this.timeSinceDeath;
    }
}