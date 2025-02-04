package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.Requirement;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SurviveRequirement implements Requirement {

    private long timeSinceDeath;

    public SurviveRequirement(String[] params) {
        try {
            long totalMillis = 0;

            Pattern pattern = Pattern.compile("(\\d+)\\s*([dhms])", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(Arrays.toString(params));

            while (matcher.find()) {
                long value = Long.parseLong(matcher.group(1));
                String unit = matcher.group(2);
                switch (unit) {
                    case "d":
                        totalMillis += value * 24L * 60 * 60 * 1000;
                        break;
                    case "h":
                        totalMillis += value * 60L * 60 * 1000;
                        break;
                    case "m":
                        totalMillis += value * 60L * 1000L;
                        break;
                    case "s":
                        totalMillis += value * 1000;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid unit: " + unit);
                }
            }
            timeSinceDeath = totalMillis;

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean meetsRequirement(@NotNull OfflinePlayer player) {

    }

    @Override
    public String getName() {
        return "time-since-death";
    }
}
