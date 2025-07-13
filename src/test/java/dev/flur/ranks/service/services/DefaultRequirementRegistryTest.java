package dev.flur.ranks.service.services;

import dev.flur.ranks.requirement.AnnotatedRequirement;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.requirement.annotations.RequirementAnnotation;
import dev.flur.ranks.requirement.records.RequirementRecord;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultRequirementRegistryTest {

    private DefaultRequirementRegistry registry;
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

        // Getter for the protected amount field for testing
        public double getAmount() {
            return amount;
        }
    }

    @RequirementAnnotation(
            name = "another-requirement",
            minimum = 2,
            usage = "Format: param1 amount"
    )
    public static class AnotherRequirement extends AnnotatedRequirement {
        public AnotherRequirement(String[] params) {
            super(params);
        }

        @Override
        public boolean meetsRequirement(@NotNull Player player) {
            return false;
        }
    }

    // This class doesn't extend AnnotatedRequirement
    private static class NonAnnotatedRequirement implements Requirement {
        @Override
        public boolean meetsRequirement(@NotNull Player player) {
            return false;
        }

        @Override
        public void consume(@NotNull Player player) {
            // Do nothing
        }
    }

    @BeforeEach
    void setUp() {
        mockLogger = mock(Logger.class);
        registry = new DefaultRequirementRegistry(mockLogger);
    }

    @Test
    void testRegisterRequirement() {
        // Act
        registry.registerRequirement(TestRequirement.class);

        // Assert
        assertTrue(registry.hasRequirement("test-requirement"));
        assertEquals(TestRequirement.class, registry.getRequirementClass("test-requirement"));

        // Verify logging
        verify(mockLogger).info(contains("Registered requirement: test-requirement"));
    }

    @Test
    void testRegisterMultipleRequirements() {
        // Act
        registry.registerRequirement(TestRequirement.class);
        registry.registerRequirement(AnotherRequirement.class);

        // Assert
        assertTrue(registry.hasRequirement("test-requirement"));
        assertTrue(registry.hasRequirement("another-requirement"));
        assertEquals(2, registry.getRegisteredRequirementNames().size());
        assertEquals(2, registry.getRegisteredRequirementClasses().size());
    }

    @Test
    void testRegisterNonAnnotatedRequirement() {
        // Act
        registry.registerRequirement(NonAnnotatedRequirement.class);

        // Assert
        assertFalse(registry.hasRequirement("non-annotated-requirement"));

        // Verify warning was logged
        verify(mockLogger).warning(contains("does not extend AnnotatedRequirement"));
    }

    @Test
    void testGetMinMaxParams() {
        // Arrange
        registry.registerRequirement(TestRequirement.class);
        registry.registerRequirement(AnotherRequirement.class);

        // Act & Assert
        assertEquals(1, registry.getMinParams("test-requirement"));
        assertEquals(3, registry.getMaxParams("test-requirement"));
        assertEquals(2, registry.getMinParams("another-requirement"));
        assertEquals(Integer.MAX_VALUE, registry.getMaxParams("another-requirement"));

        // Non-existent requirement
        assertEquals(-1, registry.getMinParams("non-existent"));
        assertEquals(-1, registry.getMaxParams("non-existent"));
    }

    @Test
    void testGetParamNames() {
        // Arrange
        registry.registerRequirement(TestRequirement.class);

        // Act
        List<String> paramNames = registry.getParamNames("test-requirement");

        // Assert
        assertEquals(1, paramNames.size());
        assertEquals("test-requirement", paramNames.getFirst());

        // Non-existent requirement
        assertTrue(registry.getParamNames("non-existent").isEmpty());
    }

    @Test
    void testCreateRequirementFromMap() {
        // Arrange
        registry.registerRequirement(TestRequirement.class);
        Map<String, String> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("amount", "100.5");

        // Act
        Requirement requirement = registry.createRequirement("test-requirement", params);

        // Assert
        assertNotNull(requirement);
        assertTrue(requirement instanceof TestRequirement);
        assertEquals(100.5, ((TestRequirement) requirement).getAmount());
    }

    @Test
    void testCreateRequirementFromString() {
        // Arrange
        registry.registerRequirement(TestRequirement.class);

        // Act
        Requirement requirement = registry.createRequirement("test-requirement", "param1,100.5");

        // Assert
        assertNotNull(requirement);
        assertTrue(requirement instanceof TestRequirement);
        assertEquals(100.5, ((TestRequirement) requirement).getAmount());
    }

    @Test
    void testCreateRequirementInvalidType() {
        // Act
        Requirement requirement = registry.createRequirement("non-existent", "param1,100");

        // Assert
        assertNull(requirement);
    }

    @Test
    void testFromNameAndFromClass() {
        // Arrange
        registry.registerRequirement(TestRequirement.class);

        // Act & Assert
        RequirementRecord record = registry.fromName("test-requirement");
        assertNotNull(record);
        assertEquals("test-requirement", record.name());
        assertEquals(TestRequirement.class, record.requirementClass());

        RequirementRecord recordFromClass = registry.fromClass(TestRequirement.class);
        assertNotNull(recordFromClass);
        assertEquals("test-requirement", recordFromClass.name());
        assertEquals(TestRequirement.class, recordFromClass.requirementClass());

        // Non-existent
        assertNull(registry.fromName("non-existent"));
        assertNull(registry.fromClass(NonAnnotatedRequirement.class));
    }

    @Test
    void testGetRegisteredNamesAndClasses() {
        // Arrange
        registry.registerRequirement(TestRequirement.class);
        registry.registerRequirement(AnotherRequirement.class);

        // Act
        Set<String> names = registry.getRegisteredNames();
        Set<Class<? extends Requirement>> classes = registry.getRegisteredClasses();

        // Assert
        assertEquals(2, names.size());
        assertTrue(names.contains("test-requirement"));
        assertTrue(names.contains("another-requirement"));

        assertEquals(2, classes.size());
        assertTrue(classes.contains(TestRequirement.class));
        assertTrue(classes.contains(AnotherRequirement.class));
    }

    @Test
    void testGetAllRequirements() {
        // Arrange
        registry.registerRequirement(TestRequirement.class);
        registry.registerRequirement(AnotherRequirement.class);

        // Act
        var requirements = registry.getAllRequirements();

        // Assert
        assertEquals(2, requirements.size());
    }
}
