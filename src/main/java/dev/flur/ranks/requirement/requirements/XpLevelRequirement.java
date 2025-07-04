package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.AnnotatedRequirement;
import dev.flur.ranks.requirement.RequirementName;
import dev.flur.ranks.requirement.RequirementParams;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


@RequirementName("xp-level")
@RequirementParams(usage = "Format: level")
public final class XpLevelRequirement extends AnnotatedRequirement {

    private final int level;

    public XpLevelRequirement(String[] params) {
        super(params);

        try {
            this.level = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid XP level format: " + params[0]);
        }
    }

    @Override
    public boolean meetsRequirement(@NotNull OfflinePlayer player) {
        return this.level <= Objects.requireNonNull(player.getPlayer()).getLevel();
    }
}