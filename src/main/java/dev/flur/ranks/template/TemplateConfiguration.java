package dev.flur.ranks.template;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

/**
 * Configuration for the template engine.
 */
public interface TemplateConfiguration {

    /**
     * Gets the base paths for template loading.
     * Paths are searched in order (first match wins).
     * Typically: templates/ first, then locale/.
     *
     * @return List of template paths
     */
    @NotNull
    List<Path> getTemplatePaths();

    /**
     * Whether to enable template caching.
     *
     * @return true if caching is enabled
     */
    boolean isCachingEnabled();

    /**
     * Maximum number of templates to cache.
     *
     * @return Maximum cache size
     */
    int getCacheMaxSize();

    /**
     * Time-to-live for cached templates (for hot-reload).
     * Returns null for infinite TTL.
     *
     * @return Cache TTL duration or null
     */
    @Nullable
    Duration getCacheTtl();

    /**
     * Whether hot-reload is enabled (checks file modification time).
     *
     * @return true if hot-reload is enabled
     */
    boolean isHotReloadEnabled();

    /**
     * Whether to use strict variable mode (fail on undefined variables).
     *
     * @return true if strict variables mode is enabled
     */
    boolean isStrictVariables();

    /**
     * Default locale for fallback.
     *
     * @return Default locale code (e.g., "en")
     */
    @NotNull
    String getDefaultLocale();

    /**
     * Whether PlaceholderAPI integration is enabled.
     *
     * @return true if PAPI is enabled
     */
    boolean isPapiEnabled();
}
