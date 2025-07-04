package dev.flur.ranks.command;

import dev.flur.ranks.Ranks;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.logging.Logger;

import static org.mockito.Mockito.*;

@DisplayName("Annotation Command Manager Tests")
public class AnnotationCommandManagerTest {

    @Mock
    private JavaPlugin mockPlugin;

    @Mock
    private PluginCommand mockCommand;

    @Mock
    private Logger mockLogger;

    private MockedStatic<Ranks> mockedStaticRanks;

    @BeforeEach
    public void setUp() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this);

        // Set up the mock plugin
        when(mockPlugin.getLogger()).thenReturn(mockLogger);
        when(mockPlugin.getCommand(anyString())).thenReturn(mockCommand);
    }

    @Test
    @DisplayName("Should register commands on initialization")
    public void shouldRegisterCommandsOnInitialization() {
        // Create the command manager
        new AnnotationCommandManager(mockPlugin);

        // Verify that getCommand was called at least once
        verify(mockPlugin, atLeastOnce()).getCommand(anyString());
        
        // Verify that setExecutor and setTabCompleter were called at least once
        verify(mockCommand, atLeastOnce()).setExecutor(any(BaseCommand.class));
        verify(mockCommand, atLeastOnce()).setTabCompleter(any(BaseCommand.class));
    }

    @Test
    @DisplayName("Should handle exceptions when registering commands")
    public void shouldHandleExceptionsWhenRegisteringCommands() {
        // Set up the mock to throw an exception
        when(mockPlugin.getCommand(anyString())).thenThrow(new RuntimeException("Test exception"));

        // Create the command manager
        new AnnotationCommandManager(mockPlugin);

        // Verify that the logger was called to log the warning
        verify(mockLogger, atLeastOnce()).warning(contains("Failed to register command"));
    }
}