package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.requirement.Requirement;

public class XpLevelRequirement implements Requirement {

    private int level;

    public XpLevelRequirement(String[] params) {
        if (params.length != 1) throw new IllegalArgumentException("Invalid input");
        try {
            level = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input");
        } catch (ArrayIndexOutOfBoundsException e) {
            Ranks.getPlugin().getLogger().severe("ArrayIndexOutOfBoundsException");
        }
    }

    @Override
    public String toString() {
        return "Xp Level: " + level;
    }
}
