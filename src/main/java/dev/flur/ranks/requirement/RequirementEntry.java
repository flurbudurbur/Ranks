package dev.flur.ranks.requirement;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Entry for a registered requirement type (used for custom/external requirements).
 */
public record RequirementEntry(
        @NotNull String key,
        int minParams,
        int maxParams,
        @NotNull String usage,
        @NotNull Function<String[], Requirement> factory
) {
    public void validateParams(String[] params) {
        if (params.length < minParams) {
            throw new IllegalArgumentException("Too few arguments: " + usage);
        }
        if (params.length > maxParams) {
            throw new IllegalArgumentException("Too many arguments: " + usage);
        }
    }

    @NotNull
    public Requirement create(String[] params) {
        validateParams(params);
        return factory.apply(params);
    }
}
