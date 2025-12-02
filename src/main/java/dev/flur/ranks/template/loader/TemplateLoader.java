package dev.flur.ranks.template.loader;

import dev.flur.ranks.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

/**
 * Interface for loading template sources.
 */
public interface TemplateLoader {

    /**
     * Gets the template content as a string.
     *
     * @param templateName Name/path of the template
     * @return Result containing the template content or error
     */
    @NotNull
    Result<String> getContent(@NotNull String templateName);

    /**
     * Checks if a template exists.
     *
     * @param templateName Name/path of the template
     * @return true if the template exists
     */
    boolean exists(@NotNull String templateName);

    /**
     * Gets the last modified time for cache invalidation.
     * Returns null if not applicable (e.g., for in-memory templates).
     *
     * @param templateName Name/path of the template
     * @return Last modified instant or null
     */
    @Nullable
    Instant getLastModified(@NotNull String templateName);

    /**
     * Creates a unique cache key for the template.
     *
     * @param templateName Name/path of the template
     * @return Unique cache key
     */
    @NotNull
    String createCacheKey(@NotNull String templateName);
}
