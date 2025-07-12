package dev.flur.ranks.message;

import dev.flur.ranks.Ranks;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.FileLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Responsible for processing templates using Pebble and formatting them with MiniMessage.
 */
public class TemplateProcessor {
    private final Ranks plugin;
    private final PebbleEngine pebbleEngine;
    private final MiniMessage miniMessage;

    public TemplateProcessor(@NotNull Ranks plugin) {
        this.plugin = plugin;
        this.pebbleEngine = createPebbleEngine();
        this.miniMessage = MiniMessage.miniMessage();
    }

    /**
     * Creates and configures the Pebble template engine.
     *
     * @return The configured PebbleEngine instance
     */
    private PebbleEngine createPebbleEngine() {
        // Create the locale directory if it doesn't exist
        File localeDir = new File(plugin.getDataFolder(), "locale");
        if (!localeDir.exists()) {
            localeDir.mkdirs();
        }

        // Create base file loader
        FileLoader fileLoader = new FileLoader();
        fileLoader.setPrefix(localeDir.getAbsolutePath());
        fileLoader.setSuffix(".yml");

        // Wrap with custom syntax loader
        RanksSyntaxLoader syntaxLoader = new RanksSyntaxLoader(fileLoader);

        return new PebbleEngine.Builder()
                .loader(syntaxLoader)
                .strictVariables(false)
                .build();
    }

    /**
     * Processes a template with the given context using Pebble for templating and MiniMessage for formatting.
     *
     * @param template The raw template string
     * @param context  The context variables for templating
     * @return The processed Component
     */
    public Component processTemplate(String template, Map<String, Object> context) {
        try {
            // Create a Pebble template from the string
            PebbleTemplate pebbleTemplate = pebbleEngine.getLiteralTemplate(template);

            // Render the template with the context
            Writer writer = new StringWriter();
            pebbleTemplate.evaluate(writer, context);
            String processedTemplate = writer.toString();

            // Parse the processed template with MiniMessage
            return miniMessage.deserialize(processedTemplate);
        } catch (Exception e) {
            plugin.getLogger().warning("Error processing template: " + template);
            plugin.getLogger().warning("Error: " + e.getMessage());

            // Return a fallback component with the raw template
            return miniMessage.deserialize("<red>Template Error: " + template + "</red>");
        }
    }
}