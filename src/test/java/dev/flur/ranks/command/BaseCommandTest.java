package dev.flur.ranks.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
                return true; // Default implementation for testing
            }
        };
    }

    @Nested
    @DisplayName("onCommand Tests")
    class OnCommandTests {
        @Test
        @DisplayName("Abstract onCommand method can be implemented")
        void testOnCommandImplementation() {
            // Arrange
            String[] args = new String[0];
            
            // Act
            boolean result = testCommand.onCommand(commandSender, command, "label", args);
            
            // Assert
            assertTrue(result, "Default implementation should return true");
        }
    }

    @Nested
    @DisplayName("onTabComplete Tests")
    class OnTabCompleteTests {
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
        
        @Test
        @DisplayName("onTabComplete can be overridden")
        void testOverriddenTabComplete() {
            // Arrange
            BaseCommand customCommand = new BaseCommand() {
                @Override
                public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                    return true;
                }
                
                @Override
                public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
                    return List.of("test", "custom");
                }
            };
            String[] args = new String[0];
            
            // Act
            List<String> result = customCommand.onTabComplete(commandSender, command, "label", args);
            
            // Assert
            assertNotNull(result, "Tab complete result should not be null");
            assertEquals(2, result.size(), "Custom tab complete should return 2 items");
            assertEquals(List.of("test", "custom"), result, "Custom tab complete should return custom list");
        }
    }
}