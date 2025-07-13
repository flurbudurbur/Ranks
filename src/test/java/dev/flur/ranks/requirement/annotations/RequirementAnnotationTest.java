package dev.flur.ranks.requirement.annotations;

import dev.flur.ranks.requirement.AnnotatedRequirement;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequirementAnnotationTest {

    @RequirementAnnotation(
            name = "test-requirement",
            minimum = 2,
            maximum = 5,
            usage = "Format: param1 param2 [param3] amount"
    )
    private static class TestRequirement extends AnnotatedRequirement {
        public TestRequirement(String[] params) {
            super(params);
        }

        @Override
        public boolean meetsRequirement(org.bukkit.entity.@NotNull Player player) {
            return false;
        }
    }

    @RequirementAnnotation(
            name = "minimal-requirement"
    )
    private static class MinimalRequirement extends AnnotatedRequirement {
        public MinimalRequirement(String[] params) {
            super(params);
        }

        @Override
        public boolean meetsRequirement(org.bukkit.entity.@NotNull Player player) {
            return false;
        }
    }

    @Test
    void testAnnotationValues() {
        // Arrange
        RequirementAnnotation annotation = TestRequirement.class.getAnnotation(RequirementAnnotation.class);

        // Assert
        assertNotNull(annotation);
        assertEquals("test-requirement", annotation.name());
        assertEquals(2, annotation.minimum());
        assertEquals(5, annotation.maximum());
        assertEquals("Format: param1 param2 [param3] amount", annotation.usage());
    }

    @Test
    void testDefaultValues() {
        // Arrange
        RequirementAnnotation annotation = MinimalRequirement.class.getAnnotation(RequirementAnnotation.class);

        // Assert
        assertNotNull(annotation);
        assertEquals("minimal-requirement", annotation.name());
        assertEquals(1, annotation.minimum()); // Default value
        assertEquals(Integer.MAX_VALUE, annotation.maximum()); // Default value
        assertEquals("", annotation.usage()); // Default value
    }

    @Test
    void testAnnotationRetention() {
        // This test verifies that the annotation is retained at runtime
        RequirementAnnotation annotation = TestRequirement.class.getAnnotation(RequirementAnnotation.class);
        assertNotNull(annotation, "Annotation should be available at runtime");
    }
}