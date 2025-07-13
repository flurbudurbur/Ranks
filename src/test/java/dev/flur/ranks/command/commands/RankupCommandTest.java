package dev.flur.ranks.command.commands;

import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.result.Result;
import dev.flur.ranks.result.RankupOutcome;
import dev.flur.ranks.service.RankupNotifier;
import dev.flur.ranks.service.RankupProcessor;
import dev.flur.ranks.service.RankupValidator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RankupCommandTest {

    @Mock
    private RankupValidator rankupValidator;

    @Mock
    private RankupProcessor rankupProcessor;

    @Mock
    private RankupNotifier rankupNotifier;

    @Mock
    private Logger logger;

    @Mock
    private Command command;

    @Mock
    private Player player;

    @Mock
    private CommandSender nonPlayerSender;

    private RankupCommand rankupCommand;
    private final String[] emptyArgs = new String[0];

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rankupCommand = new RankupCommand(rankupValidator, rankupProcessor, rankupNotifier, logger);
    }

    @Nested
    @DisplayName("Non-Player Sender Tests")
    class NonPlayerSenderTests {
        @Test
        @DisplayName("onCommand returns true and sends player-only message for non-player sender")
        void testOnCommandWithNonPlayerSender() {
            // Act
            boolean result = rankupCommand.onCommand(nonPlayerSender, command, "rankup", emptyArgs);

            // Assert
            assertTrue(result, "Command should return true for non-player sender");
            verify(rankupNotifier).sendPlayerOnlyMessage(nonPlayerSender);
            verifyNoMoreInteractions(rankupValidator, rankupProcessor);
        }
    }

    @Nested
    @DisplayName("Current Rank Validation Tests")
    class CurrentRankValidationTests {
        @Test
        @DisplayName("onCommand handles invalid current rank")
        void testOnCommandWithInvalidCurrentRank() {
            // Arrange
            when(rankupValidator.hasValidCurrentRank(player)).thenReturn(true);

            // Act
            boolean result = rankupCommand.onCommand(player, command, "rankup", emptyArgs);

            // Assert
            assertTrue(result, "Command should return true when current rank is invalid");
            verify(rankupValidator).hasValidCurrentRank(player);
            verify(rankupNotifier).sendCurrentRankErrorMessage(player);
            verifyNoMoreInteractions(rankupProcessor);
        }

        @Test
        @DisplayName("onCommand handles no available ranks")
        void testOnCommandWithNoAvailableRanks() {
            // Arrange
            when(rankupValidator.hasValidCurrentRank(player)).thenReturn(false);
            when(rankupValidator.getCurrentRank(player)).thenReturn("current-rank");
            when(rankupProcessor.getAvailableRanks(player)).thenReturn(Collections.emptyMap());

            // Act
            boolean result = rankupCommand.onCommand(player, command, "rankup", emptyArgs);

            // Assert
            assertTrue(result, "Command should return true when no ranks are available");
            verify(rankupValidator).hasValidCurrentRank(player);
            verify(rankupValidator).getCurrentRank(player);
            verify(rankupProcessor).getAvailableRanks(player);
            verify(rankupNotifier).sendHighestRankMessage(player);
        }
    }

    @Nested
    @DisplayName("Rank Selection Tests")
    class RankSelectionTests {
        @Test
        @DisplayName("onCommand shows rank options when multiple ranks are available and no rank is specified")
        void testOnCommandShowsRankOptions() {
            // Arrange
            Map<String, String> availableRanks = new HashMap<>();
            availableRanks.put("rank1", "Rank 1");
            availableRanks.put("rank2", "Rank 2");

            when(rankupValidator.hasValidCurrentRank(player)).thenReturn(false);
            when(rankupValidator.getCurrentRank(player)).thenReturn("current-rank");
            when(rankupProcessor.getAvailableRanks(player)).thenReturn(availableRanks);
            when(rankupValidator.shouldShowRankOptions(availableRanks, emptyArgs)).thenReturn(true);

            // Act
            boolean result = rankupCommand.onCommand(player, command, "rankup", emptyArgs);

            // Assert
            assertTrue(result, "Command should return true when showing rank options");
            verify(rankupValidator).hasValidCurrentRank(player);
            verify(rankupValidator).getCurrentRank(player);
            verify(rankupProcessor).getAvailableRanks(player);
            verify(rankupValidator).shouldShowRankOptions(availableRanks, emptyArgs);
            verify(rankupNotifier).showAvailableRanks(player, availableRanks);
        }

        @Test
        @DisplayName("onCommand handles invalid rank selection")
        void testOnCommandWithInvalidRankSelection() {
            // Arrange
            Map<String, String> availableRanks = new HashMap<>();
            availableRanks.put("rank1", "Rank 1");
            availableRanks.put("rank2", "Rank 2");
            String[] args = new String[]{"invalid-rank"};

            when(rankupValidator.hasValidCurrentRank(player)).thenReturn(false);
            when(rankupValidator.getCurrentRank(player)).thenReturn("current-rank");
            when(rankupProcessor.getAvailableRanks(player)).thenReturn(availableRanks);
            when(rankupValidator.shouldShowRankOptions(availableRanks, args)).thenReturn(false);
            when(rankupValidator.determineTargetRank(availableRanks, args)).thenReturn(null);

            // Act
            boolean result = rankupCommand.onCommand(player, command, "rankup", args);

            // Assert
            assertTrue(result, "Command should return true when rank selection is invalid");
            verify(rankupValidator).hasValidCurrentRank(player);
            verify(rankupValidator).getCurrentRank(player);
            verify(rankupProcessor).getAvailableRanks(player);
            verify(rankupValidator).shouldShowRankOptions(availableRanks, args);
            verify(rankupValidator).determineTargetRank(availableRanks, args);
            verify(rankupNotifier).showInvalidRankMessage(player, availableRanks);
        }
    }

    @Nested
    @DisplayName("Rankup Process Tests")
    class RankupProcessTests {
        @Test
        @DisplayName("onCommand handles unmet requirements")
        void testOnCommandWithUnmetRequirements() {
            // Arrange
            Map<String, String> availableRanks = new HashMap<>();
            availableRanks.put("target-rank", "Target Rank");
            String[] args = new String[]{"target-rank"};
            List<Requirement> unmetRequirements = List.of(mock(Requirement.class));

            when(rankupValidator.hasValidCurrentRank(player)).thenReturn(false);
            when(rankupValidator.getCurrentRank(player)).thenReturn("current-rank");
            when(rankupProcessor.getAvailableRanks(player)).thenReturn(availableRanks);
            when(rankupValidator.shouldShowRankOptions(availableRanks, args)).thenReturn(false);
            when(rankupValidator.determineTargetRank(availableRanks, args)).thenReturn("target-rank");
            when(rankupValidator.getUnmetRequirementsForRank(player, "target-rank")).thenReturn(unmetRequirements);

            // Act
            boolean result = rankupCommand.onCommand(player, command, "rankup", args);

            // Assert
            assertFalse(result, "Command should return false when requirements are not met");
            verify(rankupValidator).hasValidCurrentRank(player);
            verify(rankupValidator).getCurrentRank(player);
            verify(rankupProcessor).getAvailableRanks(player);
            verify(rankupValidator).shouldShowRankOptions(availableRanks, args);
            verify(rankupValidator).determineTargetRank(availableRanks, args);
            verify(rankupValidator).getUnmetRequirementsForRank(player, "target-rank");
            verify(rankupNotifier).notifyUnmetRequirements(player, unmetRequirements);
        }

        @Test
        @DisplayName("onCommand processes successful rankup")
        void testOnCommandWithSuccessfulRankup() {
            // Arrange
            Map<String, String> availableRanks = new HashMap<>();
            availableRanks.put("target-rank", "Target Rank");
            String[] args = new String[]{"target-rank"};
            Result<RankupOutcome> successResult = Result.success(new RankupOutcome(player, "current-rank", "target-rank", true));

            when(rankupValidator.hasValidCurrentRank(player)).thenReturn(false);
            when(rankupValidator.getCurrentRank(player)).thenReturn("current-rank");
            when(rankupProcessor.getAvailableRanks(player)).thenReturn(availableRanks);
            when(rankupValidator.shouldShowRankOptions(availableRanks, args)).thenReturn(false);
            when(rankupValidator.determineTargetRank(availableRanks, args)).thenReturn("target-rank");
            when(rankupValidator.getUnmetRequirementsForRank(player, "target-rank")).thenReturn(Collections.emptyList());
            when(rankupProcessor.processRankup(player, "current-rank", "target-rank")).thenReturn(successResult);

            // Act
            boolean result = rankupCommand.onCommand(player, command, "rankup", args);

            // Assert
            assertTrue(result, "Command should return true for successful rankup");
            verify(rankupValidator).hasValidCurrentRank(player);
            verify(rankupValidator).getCurrentRank(player);
            verify(rankupProcessor).getAvailableRanks(player);
            verify(rankupValidator).shouldShowRankOptions(availableRanks, args);
            verify(rankupValidator).determineTargetRank(availableRanks, args);
            verify(rankupValidator).getUnmetRequirementsForRank(player, "target-rank");
            verify(rankupProcessor).processRankup(player, "current-rank", "target-rank");
            verify(rankupNotifier).sendRankupSuccessMessage(player, "target-rank");
            verify(rankupNotifier).broadcastRankup(player, "current-rank", "target-rank");
        }

        @Test
        @DisplayName("onCommand handles failed rankup")
        void testOnCommandWithFailedRankup() {
            // Arrange
            Map<String, String> availableRanks = new HashMap<>();
            availableRanks.put("target-rank", "Target Rank");
            String[] args = new String[]{"target-rank"};
            Result<RankupOutcome> failureResult = Result.failure("Failed to rankup");

            when(rankupValidator.hasValidCurrentRank(player)).thenReturn(false);
            when(rankupValidator.getCurrentRank(player)).thenReturn("current-rank");
            when(rankupProcessor.getAvailableRanks(player)).thenReturn(availableRanks);
            when(rankupValidator.shouldShowRankOptions(availableRanks, args)).thenReturn(false);
            when(rankupValidator.determineTargetRank(availableRanks, args)).thenReturn("target-rank");
            when(rankupValidator.getUnmetRequirementsForRank(player, "target-rank")).thenReturn(Collections.emptyList());
            when(rankupProcessor.processRankup(player, "current-rank", "target-rank")).thenReturn(failureResult);

            // Act
            boolean result = rankupCommand.onCommand(player, command, "rankup", args);

            // Assert
            assertFalse(result, "Command should return false for failed rankup");
            verify(rankupValidator).hasValidCurrentRank(player);
            verify(rankupValidator).getCurrentRank(player);
            verify(rankupProcessor).getAvailableRanks(player);
            verify(rankupValidator).shouldShowRankOptions(availableRanks, args);
            verify(rankupValidator).determineTargetRank(availableRanks, args);
            verify(rankupValidator).getUnmetRequirementsForRank(player, "target-rank");
            verify(rankupProcessor).processRankup(player, "current-rank", "target-rank");
            verify(rankupNotifier).sendRankupFailedMessage(player);
            verify(logger).warning(contains("Failed to rankup"));
        }
    }

    @Nested
    @DisplayName("Tab Completion Tests")
    class TabCompletionTests {
        @Test
        @DisplayName("onTabComplete returns empty list for non-player sender")
        void testOnTabCompleteWithNonPlayerSender() {
            // Act
            List<String> result = rankupCommand.onTabComplete(nonPlayerSender, command, "rankup", emptyArgs);

            // Assert
            assertTrue(result.isEmpty(), "Tab completion should return empty list for non-player sender");
        }

        @Test
        @DisplayName("onTabComplete returns empty list when args length is not 1")
        void testOnTabCompleteWithInvalidArgsLength() {
            // Arrange
            String[] args = new String[]{"arg1", "arg2"};

            // Act
            List<String> result = rankupCommand.onTabComplete(player, command, "rankup", args);

            // Assert
            assertTrue(result.isEmpty(), "Tab completion should return empty list when args length is not 1");
        }

        @Test
        @DisplayName("onTabComplete returns empty list for invalid current rank")
        void testOnTabCompleteWithInvalidCurrentRank() {
            // Arrange
            String[] args = new String[]{""};
            when(rankupValidator.hasValidCurrentRank(player)).thenReturn(true);

            // Act
            List<String> result = rankupCommand.onTabComplete(player, command, "rankup", args);

            // Assert
            assertTrue(result.isEmpty(), "Tab completion should return empty list for invalid current rank");
            verify(rankupValidator).hasValidCurrentRank(player);
        }


        @Test
        @DisplayName("onTabComplete returns available ranks")
        void testOnTabCompleteReturnsAvailableRanks() {
            // Arrange
            Map<String, String> availableRanks = new HashMap<>();
            availableRanks.put("rank1", "Rank 1");
            availableRanks.put("rank2", "Rank 2");
            String[] args = new String[]{""};

            when(rankupValidator.hasValidCurrentRank(player)).thenReturn(false);
            when(rankupProcessor.getAvailableRanks(player)).thenReturn(availableRanks);

            // Act
            List<String> result = rankupCommand.onTabComplete(player, command, "rankup", args);

            // Assert
            assertEquals(2, result.size(), "Tab completion should return 2 available ranks");
            assertTrue(result.contains("rank1"), "Tab completion should include rank1");
            assertTrue(result.contains("rank2"), "Tab completion should include rank2");
            verify(rankupValidator).hasValidCurrentRank(player);
            verify(rankupProcessor).getAvailableRanks(player);
        }

        @Test
        @DisplayName("onTabComplete filters ranks by prefix")
        void testOnTabCompleteFiltersRanksByPrefix() {
            // Arrange
            Map<String, String> availableRanks = new HashMap<>();
            availableRanks.put("rank1", "Rank 1");
            availableRanks.put("rank2", "Rank 2");
            availableRanks.put("test", "Test Rank");
            String[] args = new String[]{"r"};

            when(rankupValidator.hasValidCurrentRank(player)).thenReturn(false);
            when(rankupProcessor.getAvailableRanks(player)).thenReturn(availableRanks);

            // Act
            List<String> result = rankupCommand.onTabComplete(player, command, "rankup", args);

            // Assert
            assertEquals(2, result.size(), "Tab completion should return 2 filtered ranks");
            assertTrue(result.contains("rank1"), "Tab completion should include rank1");
            assertTrue(result.contains("rank2"), "Tab completion should include rank2");
            assertFalse(result.contains("test"), "Tab completion should not include test");
            verify(rankupValidator).hasValidCurrentRank(player);
            verify(rankupProcessor).getAvailableRanks(player);
        }

        @Test
        @DisplayName("onTabComplete handles exception")
        void testOnTabCompleteHandlesException() {
            // Arrange
            when(rankupValidator.hasValidCurrentRank(player)).thenThrow(new RuntimeException("Test exception"));

            // Act
            List<String> result = rankupCommand.onTabComplete(player, command, "rankup", new String[]{""});

            // Assert
            assertTrue(result.isEmpty(), "Tab completion should return empty list when exception occurs");
            verify(rankupValidator).hasValidCurrentRank(player);
            verify(logger).severe(contains("Error in tab completion"));
        }
    }
}
