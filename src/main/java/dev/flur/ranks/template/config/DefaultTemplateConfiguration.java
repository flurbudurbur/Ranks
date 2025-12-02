package dev.flur.ranks.template.config;

import dev.flur.ranks.template.TemplateConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of TemplateConfiguration with builder pattern.
 */
public final class DefaultTemplateConfiguration implements TemplateConfiguration {

    private final List<Path> templatePaths;
    private final boolean cachingEnabled;
    private final int cacheMaxSize;
    private final Duration cacheTtl;
    private final boolean hotReloadEnabled;
    private final boolean strictVariables;
    private final String defaultLocale;
    private final boolean papiEnabled;

    private DefaultTemplateConfiguration(Builder builder) {
        this.templatePaths = Collections.unmodifiableList(new ArrayList<>(builder.templatePaths));
        this.cachingEnabled = builder.cachingEnabled;
        this.cacheMaxSize = builder.cacheMaxSize;
        this.cacheTtl = builder.cacheTtl;
        this.hotReloadEnabled = builder.hotReloadEnabled;
        this.strictVariables = builder.strictVariables;
        this.defaultLocale = builder.defaultLocale;
        this.papiEnabled = builder.papiEnabled;
    }

    /**
     * Creates a new builder with default values.
     *
     * @return A new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a configuration from a Bukkit ConfigurationSection.
     *
     * @param section   The configuration section (templates section)
     * @param dataFolder The plugin data folder for resolving paths
     * @return A new configuration instance
     */
    public static DefaultTemplateConfiguration fromConfig(@NotNull ConfigurationSection section,
                                                          @NotNull Path dataFolder) {
        Builder builder = builder();

        // Add default paths
        builder.addTemplatePath(dataFolder.resolve("templates"));
        builder.addTemplatePath(dataFolder.resolve("locale"));

        // Cache settings
        ConfigurationSection cacheSection = section.getConfigurationSection("cache");
        if (cacheSection != null) {
            builder.cachingEnabled(cacheSection.getBoolean("enabled", true));
            builder.cacheMaxSize(cacheSection.getInt("max-size", 100));

            int ttlSeconds = cacheSection.getInt("ttl-seconds", -1);
            if (ttlSeconds > 0) {
                builder.cacheTtl(Duration.ofSeconds(ttlSeconds));
            }
        }

        // Other settings
        builder.hotReloadEnabled(section.getBoolean("hot-reload", false));
        builder.strictVariables(section.getBoolean("strict-variables", false));
        builder.papiEnabled(section.getBoolean("papi-enabled", true));

        return builder.build();
    }

    /**
     * Creates a default configuration for the given data folder.
     *
     * @param dataFolder The plugin data folder
     * @return A new configuration with default values
     */
    public static DefaultTemplateConfiguration defaults(@NotNull Path dataFolder) {
        return builder()
                .addTemplatePath(dataFolder.resolve("templates"))
                .addTemplatePath(dataFolder.resolve("locale"))
                .build();
    }

    @Override
    @NotNull
    public List<Path> getTemplatePaths() {
        return templatePaths;
    }

    @Override
    public boolean isCachingEnabled() {
        return cachingEnabled;
    }

    @Override
    public int getCacheMaxSize() {
        return cacheMaxSize;
    }

    @Override
    @Nullable
    public Duration getCacheTtl() {
        return cacheTtl;
    }

    @Override
    public boolean isHotReloadEnabled() {
        return hotReloadEnabled;
    }

    @Override
    public boolean isStrictVariables() {
        return strictVariables;
    }

    @Override
    @NotNull
    public String getDefaultLocale() {
        return defaultLocale;
    }

    @Override
    public boolean isPapiEnabled() {
        return papiEnabled;
    }

    /**
     * Builder for creating TemplateConfiguration instances.
     */
    public static final class Builder {

        private final List<Path> templatePaths = new ArrayList<>();
        private boolean cachingEnabled = true;
        private int cacheMaxSize = 100;
        private Duration cacheTtl = Duration.ofMinutes(5);
        private boolean hotReloadEnabled = false;
        private boolean strictVariables = false;
        private String defaultLocale = "en";
        private boolean papiEnabled = true;

        private Builder() {
        }

        /**
         * Adds a template path to search.
         *
         * @param path The path to add
         * @return This builder
         */
        public Builder addTemplatePath(@NotNull Path path) {
            templatePaths.add(path);
            return this;
        }

        /**
         * Sets whether caching is enabled.
         *
         * @param enabled true to enable caching
         * @return This builder
         */
        public Builder cachingEnabled(boolean enabled) {
            this.cachingEnabled = enabled;
            return this;
        }

        /**
         * Sets the maximum cache size.
         *
         * @param maxSize Maximum number of cached templates
         * @return This builder
         */
        public Builder cacheMaxSize(int maxSize) {
            this.cacheMaxSize = maxSize;
            return this;
        }

        /**
         * Sets the cache time-to-live.
         *
         * @param ttl Cache TTL duration, or null for infinite
         * @return This builder
         */
        public Builder cacheTtl(@Nullable Duration ttl) {
            this.cacheTtl = ttl;
            return this;
        }

        /**
         * Sets whether hot-reload is enabled.
         *
         * @param enabled true to enable hot-reload
         * @return This builder
         */
        public Builder hotReloadEnabled(boolean enabled) {
            this.hotReloadEnabled = enabled;
            return this;
        }

        /**
         * Sets whether strict variable mode is enabled.
         *
         * @param strict true to fail on undefined variables
         * @return This builder
         */
        public Builder strictVariables(boolean strict) {
            this.strictVariables = strict;
            return this;
        }

        /**
         * Sets the default locale.
         *
         * @param locale Default locale code
         * @return This builder
         */
        public Builder defaultLocale(@NotNull String locale) {
            this.defaultLocale = locale;
            return this;
        }

        /**
         * Sets whether PlaceholderAPI is enabled.
         *
         * @param enabled true to enable PAPI
         * @return This builder
         */
        public Builder papiEnabled(boolean enabled) {
            this.papiEnabled = enabled;
            return this;
        }

        /**
         * Builds the configuration.
         *
         * @return A new TemplateConfiguration instance
         */
        public DefaultTemplateConfiguration build() {
            return new DefaultTemplateConfiguration(this);
        }
    }
}
