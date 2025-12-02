package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.BaseRequirement;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class BlockBreakRequirement extends BaseRequirement {

    private final List<String> materials;

    public BlockBreakRequirement(String[] params) {
        super(params);

        List<String> tempBlock = List.of(params).subList(0, params.length - 1);

        // Verify all blocks exist and are blocks
        for (String material : tempBlock) {
            try {
                Material mat = Material.getMaterial(material.toUpperCase());
                if (mat == null || !mat.isBlock()) {
                    throw new IllegalArgumentException("Invalid block material: " + material);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid block material: " + material);
            }
        }

        this.materials = tempBlock;
    }

    @Override
    public boolean meetsRequirement(@NotNull Player player) {
        // Check each block individually
        for (String material : materials) {
            int breaks = player.getStatistic(
                    Statistic.MINE_BLOCK,
                    Objects.requireNonNull(Material.getMaterial(material.toUpperCase()))
            );
            if (breaks < (int) super.amount) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "block-break: " + String.join(", ", materials) + " - " + (int) super.amount;
    }
}
