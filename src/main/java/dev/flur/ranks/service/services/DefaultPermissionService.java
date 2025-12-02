package dev.flur.ranks.service.services;

import dev.flur.ranks.service.PermissionService;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Default implementation of the PermissionService interface using Vault.
 */
public class DefaultPermissionService implements PermissionService {

    private static final long CACHE_TTL_MS = 500;

    private record CachedGroup(String group, long expiresAt) {
        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }

    private final Permission permission;
    private final Logger logger;
    private final Map<UUID, CachedGroup> groupCache = new ConcurrentHashMap<>();

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
        UUID uuid = player.getUniqueId();
        CachedGroup cached = groupCache.get(uuid);
        if (cached != null && !cached.isExpired()) {
            return cached.group();
        }

        try {
            String group = permission.getPrimaryGroup(player);
            String result = group != null ? group : "";
            groupCache.put(uuid, new CachedGroup(result, System.currentTimeMillis() + CACHE_TTL_MS));
            return result;
        } catch (Exception e) {
            logger.warning("Error getting primary group for player " + player.getName() + ": " + e.getMessage());
            return "";
        }
    }

    @Override
    public boolean addToGroup(@NotNull Player player, @NotNull String groupName) {
        groupCache.remove(player.getUniqueId());
        try {
            return permission.playerAddGroup(player, groupName);
        } catch (Exception e) {
            logger.warning("Error adding player " + player.getName() + " to group " + groupName + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeFromGroup(@NotNull Player player, @NotNull String groupName) {
        groupCache.remove(player.getUniqueId());
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