package dev.flur.ranks.template.extension;

import dev.flur.ranks.service.RanksService;
import dev.flur.ranks.template.extension.filter.CompactFilter;
import dev.flur.ranks.template.extension.filter.CurrencyFilter;
import dev.flur.ranks.template.extension.filter.ItemNameFilter;
import dev.flur.ranks.template.extension.filter.LocationFormatFilter;
import dev.flur.ranks.template.extension.filter.PercentFilter;
import dev.flur.ranks.template.extension.filter.TimeFormatFilter;
import dev.flur.ranks.template.extension.filter.WholeFilter;
import dev.flur.ranks.template.extension.function.PapiFunction;
import dev.flur.ranks.template.extension.function.PlayerFunction;
import dev.flur.ranks.template.extension.function.RankFunction;
import dev.flur.ranks.template.extension.function.TranslateFunction;
import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.extension.Function;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Pebble extension that provides Minecraft-specific filters and functions.
 */
public class RanksExtension extends AbstractExtension {

    private final Server server;
    private final boolean papiEnabled;
    private final TranslateFunction.TranslationProvider translationProvider;
    private final RanksService ranksService;

    /**
     * Creates a new RanksExtension.
     *
     * @param server              The Bukkit server
     * @param papiEnabled         Whether PlaceholderAPI is enabled
     * @param translationProvider Provider for translation lookups (can be null)
     * @param ranksService        The ranks service (can be null if rank function not needed)
     */
    public RanksExtension(@NotNull Server server,
                          boolean papiEnabled,
                          @Nullable TranslateFunction.TranslationProvider translationProvider,
                          @Nullable RanksService ranksService) {
        this.server = server;
        this.papiEnabled = papiEnabled;
        this.translationProvider = translationProvider;
        this.ranksService = ranksService;
    }

    /**
     * Creates a RanksExtension with minimal configuration.
     *
     * @param server      The Bukkit server
     * @param papiEnabled Whether PlaceholderAPI is enabled
     */
    public RanksExtension(@NotNull Server server, boolean papiEnabled) {
        this(server, papiEnabled, null, null);
    }

    /**
     * Creates a RanksExtension with translation provider.
     *
     * @param server              The Bukkit server
     * @param papiEnabled         Whether PlaceholderAPI is enabled
     * @param translationProvider Provider for translation lookups (can be null)
     */
    public RanksExtension(@NotNull Server server,
                          boolean papiEnabled,
                          @Nullable TranslateFunction.TranslationProvider translationProvider) {
        this(server, papiEnabled, translationProvider, null);
    }

    @Override
    public Map<String, Filter> getFilters() {
        Map<String, Filter> filters = new HashMap<>();

        // Item filters
        filters.put("itemName", new ItemNameFilter());

        // Location filters
        filters.put("formatLoc", new LocationFormatFilter());
        filters.put("formatLocation", new LocationFormatFilter()); // Alias

        // Number filters
        filters.put("currency", new CurrencyFilter());
        filters.put("money", new CurrencyFilter()); // Alias
        filters.put("compact", new CompactFilter());
        filters.put("whole", new WholeFilter());
        filters.put("pct", new PercentFilter());
        filters.put("percent", new PercentFilter()); // Alias

        // Time filters
        filters.put("formatTime", new TimeFormatFilter());
        filters.put("duration", new TimeFormatFilter()); // Alias

        return filters;
    }

    @Override
    public Map<String, Function> getFunctions() {
        Map<String, Function> functions = new HashMap<>();

        // Player function
        functions.put("player", new PlayerFunction(server));

        // Rank function
        if (ranksService != null) {
            functions.put("rank", new RankFunction(ranksService));
        }

        // Translation function
        if (translationProvider != null) {
            functions.put("translate", new TranslateFunction(translationProvider));
            functions.put("t", new TranslateFunction(translationProvider)); // Short alias
        }

        // PlaceholderAPI function
        if (papiEnabled && isPapiAvailable()) {
            functions.put("papi", new PapiFunction());
            functions.put("placeholder", new PapiFunction()); // Alias
        }

        return functions;
    }

    /**
     * Checks if PlaceholderAPI is available.
     */
    private boolean isPapiAvailable() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            return server.getPluginManager().isPluginEnabled("PlaceholderAPI");
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
