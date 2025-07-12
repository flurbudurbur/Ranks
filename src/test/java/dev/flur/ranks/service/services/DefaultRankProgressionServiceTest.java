package dev.flur.ranks.service.services;

import dev.flur.ranks.message.Messages;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.service.MessageService;
import dev.flur.ranks.service.PermissionService;
import dev.flur.ranks.service.RequirementValidator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultRankProgressionServiceTest {

    private PermissionService permissionService;
    private RequirementValidator requirementValidator;
    private MessageService messageService;
    private FileConfiguration ranksConfig;
    private Logger logger;
    private Player player;
    private Requirement requirement1;
    private Requirement requirement2;
    private DefaultRankProgressionService rankProgressionService;
    private boolean broadcastRankups;

    @BeforeEach
    void setUp() {
        // Create mocks
        permissionService = mock(PermissionService.class);
        requirementValidator = mock(RequirementValidator.class);
        messageService = mock(MessageService.class);
        ranksConfig = mock(FileConfiguration.class);
        logger = mock(Logger.class);
        player = mock(Player.class);
        requirement1 = mock(Requirement.class);
        requirement2 = mock(Requirement.class);
        broadcastRankups = true;

        // Setup player mock
        when(player.getName()).thenReturn("TestPlayer");

        rankProgressionService = new DefaultRankProgressionService(
                permissionService,
                requirementValidator,
                messageService,
                ranksConfig,
                logger,
                broadcastRankups
        );
    }

    @Test
    void testConstructor() {
        // Verify the constructor initializes the service correctly
        assertNotNull(rankProgressionService);
    }

    @Test
    void testUpgradeRank_Success() {
        // Setup
        String currentRank = "member";
        String targetRank = "vip";

        // Mock canUpgradeToRank to return true
        DefaultRankProgressionService spy = spy(rankProgressionService);
        doReturn(true).when(spy).canUpgradeToRank(player, targetRank);

        // Mock permissionService methods
        when(permissionService.getPrimaryGroup(player)).thenReturn(currentRank);
        when(permissionService.removeFromGroup(player, currentRank)).thenReturn(true);
        when(permissionService.addToGroup(player, targetRank)).thenReturn(true);

        // Test
        boolean result = spy.upgradeRank(player, targetRank);

        // Verify
        assertTrue(result);
        verify(permissionService).getPrimaryGroup(player);
        verify(permissionService).removeFromGroup(player, currentRank);
        verify(permissionService).addToGroup(player, targetRank);
        verify(logger).info(contains("Player TestPlayer upgraded from member to vip"));
    }

    @Test
    void testUpgradeRank_CannotUpgrade() {
        // Setup
        String targetRank = "vip";

        // Mock canUpgradeToRank to return false
        DefaultRankProgressionService spy = spy(rankProgressionService);
        doReturn(false).when(spy).canUpgradeToRank(player, targetRank);

        // Test
        boolean result = spy.upgradeRank(player, targetRank);

        // Verify
        assertFalse(result);
        verify(spy).canUpgradeToRank(player, targetRank);
        verify(permissionService, never()).removeFromGroup(any(), any());
        verify(permissionService, never()).addToGroup(any(), any());
    }

    @Test
    void testUpgradeRank_AddToGroupFails() {
        // Setup
        String currentRank = "member";
        String targetRank = "vip";

        // Mock canUpgradeToRank to return true
        DefaultRankProgressionService spy = spy(rankProgressionService);
        doReturn(true).when(spy).canUpgradeToRank(player, targetRank);

        // Mock permissionService methods
        when(permissionService.getPrimaryGroup(player)).thenReturn(currentRank);
        when(permissionService.removeFromGroup(player, currentRank)).thenReturn(true);
        when(permissionService.addToGroup(player, targetRank)).thenReturn(false);

        // Test
        boolean result = spy.upgradeRank(player, targetRank);

        // Verify
        assertFalse(result);
        verify(permissionService).getPrimaryGroup(player);
        verify(permissionService).removeFromGroup(player, currentRank);
        verify(permissionService).addToGroup(player, targetRank);
        verify(permissionService).addToGroup(player, currentRank); // Should revert
        verify(logger).warning(contains("Failed to upgrade player TestPlayer to rank vip"));
    }

    @Test
    void testUpgradeRank_ExceptionHandling() {
        // Setup
        String currentRank = "member";
        String targetRank = "vip";

        // Mock canUpgradeToRank to return true
        DefaultRankProgressionService spy = spy(rankProgressionService);
        doReturn(true).when(spy).canUpgradeToRank(player, targetRank);

        // Mock permissionService methods to throw exception
        when(permissionService.getPrimaryGroup(player)).thenReturn(currentRank);
        when(permissionService.removeFromGroup(player, currentRank)).thenThrow(new RuntimeException("Test exception"));

        // Test
        boolean result = spy.upgradeRank(player, targetRank);

        // Verify
        assertFalse(result);
        verify(permissionService).getPrimaryGroup(player);
        verify(permissionService).removeFromGroup(player, currentRank);
        verify(logger).severe(contains("Error upgrading player TestPlayer to rank vip"));
    }

    @Test
    void testGetAvailableRanks_Success() {
        // Setup
        String currentRank = "member";
        Map<String, String> expectedRanks = Map.of("vip", "VIP", "premium", "Premium");

        // Mock permissionService
        when(permissionService.getPrimaryGroup(player)).thenReturn(currentRank);

        // Mock configuration sections
        ConfigurationSection ranksSection = mock(ConfigurationSection.class);
        ConfigurationSection currentRankSection = mock(ConfigurationSection.class);
        ConfigurationSection nextRanksSection = mock(ConfigurationSection.class);

        when(ranksConfig.getConfigurationSection("ranks")).thenReturn(ranksSection);
        when(ranksSection.getConfigurationSection(currentRank)).thenReturn(currentRankSection);
        when(currentRankSection.getConfigurationSection("next-ranks")).thenReturn(nextRanksSection);

        when(nextRanksSection.getKeys(false)).thenReturn(expectedRanks.keySet());
        for (Map.Entry<String, String> entry : expectedRanks.entrySet()) {
            when(nextRanksSection.getString(entry.getKey(), entry.getKey())).thenReturn(entry.getValue());
        }

        // Test
        Map<String, String> result = rankProgressionService.getAvailableRanks(player);

        // Verify
        assertEquals(expectedRanks, result);
        verify(permissionService).getPrimaryGroup(player);
        verify(ranksConfig).getConfigurationSection("ranks");
        verify(ranksSection).getConfigurationSection(currentRank);
        verify(currentRankSection).getConfigurationSection("next-ranks");
        verify(nextRanksSection).getKeys(false);
    }

    @Test
    void testGetAvailableRanks_NoRanksSection() {
        // Setup
        String currentRank = "member";

        // Mock permissionService
        when(permissionService.getPrimaryGroup(player)).thenReturn(currentRank);

        // Mock configuration sections
        when(ranksConfig.getConfigurationSection("ranks")).thenReturn(null);

        // Test
        Map<String, String> result = rankProgressionService.getAvailableRanks(player);

        // Verify
        assertTrue(result.isEmpty());
        verify(permissionService).getPrimaryGroup(player);
        verify(ranksConfig).getConfigurationSection("ranks");
    }

    @Test
    void testGetAvailableRanks_NoCurrentRankSection() {
        // Setup
        String currentRank = "member";

        // Mock permissionService
        when(permissionService.getPrimaryGroup(player)).thenReturn(currentRank);

        // Mock configuration sections
        ConfigurationSection ranksSection = mock(ConfigurationSection.class);

        when(ranksConfig.getConfigurationSection("ranks")).thenReturn(ranksSection);
        when(ranksSection.getConfigurationSection(currentRank)).thenReturn(null);

        // Test
        Map<String, String> result = rankProgressionService.getAvailableRanks(player);

        // Verify
        assertTrue(result.isEmpty());
        verify(permissionService).getPrimaryGroup(player);
        verify(ranksConfig).getConfigurationSection("ranks");
        verify(ranksSection).getConfigurationSection(currentRank);
    }

    @Test
    void testGetAvailableRanks_NoNextRanksSection() {
        // Setup
        String currentRank = "member";

        // Mock permissionService
        when(permissionService.getPrimaryGroup(player)).thenReturn(currentRank);

        // Mock configuration sections
        ConfigurationSection ranksSection = mock(ConfigurationSection.class);
        ConfigurationSection currentRankSection = mock(ConfigurationSection.class);

        when(ranksConfig.getConfigurationSection("ranks")).thenReturn(ranksSection);
        when(ranksSection.getConfigurationSection(currentRank)).thenReturn(currentRankSection);
        when(currentRankSection.getConfigurationSection("next-ranks")).thenReturn(null);

        // Test
        Map<String, String> result = rankProgressionService.getAvailableRanks(player);

        // Verify
        assertTrue(result.isEmpty());
        verify(permissionService).getPrimaryGroup(player);
        verify(ranksConfig).getConfigurationSection("ranks");
        verify(ranksSection).getConfigurationSection(currentRank);
        verify(currentRankSection).getConfigurationSection("next-ranks");
    }

    @Test
    void testCanUpgradeToRank_Success() {
        // Setup
        String targetRank = "vip";
        Map<String, String> availableRanks = Map.of("vip", "VIP");

        // Mock methods
        DefaultRankProgressionService spy = spy(rankProgressionService);
        doReturn(availableRanks).when(spy).getAvailableRanks(player);

        when(permissionService.groupExists(targetRank)).thenReturn(true);
        when(requirementValidator.meetsAllRequirements(any(Player.class), anyList())).thenReturn(true);

        // Test
        boolean result = spy.canUpgradeToRank(player, targetRank);

        // Verify
        assertTrue(result);
        verify(permissionService).groupExists(targetRank);
        verify(spy).getAvailableRanks(player);
        verify(requirementValidator).meetsAllRequirements(eq(player), anyList());
    }

    @Test
    void testCanUpgradeToRank_GroupDoesNotExist() {
        // Setup
        String targetRank = "vip";

        when(permissionService.groupExists(targetRank)).thenReturn(false);

        // Test
        boolean result = rankProgressionService.canUpgradeToRank(player, targetRank);

        // Verify
        assertFalse(result);
        verify(permissionService).groupExists(targetRank);
        verify(logger).warning(contains("Rank vip does not exist"));
    }

    @Test
    void testCanUpgradeToRank_RankNotAvailable() {
        // Setup
        String targetRank = "vip";
        Map<String, String> availableRanks = Map.of("premium", "Premium");

        // Mock methods
        DefaultRankProgressionService spy = spy(rankProgressionService);
        doReturn(availableRanks).when(spy).getAvailableRanks(player);

        when(permissionService.groupExists(targetRank)).thenReturn(true);

        // Test
        boolean result = spy.canUpgradeToRank(player, targetRank);

        // Verify
        assertFalse(result);
        verify(permissionService).groupExists(targetRank);
        verify(spy).getAvailableRanks(player);
        verify(logger).warning(contains("Rank vip is not available for player TestPlayer"));
    }

    @Test
    void testCanUpgradeToRank_DoesNotMeetRequirements() {
        // Setup
        String targetRank = "vip";
        Map<String, String> availableRanks = Map.of("vip", "VIP");

        // Mock methods
        DefaultRankProgressionService spy = spy(rankProgressionService);
        doReturn(availableRanks).when(spy).getAvailableRanks(player);

        when(permissionService.groupExists(targetRank)).thenReturn(true);
        when(requirementValidator.meetsAllRequirements(any(Player.class), anyList())).thenReturn(false);

        // Test
        boolean result = spy.canUpgradeToRank(player, targetRank);

        // Verify
        assertFalse(result);
        verify(permissionService).groupExists(targetRank);
        verify(spy).getAvailableRanks(player);
        verify(requirementValidator).meetsAllRequirements(eq(player), anyList());
    }

    @Test
    void testGetUpgradeCost_Success() {
        // Setup
        String targetRank = "vip";
        double expectedCost = 1000.0;

        // Mock configuration sections
        ConfigurationSection ranksSection = mock(ConfigurationSection.class);
        ConfigurationSection rankSection = mock(ConfigurationSection.class);

        when(ranksConfig.getConfigurationSection("ranks")).thenReturn(ranksSection);
        when(ranksSection.getConfigurationSection(targetRank)).thenReturn(rankSection);
        when(rankSection.getDouble("cost", 0)).thenReturn(expectedCost);

        // Test
        double result = rankProgressionService.getUpgradeCost(targetRank);

        // Verify
        assertEquals(expectedCost, result);
        verify(ranksConfig).getConfigurationSection("ranks");
        verify(ranksSection).getConfigurationSection(targetRank);
        verify(rankSection).getDouble("cost", 0);
    }

    @Test
    void testGetUpgradeCost_NoRanksSection() {
        // Setup
        String targetRank = "vip";

        // Mock configuration sections
        when(ranksConfig.getConfigurationSection("ranks")).thenReturn(null);

        // Test
        double result = rankProgressionService.getUpgradeCost(targetRank);

        // Verify
        assertEquals(0, result);
        verify(ranksConfig).getConfigurationSection("ranks");
    }

    @Test
    void testGetUpgradeCost_NoRankSection() {
        // Setup
        String targetRank = "vip";

        // Mock configuration sections
        ConfigurationSection ranksSection = mock(ConfigurationSection.class);

        when(ranksConfig.getConfigurationSection("ranks")).thenReturn(ranksSection);
        when(ranksSection.getConfigurationSection(targetRank)).thenReturn(null);

        // Test
        double result = rankProgressionService.getUpgradeCost(targetRank);

        // Verify
        assertEquals(0, result);
        verify(ranksConfig).getConfigurationSection("ranks");
        verify(ranksSection).getConfigurationSection(targetRank);
    }

    @Test
    void testBroadcastRankUpgrade_Enabled() {
        // Setup
        String currentRank = "member";
        String targetRank = "vip";
        Map<String, Object> expectedContext = new HashMap<>();
        expectedContext.put("playerName", "TestPlayer");
        expectedContext.put("currentRank", currentRank);
        expectedContext.put("targetRank", targetRank);

        // Test
        rankProgressionService.broadcastRankUpgrade(player, currentRank, targetRank);

        // Verify
        verify(messageService).broadcastMessage(Messages.RANKUP_BROADCAST, expectedContext);
    }

    @Test
    void testBroadcastRankUpgrade_Disabled() {
        // Setup
        String currentRank = "member";
        String targetRank = "vip";

        // Create service with broadcasting disabled
        rankProgressionService = new DefaultRankProgressionService(
                permissionService,
                requirementValidator,
                messageService,
                ranksConfig,
                logger,
                false
        );

        // Test
        rankProgressionService.broadcastRankUpgrade(player, currentRank, targetRank);

        // Verify
        verify(messageService, never()).broadcastMessage(any(), any());
    }
}
