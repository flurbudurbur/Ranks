package dev.flur.ranks.template.engine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

/**
 * Interface for template caching.
 */
public interface TemplateCache {

    /**
     * Gets a cached template.
     *
     * @param cacheKey The cache key
     * @return The cached template or null if not found
     */
    @Nullable
    CompiledTemplate get(@NotNull String cacheKey);

    /**
     * Puts a template in the cache.
     *
     * @param cacheKey The cache key
     * @param template The compiled template
     */
    void put(@NotNull String cacheKey, @NotNull CompiledTemplate template);

    /**
     * Invalidates a specific template.
     *
     * @param cacheKey The cache key to invalidate
     */
    void invalidate(@NotNull String cacheKey);

    /**
     * Checks if a cached template is still valid.
     *
     * @param cacheKey       The cache key
     * @param sourceModified The source file modification time
     * @return true if the cached template is valid
     */
    boolean isValid(@NotNull String cacheKey, @Nullable Instant sourceModified);

    /**
     * Clears all cached templates.
     */
    void clear();

    /**
     * Gets cache statistics.
     *
     * @return Cache statistics
     */
    @NotNull
    CacheStats getStats();

    /**
     * Cache statistics record.
     */
    record CacheStats(long hits, long misses, int size, int maxSize) {

        /**
         * Gets the hit rate as a percentage.
         *
         * @return Hit rate (0.0 to 1.0)
         */
        public double getHitRate() {
            long total = hits + misses;
            return total == 0 ? 0.0 : (double) hits / total;
        }
    }
}
