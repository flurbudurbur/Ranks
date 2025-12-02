package dev.flur.ranks.service.services;

import dev.flur.ranks.requirement.Requirement;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultRequirementValidatorTest {

    private Logger logger;
    private DefaultRequirementRegistry registry;
    private Player player;
    private Requirement requirement1;
    private Requirement requirement2;
    private DefaultRequirementValidator validator;

    @BeforeEach
    void setUp() {
        // Create mocks
        logger = mock(Logger.class);
        registry = mock(DefaultRequirementRegistry.class);
        player = mock(Player.class);
        requirement1 = mock(Requirement.class);
        requirement2 = mock(Requirement.class);

        validator = new DefaultRequirementValidator(logger, registry);
    }

    @Test
    void testConstructor() {
        // Verify the constructor initializes the service correctly
        assertNotNull(validator);
    }

    @Test
    void testMeetsAllRequirements_AllMet() {
        // Setup requirements that are all met
        when(requirement1.meetsRequirement(player)).thenReturn(true);
        when(requirement2.meetsRequirement(player)).thenReturn(true);

        List<Requirement> requirements = Arrays.asList(requirement1, requirement2);

        // Test
        boolean result = validator.meetsAllRequirements(player, requirements);

        // Verify
        assertTrue(result);
        verify(requirement1).meetsRequirement(player);
        verify(requirement2).meetsRequirement(player);
    }

    @Test
    void testMeetsAllRequirements_SomeNotMet() {
        // Setup requirements where one is not met
        when(requirement1.meetsRequirement(player)).thenReturn(true);
        when(requirement2.meetsRequirement(player)).thenReturn(false);

        List<Requirement> requirements = Arrays.asList(requirement1, requirement2);

        // Test
        boolean result = validator.meetsAllRequirements(player, requirements);

        // Verify
        assertFalse(result);
        verify(requirement1).meetsRequirement(player);
        verify(requirement2).meetsRequirement(player);
    }

    @Test
    void testMeetsAllRequirements_EmptyList() {
        // Test with empty requirements list
        List<Requirement> requirements = List.of();

        // Test
        boolean result = validator.meetsAllRequirements(player, requirements);

        // Verify
        assertTrue(result);
    }

    @Test
    void testMeetsAllRequirements_ExceptionHandling() {
        // Setup requirement that throws exception
        when(requirement1.meetsRequirement(player)).thenThrow(new RuntimeException("Test exception"));

        List<Requirement> requirements = List.of(requirement1);

        // Test
        boolean result = validator.meetsAllRequirements(player, requirements);

        // Verify
        assertFalse(result);
        verify(requirement1).meetsRequirement(player);
        verify(logger).severe(contains("Error checking requirement"));
    }

    @Test
    void testGetUnmetRequirements_AllMet() {
        // Setup requirements that are all met
        when(requirement1.meetsRequirement(player)).thenReturn(true);
        when(requirement2.meetsRequirement(player)).thenReturn(true);

        List<Requirement> requirements = Arrays.asList(requirement1, requirement2);

        // Test
        List<Requirement> result = validator.getUnmetRequirements(player, requirements);

        // Verify
        assertTrue(result.isEmpty());
        verify(requirement1).meetsRequirement(player);
        verify(requirement2).meetsRequirement(player);
    }

    @Test
    void testGetUnmetRequirements_SomeNotMet() {
        // Setup requirements where one is not met
        when(requirement1.meetsRequirement(player)).thenReturn(true);
        when(requirement2.meetsRequirement(player)).thenReturn(false);

        List<Requirement> requirements = Arrays.asList(requirement1, requirement2);

        // Test
        List<Requirement> result = validator.getUnmetRequirements(player, requirements);

        // Verify
        assertEquals(1, result.size());
        assertTrue(result.contains(requirement2));
        verify(requirement1).meetsRequirement(player);
        verify(requirement2).meetsRequirement(player);
    }

    @Test
    void testGetUnmetRequirements_EmptyList() {
        // Test with empty requirements list
        List<Requirement> requirements = List.of();

        // Test
        List<Requirement> result = validator.getUnmetRequirements(player, requirements);

        // Verify
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUnmetRequirements_ExceptionHandling() {
        // Setup requirement that throws exception
        when(requirement1.meetsRequirement(player)).thenThrow(new RuntimeException("Test exception"));

        List<Requirement> requirements = List.of(requirement1);

        // Test
        List<Requirement> result = validator.getUnmetRequirements(player, requirements);

        // Verify
        assertEquals(1, result.size());
        assertTrue(result.contains(requirement1));
        verify(requirement1).meetsRequirement(player);
        verify(logger).severe(contains("Error checking requirement"));
    }

    @Test
    void testGetRequirementStatus_AllMet() {
        // Setup requirements that are all met
        when(requirement1.meetsRequirement(player)).thenReturn(true);
        when(requirement2.meetsRequirement(player)).thenReturn(true);

        List<Requirement> requirements = Arrays.asList(requirement1, requirement2);

        // Test
        Map<Requirement, Boolean> result = validator.getRequirementStatus(player, requirements);

        // Verify
        assertEquals(2, result.size());
        assertTrue(result.get(requirement1));
        assertTrue(result.get(requirement2));
        verify(requirement1).meetsRequirement(player);
        verify(requirement2).meetsRequirement(player);
    }

    @Test
    void testGetRequirementStatus_SomeNotMet() {
        // Setup requirements where one is not met
        when(requirement1.meetsRequirement(player)).thenReturn(true);
        when(requirement2.meetsRequirement(player)).thenReturn(false);

        List<Requirement> requirements = Arrays.asList(requirement1, requirement2);

        // Test
        Map<Requirement, Boolean> result = validator.getRequirementStatus(player, requirements);

        // Verify
        assertEquals(2, result.size());
        assertTrue(result.get(requirement1));
        assertFalse(result.get(requirement2));
        verify(requirement1).meetsRequirement(player);
        verify(requirement2).meetsRequirement(player);
    }

    @Test
    void testGetRequirementStatus_EmptyList() {
        // Test with empty requirements list
        List<Requirement> requirements = List.of();

        // Test
        Map<Requirement, Boolean> result = validator.getRequirementStatus(player, requirements);

        // Verify
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetRequirementStatus_ExceptionHandling() {
        // Setup requirement that throws exception
        when(requirement1.meetsRequirement(player)).thenThrow(new RuntimeException("Test exception"));

        List<Requirement> requirements = List.of(requirement1);

        // Test
        Map<Requirement, Boolean> result = validator.getRequirementStatus(player, requirements);

        // Verify
        assertEquals(1, result.size());
        assertFalse(result.get(requirement1));
        verify(requirement1).meetsRequirement(player);
        verify(logger).severe(contains("Error checking requirement status"));
    }

    @Test
    void testGetRequirementDescription() {
        // Setup - use the requirement's toString() method
        when(requirement1.toString()).thenReturn("Test Requirement Description");

        // Test
        String result = validator.getRequirementDescription(requirement1);

        // Verify
        assertEquals("Test Requirement Description", result);
        verify(requirement1).toString();
    }
}
