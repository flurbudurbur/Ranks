package dev.flur.ranks.requirement.records;

import dev.flur.ranks.requirement.Requirement;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class RequirementRecordTest {

    // Simple test implementation of Requirement
    private static class TestRequirement implements Requirement {
        @Override
        public boolean meetsRequirement(org.bukkit.entity.Player player) {
            return false;
        }

        @Override
        public void consume(org.bukkit.entity.Player player) {
            // Do nothing
        }
    }

    private final Function<String[], Requirement> constructor = params -> new TestRequirement();
    private final Class<? extends Requirement> requirementClass = TestRequirement.class;

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String name = "test-requirement";

        // Act
        RequirementRecord record = new RequirementRecord(name, constructor, requirementClass);

        // Assert
        assertEquals(name, record.name());
        assertSame(constructor, record.constructor());
        assertSame(requirementClass, record.requirementClass());
    }

    @Test
    void testToString() {
        // Arrange
        String name = "test-requirement";
        RequirementRecord record = new RequirementRecord(name, constructor, requirementClass);

        // Act
        String result = record.toString();

        // Assert
        assertTrue(result.contains(name));
        assertTrue(result.contains("TestRequirement"));
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        String name = "test-requirement";
        RequirementRecord record1 = new RequirementRecord(name, constructor, requirementClass);
        RequirementRecord record2 = new RequirementRecord(name, constructor, requirementClass);
        RequirementRecord record3 = new RequirementRecord("other-requirement", constructor, requirementClass);

        // Act & Assert
        assertEquals(record1, record2);
        assertEquals(record1.hashCode(), record2.hashCode());
        assertNotEquals(record1, record3);
        assertNotEquals(record1.hashCode(), record3.hashCode());
    }

    @Test
    void testConstructorNullChecks() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> new RequirementRecord(null, constructor, requirementClass));
        assertThrows(IllegalArgumentException.class, () -> new RequirementRecord("test", null, requirementClass));
        assertThrows(IllegalArgumentException.class, () -> new RequirementRecord("test", constructor, null));
    }
}
