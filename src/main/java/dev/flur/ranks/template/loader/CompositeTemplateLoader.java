package dev.flur.ranks.template.loader;

import dev.flur.ranks.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Template loader that searches multiple loaders in order.
 * First loader that has the template wins.
 */
public class CompositeTemplateLoader implements TemplateLoader {

    private final List<TemplateLoader> loaders;

    /**
     * Creates a composite loader with the given loaders.
     *
     * @param loaders Loaders to search in order
     */
    public CompositeTemplateLoader(@NotNull List<TemplateLoader> loaders) {
        this.loaders = Collections.unmodifiableList(new ArrayList<>(loaders));
    }

    /**
     * Creates a composite loader from varargs.
     *
     * @param loaders Loaders to search in order
     * @return A new CompositeTemplateLoader
     */
    public static CompositeTemplateLoader of(TemplateLoader... loaders) {
        return new CompositeTemplateLoader(List.of(loaders));
    }

    @Override
    @NotNull
    public Result<String> getContent(@NotNull String templateName) {
        for (TemplateLoader loader : loaders) {
            if (loader.exists(templateName)) {
                return loader.getContent(templateName);
            }
        }
        return Result.failure("Template not found: " + templateName);
    }

    @Override
    public boolean exists(@NotNull String templateName) {
        for (TemplateLoader loader : loaders) {
            if (loader.exists(templateName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Nullable
    public Instant getLastModified(@NotNull String templateName) {
        for (TemplateLoader loader : loaders) {
            if (loader.exists(templateName)) {
                return loader.getLastModified(templateName);
            }
        }
        return null;
    }

    @Override
    @NotNull
    public String createCacheKey(@NotNull String templateName) {
        for (TemplateLoader loader : loaders) {
            if (loader.exists(templateName)) {
                return loader.createCacheKey(templateName);
            }
        }
        // Fallback key for non-existent templates
        return "composite:" + templateName;
    }

    /**
     * Finds the loader that contains the template.
     *
     * @param templateName Template name
     * @return The loader containing the template, or null
     */
    @Nullable
    public TemplateLoader findLoader(@NotNull String templateName) {
        for (TemplateLoader loader : loaders) {
            if (loader.exists(templateName)) {
                return loader;
            }
        }
        return null;
    }

    /**
     * Gets the list of loaders.
     *
     * @return Unmodifiable list of loaders
     */
    @NotNull
    public List<TemplateLoader> getLoaders() {
        return loaders;
    }
}
