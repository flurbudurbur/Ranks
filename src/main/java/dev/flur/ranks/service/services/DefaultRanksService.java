package dev.flur.ranks.service.services;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.requirement.RequirementFactory;
import dev.flur.ranks.service.ConfigurationService;
import dev.flur.ranks.service.RanksService;
import dev.flur.ranks.service.config.TomlConfiguration;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of RankService.
 */
public class DefaultRanksService implements RanksService {

    private final ConfigurationService configurationService;
    private final Logger logger;
    private final RequirementFactory requirementFactory;
    private final Ranks plugin;
    private final Permission permissions;
    private Map<String, String> ranksCache;

    public DefaultRanksService(@NotNull ConfigurationService configurationService, 
                              @NotNull DefaultRequirementRegistry registry,
                              @NotNull Ranks plugin,
                              @NotNull Logger logger) {
        this.configurationService = configurationService;
        this.logger = logger;
        this.plugin = plugin;
        this.permissions = plugin.getVaultProvider().getPermissions();
        this.requirementFactory = new RequirementFactory(registry);
        this.ranksCache = new HashMap<>();
        loadRanks();
    }

    @Override
    @NotNull
    public List<String> getAllRanks() {
        return new ArrayList<>(ranksCache.keySet());
    }

    @Override
    @NotNull
    public Map<String, String> getNextRanks(@NotNull String currentRank) {
        Map<String, String> nextRanks = new HashMap<>();

        // Get the TOML configuration
        TomlConfiguration ranksConfig = configurationService.getConfiguration("ranks");

        // Get the next ranks for the current rank using TOML dot notation
        String nextRanksPath = "ranks." + currentRank + ".next-ranks";
        Object nextRanksObject = ranksConfig.get(nextRanksPath);

        return mapNextRanks(nextRanks, nextRanksObject);
    }

    @NotNull
    static Map<String, String> mapNextRanks(Map<String, String> nextRanks, Object nextRanksObject) {
        if (nextRanksObject instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> nextRanksMap = (Map<String, Object>) nextRanksObject;

            for (Map.Entry<String, Object> entry : nextRanksMap.entrySet()) {
                String rankName = entry.getKey();
                String displayName = entry.getValue() != null ? entry.getValue().toString() : rankName;
                nextRanks.put(rankName, displayName);
            }
        }

        return nextRanks;
    }


    @Override
    @NotNull
    public List<Requirement> getRequirements(@NotNull String nextRank, @NotNull Player player) {
        List<Requirement> requirements = new ArrayList<>();

        try {
            String primaryGroup = permissions.getPrimaryGroup(player);

            if (!ranksCache.containsKey(primaryGroup)) {
                if (plugin.isDebugEnabled()) {
                    logger.warning("No rank configuration found for player " + player.getName()
                            + " with primary group: " + primaryGroup);
                }
                return requirements;
            }

            TomlConfiguration ranksConfig = configurationService.getConfiguration("ranks");
            String path = ranksCache.get(primaryGroup) + ".next." + nextRank + ".requirements";

            if (plugin.isDebugEnabled()) {
                logger.info("Loading requirements from path: " + path);
            }

            List<String> requirementStrings;
            try {
                requirementStrings = ranksConfig.getStringList(path);
            } catch (NullPointerException e) {
                throw new RuntimeException(e);
            }

            assert requirementStrings != null;
            for (String reqString : requirementStrings) {
                try {
                    Requirement requirement = requirementFactory.createRequirement(reqString);
                    requirements.add(requirement);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to create requirement from string: " + reqString, e);
                }
            }

            if (plugin.isDebugEnabled()) {
                logger.info("Loaded " + requirements.size() + " requirements for " + nextRank);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load requirements for " + nextRank, e);
        }

        return requirements;
    }

    @Override
    public boolean canUpgrade(@NotNull Player player, @NotNull String targetRank) {
        List<Requirement> requirements = getRequirements(targetRank, player);

        for (Requirement requirement : requirements) {
            if (!requirement.meetsRequirement(player)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean upgradeRank(@NotNull Player player, @NotNull String targetRank) {
        if (!canUpgrade(player, targetRank)) {
            return false;
        }

        try {
            // Remove current rank
            String currentRank = getCurrentRank(player);
            if (!currentRank.isEmpty()) {
                permissions.playerRemoveGroup(player, currentRank);
            }

            // Add new rank
            permissions.playerAddGroup(player, targetRank);

            // Consume requirements (e.g., money, items)
            List<Requirement> requirements = getRequirements(targetRank, player);
            for (Requirement requirement : requirements) {
                requirement.consume(player);
            }

            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to upgrade rank for player " + player.getName(), e);
            return false;
        }
    }

    @Override
    @NotNull
    public String getCurrentRank(@NotNull Player player) {
        try {
            String primaryGroup = permissions.getPrimaryGroup(player);
            return primaryGroup != null ? primaryGroup : "";
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to get current rank for player " + player.getName(), e);
            return "";
        }
    }

    @Override
    public void reload() {
        configurationService.reloadConfigurations();
        loadRanks();
    }

    private void loadRanks() {
        Map<String, String> ranks = new HashMap<>();

        try {
            TomlConfiguration ranksFile = configurationService.getConfiguration("ranks");

            for (String key : ranksFile.getKeys(false)) {
                String name = ranksFile.getString(key + ".name");
                if (name == null || name.trim().isEmpty()) {
                    logger.warning("Rank configuration '" + key + "' is missing a name and will be skipped");
                    continue;
                }

                if (ranks.containsKey(name)) {
                    logger.warning("Duplicate rank name '" + name + "' found. Only the first occurrence will be used.");
                    continue;
                }

                ranks.put(name, key);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load ranks configuration", e);
        }

        this.ranksCache = ranks;
    }
}
