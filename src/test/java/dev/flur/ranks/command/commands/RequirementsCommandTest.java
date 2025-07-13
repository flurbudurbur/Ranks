package dev.flur.ranks.command.commands;

import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.service.RanksService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequirementsCommandTest {

    @Mock
    private RanksService ranksService;

    @Mock
    private Logger logger;

    @Mock
    private Command command;

    @Mock
    private Player player;

    @Mock
    private CommandSender nonPlayerSender;

    @Mock
    private Requirement requirement1;

    @Mock
    private Requirement requirement2;

    private RequirementsCommand requirementsCommand;
    private final String[] emptyArgs = new String[0];
    private final String[] validArgs = new String[]{"next-rank"};

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requirementsCommand = new RequirementsCommand(ranksService, logger);

        // Setup requirement mocks
        when(requirement1.toString()).thenReturn("Requirement 1");
        when(requirement2.toString()).thenReturn("Requirement 2");
    }

    @Nested
    @DisplayName("Sender Type Tests")
    class SenderTypeTests {
        @Test
        @DisplayName("onCommand returns true and does nothing for non-player sender")
        void testOnCommandWithNonPlayerSender() {
            // Act
            boolean result = requirementsCommand.onCommand(nonPlayerSender, command, "requirements", validArgs);

            // Assert
            assertTrue(result, "Command should return true for non-player sender");
            verifyNoInteractions(ranksService);
            verify(logger, never()).info(anyString());
        }
    }

    @Nested
    @DisplayName("Arguments Tests")
    class ArgumentsTests {
        @Test
        @DisplayName("onCommand returns true and does nothing with empty args")
        void testOnCommandWithEmptyArgs() {
            // Act
            boolean result = requirementsCommand.onCommand(player, command, "requirements", emptyArgs);

            // Assert
            assertTrue(result, "Command should return true with empty args");
            verifyNoInteractions(ranksService);
            verify(logger, never()).info(anyString());
        }

        @Test
        @DisplayName("onCommand processes valid rank argument")
        void testOnCommandWithValidRankArg() {
            // Arrange
            List<Requirement> requirements = Arrays.asList(requirement1, requirement2);
            when(ranksService.getRequirements("next-rank", player)).thenReturn(requirements);

            // Act
            boolean result = requirementsCommand.onCommand(player, command, "requirements", validArgs);

            // Assert
            assertTrue(result, "Command should return true with valid rank arg");
            verify(logger).info("Requirements for next-rank:");
            verify(ranksService).getRequirements("next-rank", player);
            verify(player).sendMessage("Requirement 1");
            verify(player).sendMessage("Requirement 2");
        }

        @Test
        @DisplayName("onCommand handles empty requirements list")
        void testOnCommandWithEmptyRequirementsList() {
            // Arrange
            when(ranksService.getRequirements("next-rank", player)).thenReturn(Collections.emptyList());

            // Act
            boolean result = requirementsCommand.onCommand(player, command, "requirements", validArgs);

            // Assert
            assertTrue(result, "Command should return true with empty requirements list");
            verify(logger).info("Requirements for next-rank:");
            verify(ranksService).getRequirements("next-rank", player);
            verify(player, never()).sendMessage(anyString());
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        @Test
        @DisplayName("onCommand handles exception from service")
        void testOnCommandHandlesException() {
            // Arrange
            when(ranksService.getRequirements("next-rank", player)).thenThrow(new RuntimeException("Test exception"));

            // Act
            boolean result = requirementsCommand.onCommand(player, command, "requirements", validArgs);

            // Assert
            assertTrue(result, "Command should return true when exception occurs");
            verify(logger).info("Requirements for next-rank:");
            verify(ranksService).getRequirements("next-rank", player);
            verify(player).sendMessage("Invalid input");
        }
    }

    @Nested
    @DisplayName("Tab Completion Tests")
    class TabCompletionTests {
        @Test
        @DisplayName("onTabComplete returns empty list for non-player sender")
        void testOnTabCompleteWithNonPlayerSender() {
            // Act
            List<String> result = requirementsCommand.onTabComplete(nonPlayerSender, command, "requirements", emptyArgs);

            // Assert
            assertTrue(result.isEmpty(), "Tab completion should return empty list for non-player sender");
            verifyNoInteractions(ranksService);
        }

        @Test
        @DisplayName("onTabComplete returns next ranks for player")
        void testOnTabCompleteWithPlayer() {
            // Arrange
            Map<String, String> nextRanks = new HashMap<>();
            nextRanks.put("rank1", "Rank 1");
            nextRanks.put("rank2", "Rank 2");

            when(ranksService.getCurrentRank(player)).thenReturn("current-rank");
            when(ranksService.getNextRanks("current-rank")).thenReturn(nextRanks);

            // Act
            // The RequirementsCommand.onTabComplete method only returns the next ranks if args.length == 1
            String[] args = new String[]{""};
            List<String> result = requirementsCommand.onTabComplete(player, command, "requirements", args);

            // Assert
            assertEquals(2, result.size(), "Tab completion should return 2 next ranks");
            assertTrue(result.contains("rank1"), "Tab completion should include rank1");
            assertTrue(result.contains("rank2"), "Tab completion should include rank2");
            verify(ranksService).getCurrentRank(player);
            verify(ranksService).getNextRanks("current-rank");
        }

        @Test
        @DisplayName("onTabComplete returns empty list when args length is not 1")
        void testOnTabCompleteWithInvalidArgsLength() {
            // Arrange
            String[] args = new String[]{"arg1", "arg2"};
            Map<String, String> nextRanks = new HashMap<>();
            nextRanks.put("rank1", "Rank 1");

            when(ranksService.getCurrentRank(player)).thenReturn("current-rank");
            when(ranksService.getNextRanks("current-rank")).thenReturn(nextRanks);

            // Act
            List<String> result = requirementsCommand.onTabComplete(player, command, "requirements", args);

            // Assert
            assertTrue(result.isEmpty(), "Tab completion should return empty list when args length is not 1");
            verify(ranksService).getCurrentRank(player);
            verify(ranksService).getNextRanks("current-rank");
        }

        @Test
        @DisplayName("onTabComplete handles empty next ranks")
        void testOnTabCompleteWithEmptyNextRanks() {
            // Arrange
            when(ranksService.getCurrentRank(player)).thenReturn("current-rank");
            when(ranksService.getNextRanks("current-rank")).thenReturn(Collections.emptyMap());

            // Act
            List<String> result = requirementsCommand.onTabComplete(player, command, "requirements", emptyArgs);

            // Assert
            assertTrue(result.isEmpty(), "Tab completion should return empty list when no next ranks are available");
            verify(ranksService).getCurrentRank(player);
            verify(ranksService).getNextRanks("current-rank");
        }

        @Test
        @DisplayName("onTabComplete handles exception")
        void testOnTabCompleteHandlesException() {
            // Arrange
            when(ranksService.getCurrentRank(player)).thenThrow(new RuntimeException("Test exception"));

            // Act & Assert
            // Since the RequirementsCommand doesn't catch exceptions in onTabComplete, we expect the exception to be thrown
            assertThrows(RuntimeException.class, () -> requirementsCommand.onTabComplete(player, command, "requirements", emptyArgs));
            verify(ranksService).getCurrentRank(player);
        }
    }
}
