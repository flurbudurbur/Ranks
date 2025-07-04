package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.requirement.AnnotatedRequirement;
import dev.flur.ranks.requirement.RequirementName;
import dev.flur.ranks.requirement.RequirementParams;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

@RequirementName("money")
@RequirementParams(usage = "Format: amount")
public final class MoneyRequirement extends AnnotatedRequirement {

    private final double amount;

    public MoneyRequirement(String[] params) {
        super(params);

        try {
            this.amount = Double.parseDouble(params[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + params[0]);
        }
    }

    @Override
    public boolean meetsRequirement(@NotNull OfflinePlayer player) {
        return Ranks.getEconomy().getBalance(player) >= this.amount;
    }
}
