package dev.flur.ranks.template.extension.filter;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

/**
 * Pebble filter that gets the display name of an ItemStack.
 * <p>
 * Usage: {@code {{ item | itemName }}}
 * <p>
 * Returns the custom display name if present, otherwise the material name formatted.
 */
public class ItemNameFilter implements Filter {

    @Override
    public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                        EvaluationContext context, int lineNumber) {
        if (input == null) {
            return "";
        }

        if (input instanceof ItemStack item) {
            return getItemName(item);
        }

        // If it's a string (material name), format it nicely
        if (input instanceof String materialName) {
            return formatMaterialName(materialName);
        }

        return input.toString();
    }

    @Override
    public List<String> getArgumentNames() {
        return List.of();
    }

    /**
     * Gets the display name of an item.
     */
    private String getItemName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            return PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        }

        return formatMaterialName(item.getType().name());
    }

    /**
     * Formats a material name to be more readable.
     * Example: DIAMOND_SWORD → Diamond Sword
     */
    private String formatMaterialName(String materialName) {
        if (materialName == null || materialName.isEmpty()) {
            return "";
        }

        String[] words = materialName.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(" ");
            }
            result.append(capitalize(words[i]));
        }

        return result.toString();
    }

    /**
     * Capitalizes the first letter of a word.
     */
    private String capitalize(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }
}
