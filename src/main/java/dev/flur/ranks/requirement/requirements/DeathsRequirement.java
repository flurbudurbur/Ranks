package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.BaseRequirement;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class DeathsRequirement extends BaseRequirement {

    public DeathsRequirement(String @NotNull [] params) {
        super(params);
    }

    @Override
    public boolean meetsRequirement(@NotNull Player player) {
        return player.getStatistic(Statistic.DEATHS) >= (int) super.amount;
    }

    @Override
    public double getCurrent(@NotNull Player player) {
        return player.getStatistic(Statistic.DEATHS);
    }

    @Override
    public String toString() {
        return "deaths: " + (int) super.amount;
    }
}
