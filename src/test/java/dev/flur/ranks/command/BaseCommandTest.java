package dev.flur.ranks.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Base Command Tests")
public class BaseCommandTest {

    @Mock
    private CommandSender mockSender;

    @Mock
    private Command mockCommand;

    private BaseCommand testCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a concrete implementation of the abstract BaseCommand class for testing
        testCommand = new BaseCommand() {
            @Override
            public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
                return true;
            }
        };
    }

    @Test
    @DisplayName("Should return empty list for tab completion by default")
    public void shouldReturnEmptyListForTabCompletionByDefault() {
        // Call the onTabComplete method
        List<String> result = testCommand.onTabComplete(mockSender, mockCommand, "label", new String[0]);
        
        // Verify that the result is not null and is empty
        assertNotNull(result, "Tab completion result should not be null");
        assertTrue(result.isEmpty(), "Tab completion result should be empty by default");
    }

    @Test
    @DisplayName("Should allow overriding tab completion")
    public void shouldAllowOverridingTabCompletion() {
        // Create a command that overrides onTabComplete
        BaseCommand customCommand = new BaseCommand() {
            @Override
            public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
                return true;
            }
            
            @Override
            public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
                return List.of("test1", "test2");
            }
        };
        
        // Call the onTabComplete method
        List<String> result = customCommand.onTabComplete(mockSender, mockCommand, "label", new String[0]);
        
        // Verify that the result is not null and contains the expected values
        assertNotNull(result, "Tab completion result should not be null");
        assertEquals(2, result.size(), "Tab completion result should have 2 items");
        assertTrue(result.contains("test1"), "Tab completion result should contain 'test1'");
        assertTrue(result.contains("test2"), "Tab completion result should contain 'test2'");
    }
}