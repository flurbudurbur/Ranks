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

@RequirementName("block-break")
@RequirementParams(minimum = 2, usage = "Format: block1 [block2 ...] amount")
public final class BlockBreakRequirement extends AnnotatedRequirement {

    private final int amount;
    private final List<String> materials;

    public BlockBreakRequirement(String[] params) {
        super(params);

        // Parse and validate the amount (last parameter)
        try {
            this.amount = Integer.parseInt(params[params.length - 1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + params[params.length - 1]);
        }

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
    public boolean meetsRequirement(@NotNull OfflinePlayer player) {
        // Your existing implementation
        int total = 0;
        for (String material : materials) {
            total += Objects.requireNonNull(player.getPlayer())
                    .getStatistic(
                            Statistic.MINE_BLOCK,
                            Objects.requireNonNull(Material.getMaterial(material.toUpperCase()))
                    );
        }
        return total >= amount;
    }
}
