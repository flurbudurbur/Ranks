package dev.flur.ranks.requirement;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public interface Requirement {

    boolean meetsRequirement(@NotNull OfflinePlayer player);
    String toString();
    String getName();
}