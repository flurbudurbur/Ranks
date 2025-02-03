package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.requirement.Requirement;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
    public boolean meetsRequirement(@NotNull OfflinePlayer player) {
        return Objects.requireNonNull(player.getPlayer()).getLevel() >= level;
    }

    @Override
    public String toString() {
        return "Xp Level: " + level;
    }

    @Override
    public String getName() {
        return "xp-level";
    }
}
