package dev.flur.ranks.service.services;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.requirement.RequirementFactory;
import dev.flur.ranks.service.ConfigurationService;
import dev.flur.ranks.service.RanksService;
import dev.flur.ranks.service.RequirementService;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
    private final Map<String, List<Requirement>> requirementCache = new ConcurrentHashMap<>();
    private Set<String> ranksCache;
    private Map<String, List<Transition>> transitionsCache; // from rank -> list of transitions

    /**
     * Represents a rank transition with requirements.
     */
    private record Transition(String from, String to, List<String> requirements) {}

    public DefaultRanksService(@NotNull ConfigurationService configurationService,
                              @NotNull RequirementService requirementService,
                              @NotNull Ranks plugin,
                              @NotNull Logger logger) {
        this.configurationService = configurationService;
        this.logger = logger;
        this.plugin = plugin;
        this.permissions = plugin.getVaultProvider().getPermissions();
        this.requirementFactory = new RequirementFactory(requirementService);
        this.ranksCache = new HashSet<>();
        this.transitionsCache = new HashMap<>();
        loadRanks();
    }

    @Override
    @NotNull
    public List<String> getAllRanks() {
        return List.copyOf(ranksCache);
    }

    @Override
    @NotNull
    public Map<String, String> getNextRanks(@NotNull String currentRank) {
        Map<String, String> nextRanks = new HashMap<>();

        if (!ranksCache.contains(currentRank)) {
            if (plugin.isDebugEnabled()) {
                logger.warning("Current rank '" + currentRank + "' not found in configuration");
            }
            return nextRanks;
        }

        List<Transition> transitions = transitionsCache.get(currentRank);
        if (transitions != null) {
            for (Transition transition : transitions) {
                nextRanks.put(transition.to(), transition.to());
            }
        }

        return nextRanks;
    }

    @Override
    @NotNull
    public List<Requirement> getRequirements(@NotNull String nextRank, @NotNull Player player) {
        try {
            String primaryGroup = permissions.getPrimaryGroup(player);

            if (!ranksCache.contains(primaryGroup)) {
                if (plugin.isDebugEnabled()) {
                    logger.warning("No rank configuration found for player " + player.getName()
                            + " with primary group: " + primaryGroup);
                }
                return List.of();
            }

            // Check cache first - cached list is already immutable via List.copyOf()
            String cacheKey = primaryGroup + ":" + nextRank;
            List<Requirement> cached = requirementCache.get(cacheKey);
            if (cached != null) {
                return cached;
            }

            // Find the transition from current rank to next rank
            List<Transition> transitions = transitionsCache.get(primaryGroup);
            Transition targetTransition = null;
            if (transitions != null) {
                for (Transition t : transitions) {
                    if (t.to().equals(nextRank)) {
                        targetTransition = t;
                        break;
                    }
                }
            }

            if (targetTransition == null) {
                if (plugin.isDebugEnabled()) {
                    logger.warning("No transition found from " + primaryGroup + " to " + nextRank);
                }
                return List.of();
            }

            if (plugin.isDebugEnabled()) {
                logger.info("Loading requirements for transition: " + primaryGroup + " -> " + nextRank);
            }

            List<Requirement> requirements = new ArrayList<>();
            for (String reqString : targetTransition.requirements()) {
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

            // Cache the requirements
            requirementCache.put(cacheKey, List.copyOf(requirements));

            return requirements;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load requirements for " + nextRank, e);
            return List.of();
        }
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
        requirementCache.clear();
        loadRanks();
    }

    @SuppressWarnings("unchecked")
    private void loadRanks() {
        Set<String> ranks = new HashSet<>();
        Map<String, List<Transition>> transitions = new HashMap<>();

        File ranksFile = new File(plugin.getDataFolder(), "ranks.yml");
        if (!ranksFile.exists()) {
            plugin.saveResource("ranks.yml", false);
        }

        try (InputStream input = new FileInputStream(ranksFile)) {
            Yaml yaml = new Yaml();
            List<Map<String, Object>> transitionsList = yaml.load(input);

            if (transitionsList == null) {
                logger.warning("ranks.yml is empty or invalid");
                this.ranksCache = ranks;
                this.transitionsCache = transitions;
                return;
            }

            for (Map<String, Object> transitionMap : transitionsList) {
                String from = String.valueOf(transitionMap.get("from"));
                String to = String.valueOf(transitionMap.get("to"));

                if (from == null || from.equals("null") || from.trim().isEmpty()) {
                    logger.warning("Transition is missing 'from' field and will be skipped");
                    continue;
                }
                if (to == null || to.equals("null") || to.trim().isEmpty()) {
                    logger.warning("Transition from '" + from + "' is missing 'to' field and will be skipped");
                    continue;
                }

                // Deduce ranks from transitions
                ranks.add(from);
                ranks.add(to);

                // Get requirements
                List<String> requirements = new ArrayList<>();
                Object reqObj = transitionMap.get("requirements");
                if (reqObj instanceof List<?> reqList) {
                    for (Object req : reqList) {
                        if (req != null) {
                            requirements.add(String.valueOf(req));
                        }
                    }
                }

                Transition transition = new Transition(from, to, List.copyOf(requirements));
                transitions.computeIfAbsent(from, k -> new ArrayList<>()).add(transition);

                if (plugin.isDebugEnabled()) {
                    logger.info("Loaded transition: " + from + " -> " + to + " with " + requirements.size() + " requirements");
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load ranks configuration", e);
        }

        this.ranksCache = ranks;
        this.transitionsCache = transitions;
    }
}
