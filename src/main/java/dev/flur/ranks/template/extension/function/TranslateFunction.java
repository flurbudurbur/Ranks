package dev.flur.ranks.template.extension.function;

import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Pebble function for looking up translations from the message service.
 * <p>
 * Usage in templates:
 * <ul>
 *   <li>{@code {{ translate('rankup.success') }}} - Get translation with default locale</li>
 *   <li>{@code {{ translate('rankup.success', 'es') }}} - Get translation with specific locale</li>
 *   <li>{@code {{ t('rankup.success') }}} - Short alias</li>
 * </ul>
 */
public class TranslateFunction implements Function {

    private final TranslationProvider provider;

    /**
     * Creates a new translate function.
     *
     * @param provider The translation provider
     */
    public TranslateFunction(@NotNull TranslationProvider provider) {
        this.provider = provider;
    }

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self,
                          EvaluationContext context, int lineNumber) {
        // Get translation key
        Object keyArg = args.get("key");
        if (keyArg == null) {
            return "";
        }

        String key = keyArg.toString();

        // Get locale (optional)
        String locale = null;
        Object localeArg = args.get("locale");
        if (localeArg != null) {
            locale = localeArg.toString();
        } else {
            // Try to get locale from context
            Object contextLocale = context.getVariable("locale");
            if (contextLocale != null) {
                locale = contextLocale.toString();
            }
        }

        // Lookup translation
        String translation = provider.getTranslation(key, locale);
        return translation != null ? translation : key;
    }

    @Override
    public List<String> getArgumentNames() {
        return List.of("key", "locale");
    }

    /**
     * Interface for providing translations.
     * This allows decoupling from the MessageService.
     */
    @FunctionalInterface
    public interface TranslationProvider {

        /**
         * Gets a translation for the given key and locale.
         *
         * @param key    The translation key
         * @param locale The locale (can be null for default)
         * @return The translated string, or null if not found
         */
        @Nullable
        String getTranslation(@NotNull String key, @Nullable String locale);
    }
}
