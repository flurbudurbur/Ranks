package dev.flur.ranks.template.loader;

import dev.flur.ranks.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.regex.Pattern;

/**
 * Decorator loader that transforms Ranks-specific syntax to standard Pebble syntax.
 * <p>
 * Transformations:
 * <ul>
 *   <li>{@code << variable >>} → {@code {{ variable }}}</li>
 *   <li>{@code <[ tag ]>} → {@code {% tag %}}</li>
 *   <li>{@code <# comment #>} → {@code {# comment #}}</li>
 * </ul>
 */
public class SyntaxTransformer implements TemplateLoader {

    private final TemplateLoader delegate;

    // Patterns for syntax transformation
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("<<\\s*(.+?)\\s*>>");
    private static final Pattern TAG_PATTERN = Pattern.compile("<\\[\\s*(.+?)\\s*]>");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("<#\\s*(.+?)\\s*#>");

    /**
     * Creates a new syntax transformer wrapping the given loader.
     *
     * @param delegate The loader to delegate to
     */
    public SyntaxTransformer(@NotNull TemplateLoader delegate) {
        this.delegate = delegate;
    }

    @Override
    @NotNull
    public Result<String> getContent(@NotNull String templateName) {
        return delegate.getContent(templateName).map(this::transform);
    }

    @Override
    public boolean exists(@NotNull String templateName) {
        return delegate.exists(templateName);
    }

    @Override
    @Nullable
    public Instant getLastModified(@NotNull String templateName) {
        return delegate.getLastModified(templateName);
    }

    @Override
    @NotNull
    public String createCacheKey(@NotNull String templateName) {
        return delegate.createCacheKey(templateName);
    }

    /**
     * Transforms Ranks syntax to Pebble syntax.
     *
     * @param content The template content
     * @return Transformed content
     */
    @NotNull
    public String transform(@NotNull String content) {
        String result = content;

        // Transform variables: << var >> → {{ var }}
        result = VARIABLE_PATTERN.matcher(result).replaceAll("{{ $1 }}");

        // Transform tags: <[ tag ]> → {% tag %}
        result = TAG_PATTERN.matcher(result).replaceAll("{% $1 %}");

        // Transform comments: <# comment #> → {# comment #}
        result = COMMENT_PATTERN.matcher(result).replaceAll("{# $1 #}");

        return result;
    }

    /**
     * Gets the delegate loader.
     *
     * @return The delegate loader
     */
    @NotNull
    public TemplateLoader getDelegate() {
        return delegate;
    }
}
