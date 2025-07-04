package dev.flur.ranks;


import dev.flur.ranks.command.AnnotationCommandManager;
import dev.flur.ranks.utils.Utils;
import dev.flur.ranks.vault.DefaultVaultProvider;
import dev.flur.ranks.vault.VaultProvider;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public class Ranks extends JavaPlugin {

    private static VaultProvider vaultProvider;
    public static boolean debug = false;


    @Override
    public void onEnable() {

        saveDefaultConfig();
        debug = getConfig().getBoolean("debug");

        vaultProvider = new DefaultVaultProvider(this);

        // Initialize command manager to register annotation-based commands
        new AnnotationCommandManager(this);

        new Utils();
    }

    public static Economy getEconomy() {
        return vaultProvider.getEconomy();
    }

    public static Permission getPermissions() {
        return vaultProvider.getPermissions();
    }

    public static Ranks getPlugin() {
        return getPlugin(Ranks.class);
    }

    public static VaultProvider getVaultProvider() {
        return vaultProvider;
    }

    public static void setVaultProvider(VaultProvider provider) {
        vaultProvider = provider;
    }
}
