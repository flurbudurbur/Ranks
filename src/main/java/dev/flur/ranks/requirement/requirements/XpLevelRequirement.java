package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.AnnotatedRequirement;
import dev.flur.ranks.requirement.annotations.RequirementAnnotation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@RequirementAnnotation(
        name = "xp-level",
        maximum = 1,
        usage = "Format: level"
)
public final class XpLevelRequirement extends AnnotatedRequirement {

    public XpLevelRequirement(String[] params) {
        super(params);
    }

    @Override
    public boolean meetsRequirement(@NotNull Player player) {
        return (int) super.amount <= player.getLevel();
    }

    @Override
    public String toString() {
        return "xp-level: " + (int) super.amount;
    }
}
