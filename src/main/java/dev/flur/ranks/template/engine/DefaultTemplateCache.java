package dev.flur.ranks.template.engine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * LRU cache implementation for compiled templates.
 */
public class DefaultTemplateCache implements TemplateCache {

    private final int maxSize;
    private final long ttlMillis;
    private final boolean hotReloadEnabled;
    private final Map<String, CompiledTemplate> cache;
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

        // LinkedHashMap with access-order for LRU eviction
        this.cache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CompiledTemplate> eldest) {
                return size() > maxSize;
            }
        };
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
    public synchronized CompiledTemplate get(@NotNull String cacheKey) {
        CompiledTemplate template = cache.get(cacheKey);
        if (template != null) {
            hits.incrementAndGet();
        } else {
            misses.incrementAndGet();
        }
        return template;
    }

    @Override
    public synchronized void put(@NotNull String cacheKey, @NotNull CompiledTemplate template) {
        cache.put(cacheKey, template);
    }

    @Override
    public synchronized void invalidate(@NotNull String cacheKey) {
        cache.remove(cacheKey);
    }

    @Override
    public synchronized boolean isValid(@NotNull String cacheKey, @Nullable Instant sourceModified) {
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
    public synchronized void clear() {
        cache.clear();
        hits.set(0);
        misses.set(0);
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
