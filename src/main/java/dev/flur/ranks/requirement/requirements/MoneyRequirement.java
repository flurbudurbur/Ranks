package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.Requirement;

public class MoneyRequirement implements Requirement {

    public MoneyRequirement(String value) {

    }

    @Override
    public boolean hasSubRequirement() {
        return false;
    }
}
