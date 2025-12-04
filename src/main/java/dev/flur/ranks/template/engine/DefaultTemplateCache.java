package dev.flur.ranks.template.engine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Lock-free cache implementation for compiled templates.
 * Uses ConcurrentHashMap for thread-safe concurrent access without synchronization.
 * Eviction is based on compile time (oldest first) rather than true LRU for better concurrency.
 */
public class DefaultTemplateCache implements TemplateCache {

    private final int maxSize;
    private final long ttlMillis;
    private final boolean hotReloadEnabled;
    private final ConcurrentHashMap<String, CompiledTemplate> cache;
    private final AtomicLong hits = new AtomicLong(0);
    private final AtomicLong misses = new AtomicLong(0);

    /**
     * Creates a new template cache.
     *
     * @param maxSize          Maximum number of templates to cache
     * @param ttl              Time-to-live for cached templates, or null for infinite
     * @param hotReloadEnabled Whether to check file modification times
     */
    public DefaultTemplateCache(int maxSize, @Nullable Duration ttl, boolean hotReloadEnabled) {
        this.maxSize = maxSize;
        this.ttlMillis = ttl != null ? ttl.toMillis() : -1;
        this.hotReloadEnabled = hotReloadEnabled;
        this.cache = new ConcurrentHashMap<>(maxSize);
    }

    /**
     * Creates a cache with default settings.
     *
     * @return A new cache with default settings
     */
    public static DefaultTemplateCache withDefaults() {
        return new DefaultTemplateCache(100, Duration.ofMinutes(5), false);
    }

    @Override
    @Nullable
    public CompiledTemplate get(@NotNull String cacheKey) {
        CompiledTemplate template = cache.get(cacheKey);
        if (template != null) {
            hits.incrementAndGet();
        } else {
            misses.incrementAndGet();
        }
        return template;
    }

    @Override
    public void put(@NotNull String cacheKey, @NotNull CompiledTemplate template) {
        // Evict oldest entries if at capacity
        if (cache.size() >= maxSize) {
            evictOldest();
        }
        cache.put(cacheKey, template);
    }

    @Override
    public void invalidate(@NotNull String cacheKey) {
        cache.remove(cacheKey);
    }

    @Override
    public boolean isValid(@NotNull String cacheKey, @Nullable Instant sourceModified) {
        CompiledTemplate template = cache.get(cacheKey);

        if (template == null) {
            return false;
        }

        // Check TTL expiration
        if (template.hasExpired(ttlMillis)) {
            cache.remove(cacheKey);
            return false;
        }

        // Check source modification time (hot-reload)
        if (hotReloadEnabled && sourceModified != null) {
            if (!template.isNewerThan(sourceModified)) {
                cache.remove(cacheKey);
                return false;
            }
        }

        return true;
    }

    @Override
    public void clear() {
        cache.clear();
        hits.set(0);
        misses.set(0);
    }

    /**
     * Evicts the oldest compiled template from the cache.
     * Uses compile time for ordering since we don't track access time.
     */
    private void evictOldest() {
        String oldestKey = null;
        Instant oldestTime = null;

        for (Map.Entry<String, CompiledTemplate> entry : cache.entrySet()) {
            Instant compileTime = entry.getValue().compiledAt();
            if (oldestTime == null || compileTime.isBefore(oldestTime)) {
                oldestTime = compileTime;
                oldestKey = entry.getKey();
            }
        }

        if (oldestKey != null) {
            cache.remove(oldestKey);
        }
    }

    @Override
    @NotNull
    public CacheStats getStats() {
        return new CacheStats(hits.get(), misses.get(), cache.size(), maxSize);
    }

    /**
     * Gets the maximum cache size.
     *
     * @return Maximum size
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Gets the TTL in milliseconds.
     *
     * @return TTL in milliseconds, or -1 for infinite
     */
    public long getTtlMillis() {
        return ttlMillis;
    }

    /**
     * Checks if hot-reload is enabled.
     *
     * @return true if hot-reload is enabled
     */
    public boolean isHotReloadEnabled() {
        return hotReloadEnabled;
    }
}
