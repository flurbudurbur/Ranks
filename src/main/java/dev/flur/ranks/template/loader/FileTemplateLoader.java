package dev.flur.ranks.template.loader;

import dev.flur.ranks.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

/**
 * Template loader that reads from the file system.
 */
public class FileTemplateLoader implements TemplateLoader {

    private final Path basePath;
    private final String suffix;

    /**
     * Creates a new file template loader.
     *
     * @param basePath Base directory for templates
     * @param suffix   File suffix to append (e.g., ".peb", ".yml")
     */
    public FileTemplateLoader(@NotNull Path basePath, @NotNull String suffix) {
        this.basePath = basePath;
        this.suffix = suffix.startsWith(".") ? suffix : "." + suffix;
    }

    /**
     * Creates a new file template loader with default ".peb" suffix.
     *
     * @param basePath Base directory for templates
     */
    public FileTemplateLoader(@NotNull Path basePath) {
        this(basePath, ".peb");
    }

    @Override
    @NotNull
    public Result<String> getContent(@NotNull String templateName) {
        Path templatePath = resolvePath(templateName);

        if (!Files.exists(templatePath)) {
            return Result.failure("Template not found: " + templateName);
        }

        try {
            String content = Files.readString(templatePath, StandardCharsets.UTF_8);
            return Result.success(content);
        } catch (IOException e) {
            return Result.failure("Failed to read template '" + templateName + "': " + e.getMessage());
        }
    }

    @Override
    public boolean exists(@NotNull String templateName) {
        return Files.exists(resolvePath(templateName));
    }

    @Override
    @Nullable
    public Instant getLastModified(@NotNull String templateName) {
        Path templatePath = resolvePath(templateName);

        if (!Files.exists(templatePath)) {
            return null;
        }

        try {
            return Files.getLastModifiedTime(templatePath).toInstant();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    @NotNull
    public String createCacheKey(@NotNull String templateName) {
        return "file:" + basePath.toString() + ":" + templateName;
    }

    /**
     * Resolves a template name to a file path.
     *
     * @param templateName Template name
     * @return Resolved file path
     */
    private Path resolvePath(String templateName) {
        // Normalize the template name (replace dots with path separators for nested paths)
        String normalizedName = templateName;

        // If the name already has a file extension, don't add suffix
        if (hasFileExtension(templateName)) {
            return basePath.resolve(normalizedName);
        }

        return basePath.resolve(normalizedName + suffix);
    }

    /**
     * Checks if a template name already has a file extension.
     *
     * @param templateName Template name
     * @return true if it has an extension
     */
    private boolean hasFileExtension(String templateName) {
        int lastDot = templateName.lastIndexOf('.');
        int lastSlash = Math.max(templateName.lastIndexOf('/'), templateName.lastIndexOf('\\'));

        // Has a dot after the last path separator
        return lastDot > lastSlash && lastDot < templateName.length() - 1;
    }

    /**
     * Gets the base path for this loader.
     *
     * @return Base path
     */
    @NotNull
    public Path getBasePath() {
        return basePath;
    }

    /**
     * Gets the file suffix.
     *
     * @return File suffix
     */
    @NotNull
    public String getSuffix() {
        return suffix;
    }
}
