package dev.flur.ranks.command.commands;

import dev.flur.ranks.service.RanksService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RanksCommandTest {

    @Mock
    private RanksService ranksService;

    @Mock
    private CommandSender sender;

    @Mock
    private Command command;

    private RanksCommand ranksCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ranksCommand = new RanksCommand(ranksService);
    }

    @Test
    @DisplayName("onCommand sends all ranks to sender")
    void testOnCommandSendsAllRanksToSender() {
        // Arrange
        List<String> ranks = Arrays.asList("Rank1", "Rank2", "Rank3");
        when(ranksService.getAllRanks()).thenReturn(ranks);
        String[] args = new String[0];

        // Act
        boolean result = ranksCommand.onCommand(sender, command, "ranks", args);

        // Assert
        assertTrue(result, "Command should return true");
        verify(ranksService).getAllRanks();
        verify(sender).sendMessage("Rank1");
        verify(sender).sendMessage("Rank2");
        verify(sender).sendMessage("Rank3");
    }

    @Test
    @DisplayName("onCommand handles empty ranks list")
    void testOnCommandHandlesEmptyRanksList() {
        // Arrange
        List<String> ranks = List.of();
        when(ranksService.getAllRanks()).thenReturn(ranks);
        String[] args = new String[0];

        // Act
        boolean result = ranksCommand.onCommand(sender, command, "ranks", args);

        // Assert
        assertTrue(result, "Command should return true");
        verify(ranksService).getAllRanks();
        verify(sender, never()).sendMessage(anyString());
    }

    @Test
    @DisplayName("onCommand works with player sender")
    void testOnCommandWorksWithPlayerSender() {
        // Arrange
        List<String> ranks = Arrays.asList("Rank1", "Rank2");
        when(ranksService.getAllRanks()).thenReturn(ranks);
        String[] args = new String[0];

        // Use a mock that is also a Player
        CommandSender playerSender = mock(CommandSender.class, withSettings().extraInterfaces(org.bukkit.entity.Player.class));

        // Act
        boolean result = ranksCommand.onCommand(playerSender, command, "ranks", args);

        // Assert
        assertTrue(result, "Command should return true");
        verify(ranksService).getAllRanks();
        verify(playerSender).sendMessage("Rank1");
        verify(playerSender).sendMessage("Rank2");
    }

    @Test
    @DisplayName("onCommand works with console sender")
    void testOnCommandWorksWithConsoleSender() {
        // Arrange
        List<String> ranks = Arrays.asList("Rank1", "Rank2");
        when(ranksService.getAllRanks()).thenReturn(ranks);
        String[] args = new String[0];

        // Use a mock that is also a ConsoleCommandSender
        CommandSender consoleSender = mock(CommandSender.class, withSettings().extraInterfaces(org.bukkit.command.ConsoleCommandSender.class));

        // Act
        boolean result = ranksCommand.onCommand(consoleSender, command, "ranks", args);

        // Assert
        assertTrue(result, "Command should return true");
        verify(ranksService).getAllRanks();
        verify(consoleSender).sendMessage("Rank1");
        verify(consoleSender).sendMessage("Rank2");
    }

    @Test
    @DisplayName("onCommand handles exception from service")
    void testOnCommandHandlesExceptionFromService() {
        // Arrange
        when(ranksService.getAllRanks()).thenThrow(new RuntimeException("Test exception"));
        String[] args = new String[0];

        // Act & Assert
        // Since the RanksCommand doesn't catch exceptions, we expect the exception to be thrown
        // This is a design choice in the command implementation
        assertThrows(RuntimeException.class, () -> {
            ranksCommand.onCommand(sender, command, "ranks", args);
        });
        verify(ranksService).getAllRanks();
    }
}
