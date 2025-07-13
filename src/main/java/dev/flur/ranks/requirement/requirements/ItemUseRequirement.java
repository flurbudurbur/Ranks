package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.AnnotatedRequirement;
import dev.flur.ranks.requirement.annotations.RequirementAnnotation;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@RequirementAnnotation(
        name = "item-use",
        minimum = 2,
        maximum = 10,
        usage = "Format: item1 [item2 ...] amount"
)
public final class ItemUseRequirement extends AnnotatedRequirement {

    private final List<String> items;

    public ItemUseRequirement(String[] params) {
        super(params);

        List<String> tempItems = List.of(params).subList(0, params.length - 1);

        // Verify all items exist and are items
        for (String item : tempItems) {
            Material mat = Material.getMaterial(item.toUpperCase());
            if (mat == null) {
                throw new IllegalArgumentException("Invalid item material (not found): " + item);
            }

            // Check if material is usable with USE_ITEM statistic
            // This is a more reliable check than isItem() && !isBlock()
            try {
                // Try to validate that this material can be used with USE_ITEM statistic
                if (!mat.isItem()) {
                    throw new IllegalArgumentException("Invalid item material (not an item): " + item);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid item material (not an item): " + item);
            }
        }

        this.items = tempItems;
    }

    @Override
    public boolean meetsRequirement(@NotNull Player player) {
        // Check each item individually
        for (String item : this.items) {
            try {
                int uses = player.getStatistic(
                        Statistic.USE_ITEM,
                        Objects.requireNonNull(Material.getMaterial(item.toUpperCase()))
                );
                if (uses < (int) super.amount) {
                    return false;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid item: " + item);
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "item-use: " + String.join(", ", items) + " - " + (int) super.amount;
    }
}