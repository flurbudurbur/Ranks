package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.BaseRequirement;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class BlockBreakRequirement extends BaseRequirement {

    private final List<Material> materials;

    public BlockBreakRequirement(String[] params) {
        super(params);

        List<Material> resolved = new ArrayList<>();
        for (int i = 0; i < params.length - 1; i++) {
            Material mat = Material.getMaterial(params[i].toUpperCase());
            if (mat == null || !mat.isBlock()) {
                throw new IllegalArgumentException("Invalid block material: " + params[i]);
            }
            resolved.add(mat);
        }

        this.materials = List.copyOf(resolved);
    }

    @Override
    public boolean meetsRequirement(@NotNull Player player) {
        for (Material material : materials) {
            if (player.getStatistic(Statistic.MINE_BLOCK, material) < (int) super.amount) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String materialNames = materials.stream()
                .map(Material::name)
                .collect(Collectors.joining(", "));
        return "block-break: " + materialNames + " - " + (int) super.amount;
    }
}
