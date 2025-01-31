package dev.flur.ranks.requirement;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.requirement.requirements.MoneyRequirement;
import dev.flur.ranks.requirement.requirements.XpLevelRequirement;

import java.util.Map;
import java.util.function.Function;

public class RequirementFactory {
    private static final Map<String, Function<String[], Requirement>> registry = Map.of(
            "money", params -> new MoneyRequirement(params),
            "xp-level", params -> new XpLevelRequirement(params)
    );

    public static Requirement registerRequirement(String input) {
        String[] token = input.split(" ");
        if (token.length == 0) throw new IllegalArgumentException("Invalid input");
        String key = token[0].toLowerCase();
        Function<String[], Requirement> constructor = registry.get(key);
        if (constructor == null) throw new IllegalArgumentException("Invalid input");

        String[] params = new String[token.length - 1];
        System.arraycopy(token, 1, params, 0, token.length - 1);
        return constructor.apply(params);
    }
}
