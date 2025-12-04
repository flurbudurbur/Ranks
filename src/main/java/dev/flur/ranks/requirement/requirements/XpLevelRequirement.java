package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.BaseRequirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class XpLevelRequirement extends BaseRequirement {

    public XpLevelRequirement(String @NotNull [] params) {
        super(params);
    }

    @Override
    public boolean meetsRequirement(@NotNull Player player) {
        return (int) super.amount <= player.getLevel();
    }

    @Override
    public double getCurrent(@NotNull Player player) {
        return player.getLevel();
    }

    @Override
    public String toString() {
        return "xp-level: " + (int) super.amount;
    }
}
