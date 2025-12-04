package dev.flur.ranks.template.engine;

import dev.flur.ranks.result.Result;
import dev.flur.ranks.template.TemplateConfiguration;
import dev.flur.ranks.template.TemplateEngine;
import dev.flur.ranks.template.error.TemplateErrorType;
import dev.flur.ranks.template.error.TemplateException;
import dev.flur.ranks.template.error.TemplateValidationResult;
import dev.flur.ranks.template.loader.TemplateLoader;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.Extension;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Default implementation of the template engine.
 * Provides two-phase processing: Pebble templates → MiniMessage formatting.
 */
public class DefaultTemplateEngine implements TemplateEngine {

    private final TemplateConfiguration config;
    private final TemplateLoader loader;
    private final TemplateCache cache;
    private final PebbleEngine pebbleEngine;
    private final PebbleEngine inlineEngine;
    private final MiniMessage miniMessage;
    private final Logger logger;

    /**
     * Creates a new template engine.
     *
     * @param config     Template configuration
     * @param loader     Template loader
     * @param extensions Pebble extensions to register
     * @param logger     Logger for error reporting
     */
    public DefaultTemplateEngine(@NotNull TemplateConfiguration config,
                                 @NotNull TemplateLoader loader,
                                 @NotNull List<Extension> extensions,
                                 @NotNull Logger logger) {
        this.config = config;
        this.loader = loader;
        this.logger = logger;

        // Create cache
        this.cache = config.isCachingEnabled()
                ? new DefaultTemplateCache(config.getCacheMaxSize(), config.getCacheTtl(), config.isHotReloadEnabled())
                : new NoOpCache();

        // Create Pebble engine with custom loader wrapper
        PebbleEngine.Builder builder = new PebbleEngine.Builder()
                .strictVariables(config.isStrictVariables())
                .autoEscaping(false); // Disable auto-escaping for MiniMessage

        // Register extensions
        for (Extension extension : extensions) {
            builder.extension(extension);
        }

        this.pebbleEngine = builder
                .loader(new PebbleLoaderAdapter(loader))
                .build();

        // Create inline engine with string loader
        this.inlineEngine = new PebbleEngine.Builder()
                .strictVariables(config.isStrictVariables())
                .autoEscaping(false)
                .loader(new StringLoader())
                .extension(extensions.toArray(new Extension[0]))
                .build();

        this.miniMessage = MiniMessage.miniMessage();
    }

    @Override
    @NotNull
    public Result<Component> render(@NotNull String templatePath, @NotNull Map<String, Object> context) {
        return renderToString(templatePath, context)
                .flatMap(this::parseWithMiniMessage);
    }

    @Override
    @NotNull
    public Result<Component> renderInline(@NotNull String template, @NotNull Map<String, Object> context) {
        return renderInlineToString(template, context)
                .flatMap(this::parseWithMiniMessage);
    }

    @Override
    @NotNull
    public Result<String> renderToString(@NotNull String templatePath, @NotNull Map<String, Object> context) {
        try {
            CompiledTemplate compiled = getOrCompileTemplate(templatePath);
            return evaluateTemplate(compiled.pebbleTemplate(), context);
        } catch (TemplateException e) {
            logger.warning("Template error in '" + templatePath + "': " + e.getMessage());
            return Result.failure(e.getMessage());
        }
    }

    @Override
    @NotNull
    public Result<String> renderInlineToString(@NotNull String template, @NotNull Map<String, Object> context) {
        try {
            PebbleTemplate pebbleTemplate = inlineEngine.getLiteralTemplate(template);
            return evaluateTemplate(pebbleTemplate, context);
        } catch (PebbleException e) {
            String errorMsg = formatPebbleError(e, null);
            logger.warning("Inline template error: " + errorMsg);
            return Result.failure(errorMsg);
        }
    }

    @Override
    @NotNull
    public TemplateValidationResult validate(@NotNull String templatePath) {
        if (!loader.exists(templatePath)) {
            return TemplateValidationResult.failure(templatePath,
                    new TemplateValidationResult.TemplateIssue(
                            TemplateErrorType.FILE_NOT_FOUND,
                            "Template file not found: " + templatePath));
        }

        Result<String> contentResult = loader.getContent(templatePath);
        if (contentResult.isFailure()) {
            return TemplateValidationResult.failure(templatePath,
                    new TemplateValidationResult.TemplateIssue(
                            TemplateErrorType.IO_ERROR,
                            contentResult.getErrorMessage()));
        }

        return validateContent(contentResult.getValue(), templatePath);
    }

    @Override
    @NotNull
    public TemplateValidationResult validateInline(@NotNull String template) {
        return validateContent(template, null);
    }

    @Override
    public void clearCache() {
        cache.clear();
    }

    @Override
    public void reload() {
        cache.clear();
    }

    @Override
    public boolean templateExists(@NotNull String templatePath) {
        return loader.exists(templatePath);
    }

    // ==================== Private Methods ====================

    /**
     * Gets a compiled template from cache or compiles it.
     */
    private CompiledTemplate getOrCompileTemplate(String templatePath) {
        String cacheKey = loader.createCacheKey(templatePath);
        Instant sourceModified = loader.getLastModified(templatePath);

        // Check cache validity
        if (cache.isValid(cacheKey, sourceModified)) {
            CompiledTemplate cached = cache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }

        // Compile and cache
        CompiledTemplate compiled = compileTemplate(templatePath, sourceModified);
        cache.put(cacheKey, compiled);
        return compiled;
    }

