package dev.flur.ranks.service.services;

import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.service.PermissionService;
import dev.flur.ranks.service.RequirementValidator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultPlayerRankServiceTest {

    private PermissionService permissionService;
    private RequirementValidator requirementValidator;
    private FileConfiguration ranksConfig;
    private Logger logger;
    private Player player;
    private Requirement requirement1;
    private Requirement requirement2;
    private DefaultPlayerRankService playerRankService;

    @BeforeEach
    void setUp() {
        // Create mocks
        permissionService = mock(PermissionService.class);
        requirementValidator = mock(RequirementValidator.class);
        ranksConfig = mock(FileConfiguration.class);
        logger = mock(Logger.class);
        player = mock(Player.class);
        requirement1 = mock(Requirement.class);
        requirement2 = mock(Requirement.class);
        
        // Setup player mock
        when(player.getName()).thenReturn("TestPlayer");
        
        playerRankService = new DefaultPlayerRankService(
                permissionService,
                requirementValidator,
                ranksConfig,
                logger
        );
    }

    @Test
    void testConstructor() {
        // Verify the constructor initializes the service correctly
        assertNotNull(playerRankService);
    }

    @Test
    void testGetCurrentRank_Success() {
        // Setup
        String expectedRank = "member";
        when(permissionService.getPrimaryGroup(player)).thenReturn(expectedRank);
        
        // Test
        String result = playerRankService.getCurrentRank(player);
        
        // Verify
        assertEquals(expectedRank, result);
        verify(permissionService).getPrimaryGroup(player);
    }

    @Test
    void testGetCurrentRank_NullResult() {
        // Setup
        when(permissionService.getPrimaryGroup(player)).thenReturn(null);
        
        // Test
        String result = playerRankService.getCurrentRank(player);
        
        // Verify
        assertEquals("", result);
        verify(permissionService).getPrimaryGroup(player);
    }

    @Test
    void testGetCurrentRank_ExceptionHandling() {
        // Setup
        when(permissionService.getPrimaryGroup(player)).thenThrow(new RuntimeException("Test exception"));
        
        // Test
        String result = playerRankService.getCurrentRank(player);
        
        // Verify
        assertEquals("", result);
        verify(permissionService).getPrimaryGroup(player);
        verify(logger).severe(contains("Failed to get current rank for player"));
    }

    @Test
    void testGetRequirementsForRank() {
        // The implementation returns an empty list, so we'll just verify it's empty
        List<Requirement> result = playerRankService.getRequirementsForRank(player, "vip");
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUnmetRequirements() {
        // Setup
        List<Requirement> requirements = List.of(requirement1, requirement2);
        List<Requirement> unmetRequirements = List.of(requirement2);
        
        // Create a spy to control getRequirementsForRank
        DefaultPlayerRankService spy = spy(playerRankService);
        doReturn(requirements).when(spy).getRequirementsForRank(player, "vip");
        
        when(requirementValidator.getUnmetRequirements(player, requirements)).thenReturn(unmetRequirements);
        
        // Test
        List<Requirement> result = spy.getUnmetRequirements(player, "vip");
        
        // Verify
        assertEquals(unmetRequirements, result);
        verify(requirementValidator).getUnmetRequirements(player, requirements);
    }

    @Test
    void testGetRequirementProgress_AllMet() {
        // Setup
        List<Requirement> requirements = List.of(requirement1, requirement2);
        
        // Create a spy to control getRequirementsForRank
        DefaultPlayerRankService spy = spy(playerRankService);
        doReturn(requirements).when(spy).getRequirementsForRank(player, "vip");
        
        when(requirement1.meetsRequirement(player)).thenReturn(true);
        when(requirement2.meetsRequirement(player)).thenReturn(true);
        when(requirementValidator.getRequirementDescription(requirement1)).thenReturn("Requirement 1");
        when(requirementValidator.getRequirementDescription(requirement2)).thenReturn("Requirement 2");
        
        // Test
        Map<String, Integer> result = spy.getRequirementProgress(player, "vip");
        
        // Verify
        assertEquals(2, result.size());
        assertEquals(100, result.get("Requirement 1"));
        assertEquals(100, result.get("Requirement 2"));
        verify(requirement1).meetsRequirement(player);
        verify(requirement2).meetsRequirement(player);
        verify(requirementValidator).getRequirementDescription(requirement1);
        verify(requirementValidator).getRequirementDescription(requirement2);
    }

    @Test
    void testGetRequirementProgress_SomeNotMet() {
        // Setup
        List<Requirement> requirements = List.of(requirement1, requirement2);
        
        // Create a spy to control getRequirementsForRank
        DefaultPlayerRankService spy = spy(playerRankService);
        doReturn(requirements).when(spy).getRequirementsForRank(player, "vip");
        
        when(requirement1.meetsRequirement(player)).thenReturn(true);
        when(requirement2.meetsRequirement(player)).thenReturn(false);
        when(requirementValidator.getRequirementDescription(requirement1)).thenReturn("Requirement 1");
        when(requirementValidator.getRequirementDescription(requirement2)).thenReturn("Requirement 2");
        
        // Test
        Map<String, Integer> result = spy.getRequirementProgress(player, "vip");
        
        // Verify
        assertEquals(2, result.size());
        assertEquals(100, result.get("Requirement 1"));
        assertEquals(0, result.get("Requirement 2"));
        verify(requirement1).meetsRequirement(player);
        verify(requirement2).meetsRequirement(player);
        verify(requirementValidator).getRequirementDescription(requirement1);
        verify(requirementValidator).getRequirementDescription(requirement2);
    }

    @Test
    void testGetRequirementProgress_ExceptionHandling() {
        // Setup
        List<Requirement> requirements = List.of(requirement1);
        
        // Create a spy to control getRequirementsForRank
        DefaultPlayerRankService spy = spy(playerRankService);
        doReturn(requirements).when(spy).getRequirementsForRank(player, "vip");
        
        when(requirement1.meetsRequirement(player)).thenThrow(new RuntimeException("Test exception"));
        
        // Test
        Map<String, Integer> result = spy.getRequirementProgress(player, "vip");
        
        // Verify
        assertEquals(1, result.size());
        assertEquals(0, result.get("Unknown requirement"));
        verify(requirement1).meetsRequirement(player);
        verify(logger).warning(contains("Error getting progress for requirement"));
    }

    @Test
    void testHasRankCommandPermission_True() {
        // Setup
        String command = "rankup";
        String permission = "ranks.rankup";
        when(permissionService.hasPermission(player, permission)).thenReturn(true);
        
        // Test
        boolean result = playerRankService.hasRankCommandPermission(player, command);
        
        // Verify
        assertTrue(result);
        verify(permissionService).hasPermission(player, permission);
    }

    @Test
    void testHasRankCommandPermission_False() {
        // Setup
        String command = "rankup";
        String permission = "ranks.rankup";
        when(permissionService.hasPermission(player, permission)).thenReturn(false);
        
        // Test
        boolean result = playerRankService.hasRankCommandPermission(player, command);
        
        // Verify
        assertFalse(result);
        verify(permissionService).hasPermission(player, permission);
    }
}