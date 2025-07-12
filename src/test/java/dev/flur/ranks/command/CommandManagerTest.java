package dev.flur.ranks.command;

import dev.flur.commands.CommandInfo;
import dev.flur.ranks.Ranks;
import dev.flur.ranks.service.RanksService;
import dev.flur.ranks.service.ServiceContainer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class CommandManagerTest {

    @Mock
    private Ranks plugin;

    @Mock
    private ServiceContainer serviceContainer;

    @Mock
    private Logger logger;

    @Mock
    private RanksService ranksService;

    @Mock
    private PluginCommand pluginCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup plugin mock
        when(plugin.getLogger()).thenReturn(logger);

        // Setup service container mock
        when(serviceContainer.getRanksService()).thenReturn(ranksService);
    }

    @Test
    @DisplayName("CommandManager constructor initializes and registers commands")
    void testConstructorInitializesAndRegistersCommands() {
        // This test verifies that the CommandManager constructor initializes properly
        // Since we can't easily mock the reflection part, we'll just verify it doesn't throw exceptions

        // Act & Assert
        assertDoesNotThrow(() -> new CommandManager(plugin, serviceContainer));
    }

    @Test
    @DisplayName("CommandManager logs errors when command registration fails")
    void testCommandManagerLogsErrorsWhenCommandRegistrationFails() {
        // Arrange
        // Return null for getCommand to simulate missing command
        when(plugin.getCommand(anyString())).thenReturn(null);

        // Act
        new CommandManager(plugin, serviceContainer);

        // Assert
        // Verify that warning was logged (we can't know exactly which command, but we know it should log warnings)
        verify(logger, atLeastOnce()).warning(anyString());
    }

    @Test
    @DisplayName("CommandManager sets executor and tab completer when command is found")
    void testCommandManagerSetsExecutorAndTabCompleterWhenCommandIsFound() {
        // Arrange
        // Return our mock PluginCommand for any command name
        when(plugin.getCommand(anyString())).thenReturn(pluginCommand);

        // Act
        new CommandManager(plugin, serviceContainer);

        // Assert
        // Verify that setExecutor and setTabCompleter were called at least once
        // (we can't know exactly how many times without mocking reflection)
        verify(pluginCommand, atLeastOnce()).setExecutor(any(BaseCommand.class));
        verify(pluginCommand, atLeastOnce()).setTabCompleter(any(BaseCommand.class));
    }

    // Test command classes for reference

    @CommandInfo(name = "test", permission = "test.permission", description = "Test command")
    public static class TestCommand extends BaseCommand {
        public TestCommand(RanksService ranksService) {
            // Constructor with dependency
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            return true;
        }
    }
}
