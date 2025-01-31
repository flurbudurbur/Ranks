package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.Requirement;

public class XpLevelRequirement implements Requirement {

    public XpLevelRequirement() {}

    @Override
    public boolean hasSubRequirement() {
        return false;
    }
}
