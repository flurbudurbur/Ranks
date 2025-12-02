package dev.flur.ranks.service.services;

import dev.flur.ranks.requirement.BaseRequirement;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.requirement.RequirementType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultRequirementRegistryTest {

    private DefaultRequirementRegistry registry;
    private Logger mockLogger;

    @BeforeEach
    void setUp() {
        mockLogger = mock(Logger.class);
        registry = new DefaultRequirementRegistry(mockLogger);
    }

    @Test
    void testBuiltInRequirementsAvailable() {
        // All built-in types should be available
        for (RequirementType type : RequirementType.values()) {
            assertTrue(registry.hasRequirement(type.getKey()),
                    "Built-in requirement should exist: " + type.getKey());
        }
    }

    @Test
    void testCreateBuiltInMoney() {
        // Act
        Requirement requirement = registry.create("money", new String[]{"100"});

        // Assert
        assertNotNull(requirement);
        assertEquals("money: 100.0", requirement.toString());
    }

    @Test
    void testCreateBuiltInXpLevel() {
        // Act
        Requirement requirement = registry.create("xp-level", new String[]{"30"});

        // Assert
        assertNotNull(requirement);
        assertEquals("xp-level: 30", requirement.toString());
    }

    @Test
    void testCreateBuiltInDeaths() {
        // Act
        Requirement requirement = registry.create("deaths", new String[]{"5"});

        // Assert
        assertNotNull(requirement);
        assertEquals("deaths: 5", requirement.toString());
    }

    @Test
    void testCreateUnknownRequirement() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> registry.create("unknown", new String[]{"100"}));
    }

    @Test
    void testRegisterCustomRequirement() {
        // Arrange
        registry.register("custom-req", 1, 2, "Format: amount",
                params -> new BaseRequirement(params) {
                    @Override
                    public boolean meetsRequirement(@NotNull Player player) {
                        return false;
                    }

                    @Override
                    public String toString() {
                        return "custom: " + amount;
                    }
                });

        // Act
        assertTrue(registry.hasRequirement("custom-req"));
        Requirement requirement = registry.create("custom-req", new String[]{"50"});

        // Assert
        assertNotNull(requirement);
        assertEquals("custom: 50.0", requirement.toString());
        verify(mockLogger).info(contains("Registered custom requirement: custom-req"));
    }

    @Test
    void testCannotOverrideBuiltIn() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> registry.register("money", 1, 1, "test",
                        params -> mock(Requirement.class)));
    }

    @Test
    void testGetMinMaxParams_BuiltIn() {
        // Assert
        assertEquals(1, registry.getMinParams("money"));
        assertEquals(1, registry.getMaxParams("money"));

        assertEquals(2, registry.getMinParams("block-break"));
        assertEquals(Integer.MAX_VALUE, registry.getMaxParams("block-break"));
    }

    @Test
    void testGetMinMaxParams_Custom() {
        // Arrange
        registry.register("custom", 2, 5, "test", params -> mock(Requirement.class));

        // Assert
        assertEquals(2, registry.getMinParams("custom"));
        assertEquals(5, registry.getMaxParams("custom"));
    }

    @Test
    void testGetMinMaxParams_NonExistent() {
        // Assert
        assertEquals(-1, registry.getMinParams("non-existent"));
        assertEquals(-1, registry.getMaxParams("non-existent"));
    }

    @Test
    void testGetRegisteredNames() {
        // Arrange
        registry.register("custom1", 1, 1, "test", params -> mock(Requirement.class));
        registry.register("custom2", 1, 1, "test", params -> mock(Requirement.class));

        // Act
        Set<String> names = registry.getRegisteredNames();

        // Assert
        // Should include all built-in types plus custom ones
        assertTrue(names.contains("money"));
        assertTrue(names.contains("xp-level"));
        assertTrue(names.contains("deaths"));
        assertTrue(names.contains("custom1"));
        assertTrue(names.contains("custom2"));
        assertEquals(RequirementType.values().length + 2, names.size());
    }

    @Test
    void testGetUsage_BuiltIn() {
        // Assert
        assertNotNull(registry.getUsage("money"));
        assertNotNull(registry.getUsage("block-break"));
    }

    @Test
    void testGetUsage_Custom() {
        // Arrange
        registry.register("custom", 1, 1, "Custom usage description",
                params -> mock(Requirement.class));

        // Assert
        assertEquals("Custom usage description", registry.getUsage("custom"));
    }

    @Test
    void testGetUsage_NonExistent() {
        // Assert
        assertNull(registry.getUsage("non-existent"));
    }

    @Test
    void testTooFewParams() {
        // block-break requires at least 2 params
        assertThrows(IllegalArgumentException.class,
                () -> registry.create("block-break", new String[]{"100"}));
    }

    @Test
    void testTooManyParams() {
        // money allows only 1 param
        assertThrows(IllegalArgumentException.class,
                () -> registry.create("money", new String[]{"100", "200"}));
    }
}
