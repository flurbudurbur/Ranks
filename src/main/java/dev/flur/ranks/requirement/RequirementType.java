package dev.flur.ranks.requirement;

import dev.flur.ranks.requirement.requirements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Enum defining all built-in requirement types.
 * This is the single source of truth for requirement registration.
 */
public enum RequirementType {
    MONEY("money", 1, 1, "amount", MoneyRequirement::new),
    XP_LEVEL("xp-level", 1, 1, "level", XpLevelRequirement::new),
    BLOCK_BREAK("block-break", 2, Integer.MAX_VALUE, "block1 [block2 ...] amount", BlockBreakRequirement::new),
    ITEM_USE("item-use", 2, 10, "item1 [item2 ...] amount", ItemUseRequirement::new),
    TIME_SINCE_DEATH("time-since-death", 1, 6, "M1 w2 d3 h4 m5 s6 (any order, but at least 1)", TimeSinceDeathRequirement::new),
    DEATHS("deaths", 1, 1, "<count>", DeathsRequirement::new);

    private final String key;
    private final int minParams;
    private final int maxParams;
    private final String usage;
    private final Function<String[], Requirement> factory;

    RequirementType(String key, int minParams, int maxParams, String usage, Function<String[], Requirement> factory) {
        this.key = key;
        this.minParams = minParams;
        this.maxParams = maxParams;
        this.usage = usage;
        this.factory = factory;
    }

    /**
     * Gets the key used in configuration files.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the minimum number of parameters required.
     */
    public int getMinParams() {
        return minParams;
    }

    /**
     * Gets the maximum number of parameters allowed.
     */
    public int getMaxParams() {
        return maxParams;
    }

    /**
     * Gets the usage description for error messages.
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Validates the parameter count against min/max constraints.
     *
     * @param params the parameters to validate
     * @throws IllegalArgumentException if param count is out of bounds
     */
    public void validateParams(String[] params) {
        if (params.length < minParams) {
            throw new IllegalArgumentException("Too few arguments: " + usage);
        }
        if (params.length > maxParams) {
            throw new IllegalArgumentException("Too many arguments: " + usage);
        }
    }

    /**
     * Creates a requirement instance after validating parameters.
     *
     * @param params the parameters for the requirement
     * @return the created requirement
     * @throws IllegalArgumentException if validation fails
     */
    @NotNull
    public Requirement create(String[] params) {
        validateParams(params);
        return factory.apply(params);
    }

    /**
     * Looks up a RequirementType by its key.
     *
     * @param key the key to look up
     * @return the matching RequirementType, or null if not found
     */
    @Nullable
    public static RequirementType fromKey(String key) {
        for (RequirementType type : values()) {
            if (type.key.equals(key)) {
                return type;
            }
        }
        return null;
    }
}
