package dev.flur.ranks.requirement;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Requirement {

    boolean meetsRequirement(@NotNull Player player);

    void consume(@NotNull Player player);

    String toString();

}
