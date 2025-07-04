package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.requirement.AnnotatedRequirement;
import dev.flur.ranks.requirement.RequirementName;
import dev.flur.ranks.requirement.RequirementParams;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;

@RequirementName("deaths")
@RequirementParams(usage = "Format: deaths")
public final class DeathsRequirement extends AnnotatedRequirement {

    private int deaths = 0;

    public DeathsRequirement(String[] params) {
        super(params);
        try {
            this.deaths = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            Ranks.getPlugin().getLogger().warning("Invalid deaths requirement: " + params[0]);
        }
    }

    @Override
    public boolean meetsRequirement(@NotNull OfflinePlayer player) {
        return player.getStatistic(Statistic.DEATHS) >= deaths;
    }

}
