package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.requirement.AnnotatedRequirement;
import dev.flur.ranks.requirement.RequirementName;
import dev.flur.ranks.requirement.RequirementParams;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

@RequirementName("money")
@RequirementParams(maximum = 1, usage = "Format: amount")
public final class MoneyRequirement extends AnnotatedRequirement {

    public MoneyRequirement(String[] params) {
        super(params);
    }

    @Override
    public boolean meetsRequirement(@NotNull OfflinePlayer player) {
        return Ranks.getEconomy().getBalance(player) >= super.amount;
    }
}
