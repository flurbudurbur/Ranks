package dev.flur.ranks;

import dev.flur.ranks.command.commands.RanksCommand;
import dev.flur.ranks.command.commands.RankupCommand;
import dev.flur.ranks.command.commands.RequirementsCommand;
import dev.flur.ranks.command.commands.subcommands.ReloadSubCommand;
import dev.flur.ranks.service.*;
import dev.flur.ranks.service.services.*;
import dev.flur.ranks.template.service.DefaultTemplateService;
import dev.flur.ranks.template.service.TemplateService;
import dev.flur.ranks.vault.DefaultVaultProvider;
import dev.flur.ranks.vault.VaultProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class Ranks extends JavaPlugin {

    private VaultProvider vaultProvider;
    private boolean debug = false;

    // Services
    private ConfigurationService configurationService;
    private TemplateService templateService;
    private MessageService messageService;
    private PermissionService permissionService;
    private RequirementService requirementService;
    private RanksService ranksService;
    private RankupService rankupService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveDefaultResources();
        debug = getConfig().getBoolean("debug");

        vaultProvider = new DefaultVaultProvider(this);
        initializeServices();
        registerCommands();

        getLogger().info("Ranks plugin enabled");
    }

    private void saveDefaultResources() {
        // Save locale files
        saveResourceIfMissing("locale/en.yml");

        // Save template layouts
        saveResourceIfMissing("templates/broadcast");
        saveResourceIfMissing("templates/default");
        saveResourceIfMissing("templates/unmet-requirements");
    }

    private void saveResourceIfMissing(String resourcePath) {
        File file = new File(getDataFolder(), resourcePath);
        if (file.exists()) {
            return;
        }

        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            getLogger().warning("Failed to create directory: " + parent.getPath());
            return;
        }

        try (InputStream in = getResource(resourcePath)) {
            if (in == null) {
                getLogger().warning("Resource not found: " + resourcePath);
                return;
            }
            Files.copy(in, file.toPath());
        } catch (IOException e) {
            getLogger().warning("Failed to save resource: " + resourcePath);
        }
    }

    private void initializeServices() {
        // Core services
        configurationService = new DefaultConfigurationService(this);
        templateService = new DefaultTemplateService(this, configurationService);
        permissionService = new DefaultPermissionService(vaultProvider.getPermissions(), getLogger());
        messageService = new DefaultMessageService(this, templateService);
        requirementService = new DefaultRequirementService(getLogger());

        // Business logic services
        ranksService = new DefaultRanksService(configurationService, requirementService, this, getLogger());
        rankupService = new DefaultRankupService(
                permissionService,
                requirementService,
                messageService,
                ranksService,
                getLogger(),
                getConfig().getBoolean("broadcast-rankups", true));
    }

    private void registerCommands() {
        RanksCommand ranksCommand = new RanksCommand(ranksService);
        ranksCommand.registerSubCommand("reload", new ReloadSubCommand(this, messageService));
        register("ranks", ranksCommand);

        register("rankup", new RankupCommand(rankupService, getLogger()));
        register("requirements", new RequirementsCommand(ranksService, getLogger()));
    }

    private void register(String name, dev.flur.ranks.command.BaseCommand command) {
        var cmd = getCommand(name);
        if (cmd != null) {
            cmd.setExecutor(command);
            cmd.setTabCompleter(command);
        } else {
            getLogger().warning("Command '" + name + "' not found in plugin.yml");
        }
    }

    @Override
    public void onDisable() {
        if (templateService != null) {
            templateService.shutdown();
        }
        if (messageService != null) {
            messageService.shutdown();
        }
        getLogger().info("Ranks plugin disabled");
    }

    public void reload() {
        configurationService.reloadConfigurations();
        templateService.reload();
        messageService.reload();
        ranksService.reload();
    }

    public VaultProvider getVaultProvider() {
        return vaultProvider;
    }

    public RequirementService getRequirementService() {
        return requirementService;
    }

    public boolean isDebugEnabled() {
        return debug;
    }
}
