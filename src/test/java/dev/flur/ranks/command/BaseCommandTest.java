package dev.flur.ranks.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BaseCommand class.
 *
 * Note: BaseCommand is a minimal abstract base class that provides:
 * 1. An abstract onCommand() method (no implementation to test)
 * 2. A default onTabComplete() implementation that returns an empty list
 *
 * Since onCommand() is abstract with no base class logic, there's nothing to test
 * for it here. Concrete command implementations should be tested in their own test classes.
 */
class BaseCommandTest {

    @Mock
    private CommandSender commandSender;

    @Mock
    private Command command;

    private BaseCommand testCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Create a concrete implementation of the abstract BaseCommand for testing
        testCommand = new BaseCommand() {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                return true; // Minimal implementation required for instantiation
            }
        };
    }

    @Test
    @DisplayName("Default onTabComplete returns empty list")
    void testDefaultTabComplete() {
        // Arrange
        String[] args = new String[0];

        // Act
        List<String> result = testCommand.onTabComplete(commandSender, command, "label", args);

        // Assert
        assertNotNull(result, "Tab complete result should not be null");
        assertTrue(result.isEmpty(), "Default tab complete should return empty list");
        assertEquals(List.of(), result, "Default tab complete should return List.of()");
    }
}