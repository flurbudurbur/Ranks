package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.requirement.Requirement;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class MoneyRequirement implements Requirement {

    private final double money;

    public MoneyRequirement(String[] params) {
        if (Ranks.getEconomy() == null) throw new IllegalStateException("Economy not found");
        if (params.length != 1) throw new IllegalArgumentException("Invalid input");
        try {
            money = Double.parseDouble(params[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input");
        }
    }

    @Override
    public boolean meetsRequirement(@NotNull OfflinePlayer player) {
        double balance = Ranks.getEconomy().getBalance(player);
        return balance >= money;
    }

    @Override
    public String toString() {
        return "Money: " + money;
    }

    @Override
    public String getName() {
        return "money";
    }
}
