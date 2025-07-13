package dev.flur.ranks.requirement;

import dev.flur.ranks.requirement.annotations.RequirementAnnotation;
import dev.flur.ranks.service.services.DefaultRequirementRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequirementFactoryTest {

    private DefaultRequirementRegistry registry;
    private RequirementFactory factory;
    private Logger mockLogger;

    @RequirementAnnotation(
            name = "test-requirement",
            maximum = 3,
            usage = "Format: [param1 [param2]] amount"
    )
    public static class TestRequirement extends AnnotatedRequirement {
        public TestRequirement(String[] params) {
            super(params);
        }

        @Override
        public boolean meetsRequirement(@NotNull Player player) {
            return false;
        }

        @Override
        public String toString() {
            return "TestRequirement: " + amount;
        }
    }

    @BeforeEach
    void setUp() {
        // Create mocks
        mockLogger = mock(Logger.class);
        registry = new DefaultRequirementRegistry(mockLogger);

        // Register the test requirement
        registry.registerRequirement(TestRequirement.class);

        // Create the factory
        factory = new RequirementFactory(registry);
    }

    @Test
    void testCreateRequirement_ValidInput() {
        // Arrange
        String input = "test-requirement 100.5";

        // Act
        Requirement requirement = factory.createRequirement(input);

        // Assert
        assertNotNull(requirement);
        assertTrue(requirement instanceof TestRequirement);
        assertEquals(100.5, ((TestRequirement) requirement).amount);
    }

    @Test
    void testCreateRequirement_ValidInputWithMultipleParams() {
        // Arrange
        String input = "test-requirement param1 param2 50.0";

        // Act
        Requirement requirement = factory.createRequirement(input);

        // Assert
        assertNotNull(requirement);
        assertTrue(requirement instanceof TestRequirement);
        assertEquals(50.0, ((TestRequirement) requirement).amount);
        assertEquals(3, ((TestRequirement) requirement).params.length);
        assertEquals("param1", ((TestRequirement) requirement).params[0]);
        assertEquals("param2", ((TestRequirement) requirement).params[1]);
        assertEquals("50.0", ((TestRequirement) requirement).params[2]);
    }

    @Test
    void testCreateRequirement_EmptyInput() {
        // Arrange
        String input = "";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.createRequirement(input)
        );
        assertTrue(exception.getMessage().contains("Invalid requirement input"));
    }

    @Test
    void testCreateRequirement_UnknownRequirement() {
        // Arrange
        String input = "unknown-requirement 100";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.createRequirement(input)
        );
        assertTrue(exception.getMessage().contains("Invalid requirement type"));
    }

    @Test
    void testGetRequirementName_ValidRequirement() {
        // Arrange
        Requirement requirement = new TestRequirement(new String[]{"100"});

        // Act
        String name = factory.getRequirementName(requirement);

        // Assert
        assertEquals("test-requirement", name);
    }

    @Test
    void testGetRequirementName_UnregisteredRequirement() {
        // Arrange
        Requirement unregisteredRequirement = mock(Requirement.class);

        // Act
        String name = factory.getRequirementName(unregisteredRequirement);

        // Assert
        assertNull(name);
    }
}
