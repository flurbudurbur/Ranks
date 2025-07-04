package dev.flur.ranks.command.commands;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.command.CommandInfo;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.utils.Utils;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Requirements Command Tests")
public class RequirementsCommandTest {

    @Mock
    private CommandSender mockSender;

    @Mock
    private Player mockPlayer;

    @Mock
    private Command mockCommand;

    @Mock
    private Permission mockPermission;

    @Mock
    private Ranks mockPlugin;

    @Mock
    private Logger mockLogger;

    @Mock
    private Requirement mockRequirement;

    private RequirementsCommand requirementsCommand;
    private MockedStatic<Ranks> mockedStaticRanks;
    private MockedStatic<Utils> mockedStaticUtils;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        requirementsCommand = new RequirementsCommand();

        // Mock static methods
        mockedStaticRanks = mockStatic(Ranks.class);
        mockedStaticRanks.when(Ranks::getPermissions).thenReturn(mockPermission);
        mockedStaticRanks.when(Ranks::getPlugin).thenReturn(mockPlugin);

        mockedStaticUtils = mockStatic(Utils.class);

        // Set up the mock plugin
        when(mockPlugin.getLogger()).thenReturn(mockLogger);

        // Set up the mock player
        when(mockPermission.getPrimaryGroup(any(Player.class))).thenReturn("current-rank");

        // Set up the mock requirement
        when(mockRequirement.toString()).thenReturn("Test Requirement");
    }

    @AfterEach
    public void tearDown() {
        mockedStaticRanks.close();
        mockedStaticUtils.close();
    }

    @Test
    @DisplayName("Should have correct command info annotation")
    public void shouldHaveCorrectCommandInfoAnnotation() {
        // Get the CommandInfo annotation
        CommandInfo info = RequirementsCommand.class.getAnnotation(CommandInfo.class);

        // Verify that the annotation is not null
        assertNotNull(info, "CommandInfo annotation should not be null");

        // Verify that the annotation values are correct
        assertEquals("requirements", info.name(), "Command name should be 'requirements'");
        assertEquals("ranks.requirements", info.permission(), "Command permission should be 'ranks.requirements'");
        assertEquals("View requirements for ranks", info.description(), "Command description should be 'View requirements for ranks'");
    }

    @Test
    @DisplayName("Should display requirements for specified rank")
    public void shouldDisplayRequirementsForSpecifiedRank() {
        // Set up the mock Utils.getRequirements to return a list with one requirement
        ArrayList<Requirement> requirements = new ArrayList<>();
        requirements.add(mockRequirement);
        mockedStaticUtils.when(() -> Utils.getRequirements(anyString(), any(Player.class))).thenReturn(requirements);

        // Execute the command as a player with a rank argument
        boolean result = requirementsCommand.onCommand(mockPlayer, mockCommand, "requirements", new String[]{"next-rank"});

        // Verify that the command returned true
        assertTrue(result, "Command should return true");

        // Verify that the logger was called
        verify(mockLogger).info(contains("Requirements for next-rank"));

        // Verify that the player was shown the requirements
        verify(mockPlayer).sendMessage("Test Requirement");
    }

    @Test
    @DisplayName("Should handle exceptions")
    public void shouldHandleExceptions() {
        // Set up the mock Utils.getRequirements to throw an exception
        mockedStaticUtils.when(() -> Utils.getRequirements(anyString(), any(Player.class))).thenThrow(new RuntimeException("Test exception"));

        // Execute the command as a player with a rank argument
        boolean result = requirementsCommand.onCommand(mockPlayer, mockCommand, "requirements", new String[]{"next-rank"});

        // Verify that the command returned true
        assertTrue(result, "Command should return true");

        // Verify that the player was informed about the invalid input
        verify(mockPlayer).sendMessage("Invalid input");
    }

    @Test
    @DisplayName("Should provide tab completion")
    public void shouldProvideTabCompletion() {
        // Set up the mock Utils.getNext to return multiple ranks
        HashMap<String, String> nextRanks = new HashMap<>();
        nextRanks.put("rank1", "path1");
        nextRanks.put("rank2", "path2");
        mockedStaticUtils.when(() -> Utils.getNext(anyString())).thenReturn(nextRanks);

        // Execute tab completion
        List<String> completions = requirementsCommand.onTabComplete(mockPlayer, mockCommand, "requirements", new String[]{"r"});

        // Verify that the completions are not null and contain the expected values
        assertNotNull(completions, "Tab completions should not be null");
        assertEquals(2, completions.size(), "Should return 2 completions");
        assertTrue(completions.contains("rank1"), "Completions should contain 'rank1'");
        assertTrue(completions.contains("rank2"), "Completions should contain 'rank2'");
    }
}
