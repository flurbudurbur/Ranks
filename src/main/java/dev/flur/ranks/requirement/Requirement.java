package dev.flur.ranks.requirement;

import org.bukkit.OfflinePlayer;

public interface Requirement {

    boolean meetsRequirement(OfflinePlayer player);
    String toString();
    String getName();
}