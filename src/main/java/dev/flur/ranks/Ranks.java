package dev.flur.ranks;


import dev.flur.ranks.command.CommandManager;
import dev.flur.ranks.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Ranks extends JavaPlugin {

    private static Economy eco = null;
    private static Permission perms = null;


    @Override
    public void onEnable() {

        saveDefaultConfig();
        setupEconomy();
        setupPermissions();

        new Utils();
        new CommandManager();

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return eco != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Economy getEconomy() {
        return eco;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Ranks getPlugin() {
        return getPlugin(Ranks.class);
    }

}
