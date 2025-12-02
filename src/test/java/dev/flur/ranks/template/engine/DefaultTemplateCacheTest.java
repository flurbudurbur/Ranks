package dev.flur.ranks.template.engine;

import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DefaultTemplateCache.
 */
class DefaultTemplateCacheTest {

    private DefaultTemplateCache cache;
    private PebbleTemplate mockTemplate;

    @BeforeEach
    void setUp() {
        mockTemplate = mock(PebbleTemplate.class);
    }

    private CompiledTemplate createTemplate(String path) {
        return new CompiledTemplate(path, mockTemplate, Instant.now(), Instant.now());
    }

    private CompiledTemplate createTemplate(String path, Instant compiledAt) {
        return new CompiledTemplate(path, mockTemplate, compiledAt, Instant.now());
    }

    private CompiledTemplate createTemplate(String path, Instant compiledAt, Instant sourceModified) {
        return new CompiledTemplate(path, mockTemplate, compiledAt, sourceModified);
    }

    @Nested
    @DisplayName("Basic Cache Operations")
    class BasicOperations {

        @BeforeEach
        void setUp() {
            cache = new DefaultTemplateCache(100, null, false);
        }

        @Test
        @DisplayName("put and get returns template")
        void putAndGet_ReturnsTemplate() {
            String key = "test.peb";
            CompiledTemplate template = createTemplate(key);

            cache.put(key, template);
            CompiledTemplate result = cache.get(key);

            assertNotNull(result);
            assertEquals(key, result.sourcePath());
        }

        @Test
        @DisplayName("get non-existent returns null")
        void get_NonExistent_ReturnsNull() {
            assertNull(cache.get("missing.peb"));
        }

        @Test
        @DisplayName("invalidate removes entry")
        void invalidate_RemovesEntry() {
            String key = "test.peb";
            cache.put(key, createTemplate(key));

            cache.invalidate(key);

            assertNull(cache.get(key));
        }

        @Test
        @DisplayName("clear removes all entries")
        void clear_RemovesAll() {
            cache.put("a.peb", createTemplate("a.peb"));
            cache.put("b.peb", createTemplate("b.peb"));
            cache.put("c.peb", createTemplate("c.peb"));

            cache.clear();

            assertNull(cache.get("a.peb"));
            assertNull(cache.get("b.peb"));
            assertNull(cache.get("c.peb"));
        }
    }

    @Nested
    @DisplayName("LRU Eviction")
    class LRUEviction {

        @Test
        @DisplayName("evicts least recently used when exceeding max size")
        void put_ExceedsMaxSize_EvictsLRU() {
            cache = new DefaultTemplateCache(3, null, false);

            cache.put("a.peb", createTemplate("a.peb"));
            cache.put("b.peb", createTemplate("b.peb"));
            cache.put("c.peb", createTemplate("c.peb"));

            // Access "a" to make it recently used
            cache.get("a.peb");

            // Add new entry, should evict "b" (least recently used)
            cache.put("d.peb", createTemplate("d.peb"));

            assertNotNull(cache.get("a.peb"), "a.peb should still exist");
            assertNull(cache.get("b.peb"), "b.peb should be evicted");
            assertNotNull(cache.get("c.peb"), "c.peb should still exist");
            assertNotNull(cache.get("d.peb"), "d.peb should exist");
        }

        @Test
        @DisplayName("get updates access order")
        void get_UpdatesAccessOrder() {
            cache = new DefaultTemplateCache(2, null, false);

            cache.put("old.peb", createTemplate("old.peb"));
            cache.put("new.peb", createTemplate("new.peb"));

            // Access "old" to make it recently used
            cache.get("old.peb");

            // Add another entry, should evict "new" (now least recently used)
            cache.put("newest.peb", createTemplate("newest.peb"));

            assertNotNull(cache.get("old.peb"), "old.peb should still exist");
            assertNull(cache.get("new.peb"), "new.peb should be evicted");
        }
    }

    @Nested
    @DisplayName("TTL Expiration")
    class TTLExpiration {

        @Test
        @DisplayName("isValid returns true before TTL expires")
        void isValid_BeforeTTL_ReturnsTrue() {
            cache = new DefaultTemplateCache(100, Duration.ofHours(1), false);
            String key = "test.peb";

            cache.put(key, createTemplate(key));

            assertTrue(cache.isValid(key, null));
        }

        @Test
        @DisplayName("isValid returns false after TTL expires")
        void isValid_AfterTTL_ReturnsFalse() {
            cache = new DefaultTemplateCache(100, Duration.ofMillis(1), false);
            String key = "test.peb";

            // Create template compiled in the past
            Instant pastCompileTime = Instant.now().minus(Duration.ofSeconds(1));
            cache.put(key, createTemplate(key, pastCompileTime));

            assertFalse(cache.isValid(key, null));
        }

