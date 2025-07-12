package dev.flur.ranks.requirement;

import dev.flur.ranks.requirement.annotations.RequirementAnnotation;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class AnnotatedRequirementTest {

    @RequirementAnnotation(
            name = "test-requirement",
            minimum = 2,
            maximum = 4,
            usage = "Format: param1 param2 [param3] amount"
    )
    private static class TestRequirement extends AnnotatedRequirement {
        public TestRequirement(String[] params) {
            super(params);
        }

        @Override
        public boolean meetsRequirement(Player player) {
            return false;
        }

        @Override
        public String toString() {
            return "TestRequirement: " + amount;
        }
    }

    @Test
    void testValidParameters() {
        // Valid parameters: exactly minimum required (2)
        String[] params = {"param1", "10.5"};
        TestRequirement requirement = new TestRequirement(params);
        assertEquals(10.5, requirement.amount);
        assertArrayEquals(params, requirement.params);
    }

    @Test
    void testValidParametersMaximum() {
        // Valid parameters: maximum allowed (4)
        String[] params = {"param1", "param2", "param3", "20.0"};
        TestRequirement requirement = new TestRequirement(params);
        assertEquals(20.0, requirement.amount);
        assertArrayEquals(params, requirement.params);
    }

    @Test
    void testTooFewParameters() {
        // Too few parameters (1 < minimum 2)
        String[] params = {"5.0"};
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TestRequirement(params)
        );
        assertTrue(exception.getMessage().contains("Too few arguments"));
    }

    @Test
    void testTooManyParameters() {
        // Too many parameters (5 > maximum 4)
        String[] params = {"param1", "param2", "param3", "param4", "30.0"};
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TestRequirement(params)
        );
        assertTrue(exception.getMessage().contains("Too many arguments"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1.0", "abc", ""})
    void testInvalidAmount(String invalidAmount) {
        // Invalid amount (must be positive)
        String[] params = {"param1", invalidAmount};
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TestRequirement(params)
        );
        assertTrue(exception.getMessage().contains("Invalid amount"));
    }

    @Test
    void testValidAmount() {
        // Valid amount (positive)
        String[] params = {"param1", "0.1"};
        TestRequirement requirement = new TestRequirement(params);
        assertEquals(0.1, requirement.amount);
    }

    @Test
    void testGetParameterDescription() {
        String[] params = {"param1", "10.0"};
        TestRequirement requirement = new TestRequirement(params);
        assertEquals("Format: param1 param2 [param3] amount", requirement.getParameterDescription());
    }

    @Test
    void testGetParameterInfoStatic() {
        RequirementAnnotation annotation = AnnotatedRequirement.getParameterInfo(TestRequirement.class);
        assertNotNull(annotation);
        assertEquals("test-requirement", annotation.name());
        assertEquals(2, annotation.minimum());
        assertEquals(4, annotation.maximum());
    }

    @Test
    void testDefaultConsume() {
        // The default consume method does nothing, so we just verify it doesn't throw an exception
        String[] params = {"param1", "10.0"};
        TestRequirement requirement = new TestRequirement(params);
        Player mockPlayer = Mockito.mock(Player.class);
        assertDoesNotThrow(() -> requirement.consume(mockPlayer));
    }
}
