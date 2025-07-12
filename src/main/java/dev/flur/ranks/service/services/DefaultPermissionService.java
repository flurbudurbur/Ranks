package dev.flur.ranks.service.services;

import dev.flur.ranks.service.PermissionService;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Default implementation of the PermissionService interface using Vault.
 */
public class DefaultPermissionService implements PermissionService {

    private final Permission permission;
    private final Logger logger;

    public DefaultPermissionService(@NotNull Permission permission, @NotNull Logger logger) {
        this.permission = permission;
        this.logger = logger;
    }

    @Override
    public boolean hasPermission(@NotNull Player player, @NotNull String permissionNode) {
        try {
            return permission.has(player, permissionNode);
        } catch (Exception e) {
            logger.warning("Error checking permission '" + permissionNode + "' for player " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    @NotNull
    public String getPrimaryGroup(@NotNull Player player) {
        try {
            String group = permission.getPrimaryGroup(player);
            return group != null ? group : "";
        } catch (Exception e) {
            logger.warning("Error getting primary group for player " + player.getName() + ": " + e.getMessage());
            return "";
        }
    }

    @Override
    public boolean addToGroup(@NotNull Player player, @NotNull String groupName) {
        try {
            return permission.playerAddGroup(player, groupName);
        } catch (Exception e) {
            logger.warning("Error adding player " + player.getName() + " to group " + groupName + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeFromGroup(@NotNull Player player, @NotNull String groupName) {
        try {
            return permission.playerRemoveGroup(player, groupName);
        } catch (Exception e) {
            logger.warning("Error removing player " + player.getName() + " from group " + groupName + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean groupExists(@NotNull String groupName) {
        try {
            return permission.groupHas((String) null, groupName, "");
        } catch (Exception e) {
            // This is a hack to check if a group exists since Vault doesn't provide a direct method
            // If the group doesn't exist, Vault will throw an exception
            return false;
        }
    }
}