        @Test
        @DisplayName("isValid with no TTL never expires")
        void isValid_NoTTL_NeverExpires() {
            cache = new DefaultTemplateCache(100, null, false);
            String key = "test.peb";

            // Create template compiled in the past
            Instant pastCompileTime = Instant.now().minus(Duration.ofDays(365));
            cache.put(key, createTemplate(key, pastCompileTime));

            assertTrue(cache.isValid(key, null));
        }
    }

    @Nested
    @DisplayName("Hot Reload")
    class HotReload {

        @Test
        @DisplayName("isValid returns false when source is newer")
        void isValid_SourceModified_ReturnsFalse() {
            cache = new DefaultTemplateCache(100, null, true);
            String key = "test.peb";

            Instant compiledAt = Instant.now().minus(Duration.ofMinutes(5));
            Instant sourceModifiedAt = Instant.now().minus(Duration.ofMinutes(5));
            cache.put(key, createTemplate(key, compiledAt, sourceModifiedAt));

            // Source was modified after compilation
            Instant newerSourceTime = Instant.now();

            assertFalse(cache.isValid(key, newerSourceTime));
        }

        @Test
        @DisplayName("isValid returns true when source is older")
        void isValid_SourceUnchanged_ReturnsTrue() {
            cache = new DefaultTemplateCache(100, null, true);
            String key = "test.peb";

            Instant now = Instant.now();
            cache.put(key, createTemplate(key, now, now));

            // Source was modified before compilation
            Instant olderSourceTime = now.minus(Duration.ofMinutes(1));

            assertTrue(cache.isValid(key, olderSourceTime));
        }

        @Test
        @DisplayName("isValid ignores source time when hot-reload disabled")
        void isValid_HotReloadDisabled_IgnoresSourceTime() {
            cache = new DefaultTemplateCache(100, null, false);
            String key = "test.peb";

            Instant pastTime = Instant.now().minus(Duration.ofMinutes(10));
            cache.put(key, createTemplate(key, pastTime, pastTime));

            // Even with newer source time, should return true
            assertTrue(cache.isValid(key, Instant.now()));
        }
    }

    @Nested
    @DisplayName("Cache Statistics")
    class Statistics {

        @BeforeEach
        void setUp() {
            cache = new DefaultTemplateCache(100, null, false);
        }

        @Test
        @DisplayName("tracks cache hits")
        void getStats_TracksHits() {
            String key = "test.peb";
            cache.put(key, createTemplate(key));

            cache.get(key);
            cache.get(key);
            cache.get(key);

            TemplateCache.CacheStats stats = cache.getStats();
            assertEquals(3, stats.hits());
        }

        @Test
        @DisplayName("tracks cache misses")
        void getStats_TracksMisses() {
            cache.get("missing1.peb");
            cache.get("missing2.peb");

            TemplateCache.CacheStats stats = cache.getStats();
            assertEquals(2, stats.misses());
        }

        @Test
        @DisplayName("tracks hits and misses correctly")
        void getStats_TracksHitsAndMisses() {
            String key = "test.peb";
            cache.put(key, createTemplate(key));

            cache.get(key);      // hit
            cache.get(key);      // hit
            cache.get("x.peb");  // miss
            cache.get(key);      // hit

            TemplateCache.CacheStats stats = cache.getStats();
            assertEquals(3, stats.hits());
            assertEquals(1, stats.misses());

            // Calculate hit rate manually
            double hitRate = (double) stats.hits() / (stats.hits() + stats.misses());
            assertEquals(0.75, hitRate, 0.001);
        }

        @Test
        @DisplayName("tracks current size")
        void getStats_TracksSize() {
            cache.put("a.peb", createTemplate("a.peb"));
            cache.put("b.peb", createTemplate("b.peb"));

            TemplateCache.CacheStats stats = cache.getStats();
            assertEquals(2, stats.size());
        }

        @Test
        @DisplayName("clear resets statistics")
        void clear_ResetsStats() {
            cache.put("test.peb", createTemplate("test.peb"));
            cache.get("test.peb");
            cache.get("missing.peb");

            cache.clear();

            TemplateCache.CacheStats stats = cache.getStats();
            assertEquals(0, stats.hits());
            assertEquals(0, stats.misses());
            assertEquals(0, stats.size());
        }
    }

    @Nested
    @DisplayName("Factory Methods")
    class FactoryMethods {

        @Test
        @DisplayName("withDefaults creates cache with sensible defaults")
        void withDefaults_CreatesCacheWithDefaults() {
            DefaultTemplateCache cache = DefaultTemplateCache.withDefaults();

            assertEquals(100, cache.getMaxSize());
            assertEquals(Duration.ofMinutes(5).toMillis(), cache.getTtlMillis());
            assertFalse(cache.isHotReloadEnabled());
        }
    }
}
