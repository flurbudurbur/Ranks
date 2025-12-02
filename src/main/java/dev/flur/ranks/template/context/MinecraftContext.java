package dev.flur.ranks.template.context;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper classes for Minecraft objects that provide template-friendly access.
 */
public final class MinecraftContext {

    private MinecraftContext() {
        // Utility class
    }

    /**
     * Wrapper for Player that provides template-friendly property access.
     */
    public static class PlayerContext {

        private final Player player;

        public PlayerContext(@NotNull Player player) {
            this.player = player;
        }

        @NotNull
        public Player getPlayer() {
            return player;
        }

        @NotNull
        public String getName() {
            return player.getName();
        }

        @NotNull
        public String getDisplayName() {
            return PlainTextComponentSerializer.plainText().serialize(player.displayName());
        }

        @NotNull
        public String getUuid() {
            return player.getUniqueId().toString();
        }

        public double getHealth() {
            return player.getHealth();
        }

        public double getMaxHealth() {
            var attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            return attribute != null ? attribute.getValue() : 20.0;
        }

        public int getFoodLevel() {
            return player.getFoodLevel();
        }

        public int getLevel() {
            return player.getLevel();
        }

        public float getExp() {
            return player.getExp();
        }

        public int getTotalExperience() {
            return player.getTotalExperience();
        }

        @NotNull
        public String getGameMode() {
            return player.getGameMode().name();
        }

        public boolean isFlying() {
            return player.isFlying();
        }

        public boolean isSneaking() {
            return player.isSneaking();
        }

        public boolean isSprinting() {
            return player.isSprinting();
        }

        public boolean isOnline() {
            return player.isOnline();
        }

        public boolean isOp() {
            return player.isOp();
        }

        @NotNull
        public String getWorld() {
            return player.getWorld().getName();
        }

        public int getX() {
            return player.getLocation().getBlockX();
        }

        public int getY() {
            return player.getLocation().getBlockY();
        }

        public int getZ() {
            return player.getLocation().getBlockZ();
        }

        @NotNull
        public Location getLocation() {
            return player.getLocation();
        }

        public long getFirstPlayed() {
            return player.getFirstPlayed();
        }

        public long getLastPlayed() {
            return player.getLastPlayed();
        }

        /**
         * Converts to a map for flexible template access.
         */
        @NotNull
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("name", getName());
            map.put("displayName", getDisplayName());
            map.put("uuid", getUuid());
            map.put("health", getHealth());
            map.put("maxHealth", getMaxHealth());
            map.put("foodLevel", getFoodLevel());
            map.put("level", getLevel());
            map.put("exp", getExp());
            map.put("totalExperience", getTotalExperience());
            map.put("gameMode", getGameMode());
            map.put("isFlying", isFlying());
            map.put("isSneaking", isSneaking());
            map.put("isSprinting", isSprinting());
            map.put("isOnline", isOnline());
            map.put("isOp", isOp());
            map.put("world", getWorld());
            map.put("x", getX());
            map.put("y", getY());
            map.put("z", getZ());
            map.put("location", getLocation());
            map.put("firstPlayed", getFirstPlayed());
            map.put("lastPlayed", getLastPlayed());
            return map;
        }
    }

    /**
     * Wrapper for Location that provides template-friendly access.
     */
    public static class LocationContext {

        private final Location location;

        public LocationContext(@NotNull Location location) {
            this.location = location;
        }

        @NotNull
        public Location getLocation() {
            return location;
        }

        @Nullable
        public String getWorld() {
            return location.getWorld() != null ? location.getWorld().getName() : null;
        }

        public double getX() {
            return location.getX();
        }

        public double getY() {
            return location.getY();
        }

        public double getZ() {
            return location.getZ();
        }

        public int getBlockX() {
            return location.getBlockX();
        }

        public int getBlockY() {
            return location.getBlockY();
        }

        public int getBlockZ() {
            return location.getBlockZ();
        }

        public float getYaw() {
            return location.getYaw();
        }

        public float getPitch() {
            return location.getPitch();
        }

        @Override
        public String toString() {
            return String.format("%s: %d, %d, %d",
                    getWorld() != null ? getWorld() : "unknown",
                    getBlockX(), getBlockY(), getBlockZ());
        }
    }

    /**
     * Wrapper for ItemStack that provides template-friendly access.
     */
    public static class ItemContext {

        private final ItemStack item;

        public ItemContext(@NotNull ItemStack item) {
            this.item = item;
        }

        @NotNull
        public ItemStack getItem() {
            return item;
        }

        @NotNull
        public String getType() {
            return item.getType().name();
        }

        @NotNull
        public String getTypeName() {
            return formatMaterialName(item.getType().name());
        }

        public int getAmount() {
            return item.getAmount();
        }

        public int getDurability() {
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof Damageable damageable) {
                return damageable.getDamage();
            }
            return 0;
        }

        @Nullable
        public String getDisplayName() {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                return PlainTextComponentSerializer.plainText().serialize(meta.displayName());
            }
            return null;
        }

        @NotNull
        public String getName() {
            String displayName = getDisplayName();
            return displayName != null ? displayName : getTypeName();
        }

        public boolean hasEnchantments() {
            return !item.getEnchantments().isEmpty();
        }

        public int getMaxStackSize() {
            return item.getMaxStackSize();
        }

        private String formatMaterialName(String materialName) {
            String[] words = materialName.toLowerCase().split("_");
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) result.append(" ");
                if (!words[i].isEmpty()) {
                    result.append(Character.toUpperCase(words[i].charAt(0)));
                    result.append(words[i].substring(1));
                }
            }
            return result.toString();
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}
