package dev.flur.ranks.service;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Service interface for managing permissions.
 */
public interface PermissionService {

    /**
     * Checks if a player has a specific permission.
     *
     * @param player     The player to check
     * @param permission The permission to check for
     * @return True if the player has the permission, false otherwise
     */
    boolean hasPermission(@NotNull Player player, @NotNull String permission);

    /**
     * Gets a player's primary group.
     *
     * @param player The player to get the group for
     * @return The player's primary group name
     */
    @NotNull
    String getPrimaryGroup(@NotNull Player player);

    /**
     * Adds a player to a group.
     *
     * @param player    The player to add to the group
     * @param groupName The group to add the player to
     * @return True if the operation was successful, false otherwise
     */
    boolean addToGroup(@NotNull Player player, @NotNull String groupName);

    /**
     * Removes a player from a group.
     *
     * @param player    The player to remove from the group
     * @param groupName The group to remove the player from
     * @return True if the operation was successful, false otherwise
     */
    boolean removeFromGroup(@NotNull Player player, @NotNull String groupName);

    /**
     * Checks if a group exists.
     *
     * @param groupName The group name to check
     * @return True if the group exists, false otherwise
     */
    boolean groupExists(@NotNull String groupName);
}