    /**
     * Compiles a template from the loader.
     */
    private CompiledTemplate compileTemplate(String templatePath, @Nullable Instant sourceModified) {
        Result<String> contentResult = loader.getContent(templatePath);

        if (contentResult.isFailure()) {
            throw new TemplateException(
                    TemplateErrorType.FILE_NOT_FOUND,
                    contentResult.getErrorMessage(),
                    templatePath, -1, -1, null);
        }

        try {
            PebbleTemplate pebbleTemplate = pebbleEngine.getTemplate(templatePath);
            return new CompiledTemplate(templatePath, pebbleTemplate, Instant.now(), sourceModified);
        } catch (PebbleException e) {
            throw new TemplateException(
                    TemplateErrorType.SYNTAX_ERROR,
                    e.getMessage(),
                    templatePath,
                    e.getLineNumber() != null ? e.getLineNumber() : -1,
                    -1,
                    e);
        }
    }

    /**
     * Evaluates a Pebble template with context.
     */
    private Result<String> evaluateTemplate(PebbleTemplate template, Map<String, Object> context) {
        try {
            Writer writer = new StringWriter();
            template.evaluate(writer, context);
            return Result.success(writer.toString());
        } catch (PebbleException e) {
            return Result.failure(formatPebbleError(e, null));
        } catch (IOException e) {
            return Result.failure("Template evaluation failed: " + e.getMessage());
        }
    }

    /**
     * Parses a string with MiniMessage.
     */
    private Result<Component> parseWithMiniMessage(String content) {
        try {
            Component component = miniMessage.deserialize(content);
            return Result.success(component);
        } catch (ParsingException e) {
            String errorMsg = "MiniMessage parsing error: " + e.getMessage();
            logger.warning(errorMsg);
            return Result.failure(errorMsg);
        }
    }

    /**
     * Validates template content.
     */
    private TemplateValidationResult validateContent(String content, @Nullable String templatePath) {
        List<TemplateValidationResult.TemplateIssue> issues = new ArrayList<>();

        // Validate Pebble syntax
        try {
            inlineEngine.getLiteralTemplate(content);
        } catch (PebbleException e) {
            issues.add(new TemplateValidationResult.TemplateIssue(
                    TemplateErrorType.SYNTAX_ERROR,
                    e.getMessage(),
                    e.getLineNumber() != null ? e.getLineNumber() : -1,
                    -1,
                    null));
        }

        // Validate MiniMessage syntax (basic check)
        try {
            // Replace Pebble variables with placeholder text for MiniMessage validation
            String forValidation = content
                    .replaceAll("\\{\\{.*?}}", "placeholder")
                    .replaceAll("\\{%.*?%}", "")
                    .replaceAll("\\{#.*?#}", "");
            miniMessage.deserialize(forValidation);
        } catch (ParsingException e) {
            issues.add(new TemplateValidationResult.TemplateIssue(
                    TemplateErrorType.MINIMESSAGE_ERROR,
                    e.getMessage()));
        }

        if (issues.isEmpty()) {
            return TemplateValidationResult.success(templatePath);
        } else {
            return TemplateValidationResult.failure(templatePath, issues);
        }
    }

    /**
     * Formats a Pebble exception for user display.
     */
    private String formatPebbleError(PebbleException e, @Nullable String templatePath) {
        StringBuilder sb = new StringBuilder();
        sb.append("Template error");

        if (templatePath != null) {
            sb.append(" in '").append(templatePath).append("'");
        }

        if (e.getLineNumber() != null) {
            sb.append(" at line ").append(e.getLineNumber());
        }

        sb.append(": ").append(e.getMessage());
        return sb.toString();
    }

    // ==================== Inner Classes ====================

    /**
     * Adapter to use our TemplateLoader with Pebble.
     */
    private static class PebbleLoaderAdapter implements io.pebbletemplates.pebble.loader.Loader<String> {

        private final TemplateLoader loader;

        PebbleLoaderAdapter(TemplateLoader loader) {
            this.loader = loader;
        }

        @Override
        public java.io.Reader getReader(String templateName) {
            Result<String> result = loader.getContent(templateName);
            if (result.isFailure()) {
                throw new RuntimeException("Template not found: " + templateName);
            }
            return new java.io.StringReader(result.getValue());
        }

        @Override
        public void setCharset(String charset) {
            // Not used
        }

        @Override
        public void setPrefix(String prefix) {
            // Not used
        }

        @Override
        public void setSuffix(String suffix) {
            // Not used
        }

        @Override
        public String resolveRelativePath(String relativePath, String anchorPath) {
            // Handle template inheritance paths
            if (relativePath.startsWith("/")) {
                return relativePath.substring(1);
            }

            if (anchorPath == null) {
                return relativePath;
            }

            int lastSlash = anchorPath.lastIndexOf('/');
            if (lastSlash < 0) {
                return relativePath;
            }

            return anchorPath.substring(0, lastSlash + 1) + relativePath;
        }

        @Override
        public String createCacheKey(String templateName) {
            return loader.createCacheKey(templateName);
        }

        @Override
        public boolean resourceExists(String templateName) {
            return loader.exists(templateName);
        }
    }

    /**
     * No-op cache for when caching is disabled.
     */
    private static class NoOpCache implements TemplateCache {

        @Override
        public CompiledTemplate get(@NotNull String cacheKey) {
            return null;
        }

        @Override
        public void put(@NotNull String cacheKey, @NotNull CompiledTemplate template) {
            // No-op
        }

        @Override
        public void invalidate(@NotNull String cacheKey) {
            // No-op
        }

        @Override
        public boolean isValid(@NotNull String cacheKey, Instant sourceModified) {
            return false;
        }

        @Override
        public void clear() {
            // No-op
        }

        @Override
        @NotNull
        public CacheStats getStats() {
            return new CacheStats(0, 0, 0, 0);
        }
    }
}
