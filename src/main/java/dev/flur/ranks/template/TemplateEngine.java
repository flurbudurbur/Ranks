package dev.flur.ranks.template;

import dev.flur.ranks.result.Result;
import dev.flur.ranks.template.error.TemplateValidationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Main interface for the templating engine.
 * Provides two-phase processing: Pebble first, then MiniMessage.
 */
public interface TemplateEngine {

    /**
     * Renders a template from a file path with the given context.
     *
     * @param templatePath Path relative to templates/ or locale/ folder
     * @param context      Variables to inject into the template
     * @return Result containing the rendered Component or error
     */
    @NotNull
    Result<Component> render(@NotNull String templatePath, @NotNull Map<String, Object> context);

    /**
     * Renders an inline template string (no file loading).
     *
     * @param template The template string
     * @param context  Variables to inject into the template
     * @return Result containing the rendered Component or error
     */
    @NotNull
    Result<Component> renderInline(@NotNull String template, @NotNull Map<String, Object> context);

    /**
     * Renders a template and returns the raw string (after Pebble, before MiniMessage).
     * Useful for debugging or when Component output is not needed.
     *
     * @param templatePath Path to the template
     * @param context      Variables to inject into the template
     * @return Result containing the processed string or error
     */
    @NotNull
    Result<String> renderToString(@NotNull String templatePath, @NotNull Map<String, Object> context);

    /**
     * Renders an inline template and returns the raw string (after Pebble, before MiniMessage).
     *
     * @param template The template string
     * @param context  Variables to inject into the template
     * @return Result containing the processed string or error
     */
    @NotNull
    Result<String> renderInlineToString(@NotNull String template, @NotNull Map<String, Object> context);

    /**
     * Validates a template without rendering.
     *
     * @param templatePath Path to the template
     * @return Validation result with detailed error information
     */
    @NotNull
    TemplateValidationResult validate(@NotNull String templatePath);

    /**
     * Validates an inline template string without rendering.
     *
     * @param template The template string
     * @return Validation result with detailed error information
     */
    @NotNull
    TemplateValidationResult validateInline(@NotNull String template);

    /**
     * Clears the template cache.
     */
    void clearCache();

    /**
     * Reloads templates from disk (for hot-reload during development).
     */
    void reload();

    /**
     * Checks if a template exists.
     *
     * @param templatePath Path to the template
     * @return true if the template exists
     */
    boolean templateExists(@NotNull String templatePath);
}
