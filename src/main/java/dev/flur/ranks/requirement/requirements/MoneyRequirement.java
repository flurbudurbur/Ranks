package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.requirement.Requirement;

public class MoneyRequirement implements Requirement {

    private double money;

    public MoneyRequirement(String[] params) {
        if (Ranks.getEconomy() == null) throw new IllegalStateException("Economy not found");
        if (params.length != 1) throw new IllegalArgumentException("Invalid input");
        try {
            money = Double.parseDouble(params[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input");
        } catch (ArrayIndexOutOfBoundsException e) {
            Ranks.getPlugin().getLogger().severe("ArrayIndexOutOfBoundsException");
        }
    }

    @Override
    public String toString() {
        return "Money: " + money;
    }
}
