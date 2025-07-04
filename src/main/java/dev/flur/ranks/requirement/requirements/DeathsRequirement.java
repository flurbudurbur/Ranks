package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.AnnotatedRequirement;
import dev.flur.ranks.requirement.RequirementName;
import dev.flur.ranks.requirement.RequirementParams;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;

@RequirementName("deaths")
@RequirementParams(usage = "Format: deaths")
public final class DeathsRequirement extends AnnotatedRequirement {

    public DeathsRequirement(String[] params) {
        super(params);
    }

    @Override
    public boolean meetsRequirement(@NotNull OfflinePlayer player) {
        return player.getStatistic(Statistic.DEATHS) >= (int) super.amount;
    }

}
