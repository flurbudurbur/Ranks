package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.AnnotatedRequirement;
import dev.flur.ranks.requirement.RequirementName;
import dev.flur.ranks.requirement.RequirementParams;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@RequirementName("item-use")
@RequirementParams(minimum = 2, usage = "Format: item1 [item2 ...] amount")
public final class ItemUseRequirement extends AnnotatedRequirement {

    private final int amount;
    private final List<String> items;

    public ItemUseRequirement(String[] params) {
        super(params);

        // Parse and validate the amount (last parameter)
        try {
            this.amount = Integer.parseInt(params[params.length - 1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + params[params.length - 1]);
        }

        List<String> tempItems = List.of(params).subList(0, params.length - 1);

        // Verify all items exist and are items
        for (String item : tempItems) {
            try {
                Material mat = Material.getMaterial(item.toUpperCase());
                if (mat == null || !mat.isItem()) {
                    throw new IllegalArgumentException("Invalid item material: " + item);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid item material: " + item);
            }
        }

        this.items = tempItems;
    }


    @Override
    public boolean meetsRequirement(@NotNull OfflinePlayer player) {
        int total = 0;
        for (String item : this.items) {
            total += Objects.requireNonNull(player.getPlayer())
                    .getStatistic(
                            Statistic.USE_ITEM,
                            Objects.requireNonNull(Material.getMaterial(item.toUpperCase()))
                    );
        }
        return total >= this.amount;
    }
}
