package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.AnnotatedRequirement;
import dev.flur.ranks.requirement.annotations.RequirementAnnotation;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequirementAnnotation(
        name = "deaths",
        usage = "Format: deaths",
        maximum = 1
)
public final class DeathsRequirement extends AnnotatedRequirement {

    public DeathsRequirement(String[] params) {
        super(params);
    }

    @Override
    public boolean meetsRequirement(@NotNull Player player) {
        return player.getStatistic(Statistic.DEATHS) >= (int) super.amount;
    }

    @Override
    public String toString() {
        return "deaths: " + (int) super.amount;
    }
}
