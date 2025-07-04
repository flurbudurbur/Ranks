package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.AnnotatedRequirement;
import dev.flur.ranks.requirement.RequirementName;
import dev.flur.ranks.requirement.RequirementParams;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


@RequirementName("xp-level")
@RequirementParams(minimum = 1, maximum = 1, usage = "Format: level")
public final class XpLevelRequirement extends AnnotatedRequirement {

    public XpLevelRequirement(String[] params) {
        super(params);
    }

    @Override
    public boolean meetsRequirement(@NotNull OfflinePlayer player) {
        return (int) super.amount <= Objects.requireNonNull(player.getPlayer()).getLevel();
    }
}
