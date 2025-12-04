package dev.flur.ranks.requirement;

import dev.flur.ranks.service.services.DefaultRequirementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequirementFactoryTest {

    private DefaultRequirementService requirementService;
    private RequirementFactory factory;
    private Logger mockLogger;

    @BeforeEach
    void setUp() {
        mockLogger = mock(Logger.class);
        requirementService = new DefaultRequirementService(mockLogger);
        factory = new RequirementFactory(requirementService);
    }

    @Test
    void testCreateRequirement_BuiltInMoney() {
        // Act
        Requirement requirement = factory.createRequirement("money 100.5");

        // Assert
        assertNotNull(requirement);
        assertEquals("money: 100.5", requirement.toString());
    }

    @Test
    void testCreateRequirement_BuiltInXpLevel() {
        // Act
        Requirement requirement = factory.createRequirement("xp-level 30");

        // Assert
        assertNotNull(requirement);
        assertEquals("xp-level: 30", requirement.toString());
    }

    @Test
    void testCreateRequirement_BuiltInDeaths() {
        // Act
        Requirement requirement = factory.createRequirement("deaths 5");

        // Assert
        assertNotNull(requirement);
        assertEquals("deaths: 5", requirement.toString());
    }

    @Test
    void testCreateRequirement_EmptyInput() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.createRequirement("")
        );
        assertTrue(exception.getMessage().contains("Invalid requirement input"));
    }

    @Test
    void testCreateRequirement_WhitespaceInput() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.createRequirement("   ")
        );
        assertTrue(exception.getMessage().contains("Invalid requirement input"));
    }

    @Test
    void testCreateRequirement_UnknownRequirement() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.createRequirement("unknown-requirement 100")
        );
        assertTrue(exception.getMessage().contains("Unknown requirement type"));
    }

    @Test
    void testCreateRequirement_CustomRequirement() {
        // Arrange - register a custom requirement
        requirementService.register("custom-test", 1, 2, "Format: amount",
                params -> new BaseRequirement(params) {
                    @Override
                    public boolean meetsRequirement(org.bukkit.entity.Player player) {
                        return false;
                    }

                    @Override
                    public String toString() {
                        return "custom: " + amount;
                    }
                });

        // Act
        Requirement requirement = factory.createRequirement("custom-test 42");

        // Assert
        assertNotNull(requirement);
        assertEquals("custom: 42.0", requirement.toString());
    }

    @Test
    void testCreateRequirement_TooFewParams() {
        // Act & Assert - block-break requires at least 2 params
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.createRequirement("block-break 100")
        );
        assertTrue(exception.getMessage().contains("Too few"));
    }
}
