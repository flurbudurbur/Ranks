package dev.flur.ranks.service;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.service.services.DefaultRequirementRegistry;
import dev.flur.ranks.service.services.*;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Service container for managing all plugin services.
 */
public class ServiceContainer implements Lifecycle {

    private final Ranks plugin;
    private final Logger logger;

    // Core services
    private ConfigurationService configurationService;
    private MessageService messageService;
    private PermissionService permissionService;
    private DefaultRequirementRegistry requirementRegistry;

    // Business logic services
    private RanksService ranksService;
    private RequirementValidator requirementValidator;
    private RankProgressionService rankProgressionService;
    private PlayerRankService playerRankService;

    // Rankup services
    private RankupValidator rankupValidator;
    private RankupProcessor rankupProcessor;
    private RankupNotifier rankupNotifier;

    public ServiceContainer(@NotNull Ranks plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        initialize();
    }

    private void initialize() {
        // Initialize services in dependency order

        // Core services first
        this.configurationService = createConfigurationService();
        this.permissionService = createPermissionService();
        this.messageService = createMessageService();
        this.requirementRegistry = createRequirementRegistry();

        // Then business logic services
        this.requirementValidator = createRequirementValidator();
        this.ranksService = createRanksService();
        this.rankProgressionService = createRankProgressionService();
        this.playerRankService = createPlayerRankService();

        // Finally rankup services
        this.rankupValidator = createRankupValidator();
        this.rankupProcessor = createRankupProcessor();
        this.rankupNotifier = createRankupNotifier();
    }

    @Contract(" -> new")
    private @NotNull ConfigurationService createConfigurationService() {
        return new DefaultConfigurationService(plugin);
    }

    private @NotNull PermissionService createPermissionService() {
        Permission vaultPermission = plugin.getVaultProvider().getPermissions();
        return new DefaultPermissionService(vaultPermission, logger);
    }

    @Contract(" -> new")
    private @NotNull MessageService createMessageService() {
        return new DefaultMessageService(plugin, configurationService);
    }

    @Contract(" -> new")
    private @NotNull DefaultRequirementRegistry createRequirementRegistry() {
        DefaultRequirementRegistry registry = new DefaultRequirementRegistry(logger);
        registry.discoverRequirements("dev.flur.ranks.requirement.requirements");
        return registry;
    }

    @Contract(value = " -> new", pure = true)
    private @NotNull RequirementValidator createRequirementValidator() {
        return new DefaultRequirementValidator(logger, requirementRegistry);
    }

    @Contract(" -> new")
    private @NotNull RanksService createRanksService() {
        return new DefaultRanksService(configurationService, requirementRegistry, plugin, logger);
    }

    private @NotNull RankProgressionService createRankProgressionService() {
        FileConfiguration ranksConfig = configurationService.getConfiguration("ranks.yml");
        boolean broadcastRankups = plugin.getConfig().getBoolean("broadcast-rankups", true);
        return new DefaultRankProgressionService(
                permissionService,
                requirementValidator,
                messageService,
                ranksConfig,
                logger,
                broadcastRankups);
    }

    private @NotNull PlayerRankService createPlayerRankService() {
        FileConfiguration ranksConfig = configurationService.getConfiguration("ranks.yml");
        return new DefaultPlayerRankService(
                permissionService,
                requirementValidator,
                ranksConfig,
                logger);
    }

    @Contract(" -> new")
    private @NotNull RankupValidator createRankupValidator() {
        return new DefaultRankupValidator(
                playerRankService,
                requirementValidator,
                logger);
    }

    @Contract(" -> new")
    private @NotNull RankupProcessor createRankupProcessor() {
        return new DefaultRankupProcessor(
                playerRankService,
                rankProgressionService,
                requirementValidator,
                logger);
    }

    @Contract(" -> new")
    private @NotNull RankupNotifier createRankupNotifier() {
        return new DefaultRankupNotifier(
                messageService,
                requirementValidator,
                rankProgressionService,
                logger);
    }

    @NotNull
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @NotNull
    public MessageService getMessageService() {
        return messageService;
    }

    @NotNull
    public PermissionService getPermissionService() {
        return permissionService;
    }

    @NotNull
    public RanksService getRanksService() {
        return ranksService;
    }

    @NotNull
    public RequirementValidator getRequirementValidator() {
        return requirementValidator;
    }

    @NotNull
    public RankProgressionService getRankProgressionService() {
        return rankProgressionService;
    }

    @NotNull
    public PlayerRankService getPlayerRankService() {
        return playerRankService;
    }

    @NotNull
    public RankupValidator getRankupValidator() {
        return rankupValidator;
    }

    @NotNull
    public RankupProcessor getRankupProcessor() {
        return rankupProcessor;
    }

    @NotNull
    public RankupNotifier getRankupNotifier() {
        return rankupNotifier;
    }

    /**
     * Gets the DefaultRequirementRegistry instance.
     *
     * @return The DefaultRequirementRegistry instance
     */
    @NotNull
    public DefaultRequirementRegistry getRequirementRegistry() {
        return requirementRegistry;
    }

    /**
     * Reloads all services.
     */
    public void reload() {
        configurationService.reloadConfigurations();
        messageService.reload();
        ranksService.reload();
    }

    @Override
    public void start() {
        // Services are already started in the initialize method
        logger.info("Service container started");
    }

    @Override
    public void stop() {
        // Shutdown all services that need to be stopped
        if (messageService != null) {
            messageService.shutdown();
        }
        logger.info("Service container stopped");
    }

    @Override
    public boolean isHealthy() {
        // Check if all required services are available and healthy
        return configurationService != null
                && messageService != null
                && permissionService != null
                && ranksService != null
                && requirementValidator != null
                && rankProgressionService != null
                && playerRankService != null
                && rankupValidator != null
                && rankupProcessor != null
                && rankupNotifier != null;
    }
}
