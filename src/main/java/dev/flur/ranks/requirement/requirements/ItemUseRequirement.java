package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.BaseRequirement;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ItemUseRequirement extends BaseRequirement {

    private final List<Material> items;

    public ItemUseRequirement(String[] params) {
        super(params);

        List<Material> resolved = new ArrayList<>();
        for (int i = 0; i < params.length - 1; i++) {
            Material mat = Material.getMaterial(params[i].toUpperCase());
            if (mat == null) {
                throw new IllegalArgumentException("Invalid item material (not found): " + params[i]);
            }
            if (!mat.isItem()) {
                throw new IllegalArgumentException("Invalid item material (not an item): " + params[i]);
            }
            resolved.add(mat);
        }

        this.items = List.copyOf(resolved);
    }

    @Override
    public boolean meetsRequirement(@NotNull Player player) {
        for (Material item : items) {
            if (player.getStatistic(Statistic.USE_ITEM, item) < (int) super.amount) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String itemNames = items.stream()
                .map(Material::name)
                .collect(Collectors.joining(", "));
        return "item-use: " + itemNames + " - " + (int) super.amount;
    }
}