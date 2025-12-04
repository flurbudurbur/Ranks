package dev.flur.ranks.requirement;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Requirement {

    boolean meetsRequirement(@NotNull Player player);

    void consume(@NotNull Player player);

    String toString();

    /**
     * Gets the requirement type identifier (e.g., "money", "xp-level").
     * Used for template lookups like {{ step.get('money') }}.
     *
     * @return the requirement type name
     */
    default @NotNull String getType() {
        return getClass().getSimpleName()
                .replace("Requirement", "")
                .toLowerCase();
    }

    /**
     * Gets the target amount required to satisfy this requirement.
     * Used in templates like {{ req.target }}.
     *
     * @return the target amount, or 0 if not applicable
     */
    default double getTarget() {
        return 0;
    }

    /**
     * Gets the player's current progress towards this requirement.
     * Used in templates like {{ req.current }}.
     *
     * @param player the player to check
     * @return the current progress amount, or 0 if not applicable
     */
    default double getCurrent(@NotNull Player player) {
        return 0;
    }

}
