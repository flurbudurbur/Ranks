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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Rankup Command Tests")
public class RankupCommandTest {

    @Mock
    private CommandSender mockSender;

    @Mock
    private Player mockPlayer;

    @Mock
    private Command mockCommand;

    @Mock
    private Permission mockPermission;

    private RankupCommand rankupCommand;
    private MockedStatic<Ranks> mockedStaticRanks;
    private MockedStatic<Utils> mockedStaticUtils;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rankupCommand = new RankupCommand();
        
        // Mock static methods
        mockedStaticRanks = mockStatic(Ranks.class);
        mockedStaticRanks.when(Ranks::getPermissions).thenReturn(mockPermission);
        
        mockedStaticUtils = mockStatic(Utils.class);
        
        // Set up the mock player
        when(mockPermission.getPrimaryGroup(any(Player.class))).thenReturn("current-rank");
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
        CommandInfo info = RankupCommand.class.getAnnotation(CommandInfo.class);
        
        // Verify that the annotation is not null
        assertNotNull(info, "CommandInfo annotation should not be null");
        
        // Verify that the annotation values are correct
        assertEquals("rankup", info.name(), "Command name should be 'rankup'");
        assertEquals("ranks.rankup", info.permission(), "Command permission should be 'ranks.rankup'");
        assertEquals("Rank up to the next available rank", info.description(), "Command description should be 'Rank up to the next available rank'");
    }

    @Test
    @DisplayName("Should handle multiple next ranks")
    public void shouldHandleMultipleNextRanks() {
        // Set up the mock Utils.getNext to return multiple ranks
        HashMap<String, String> nextRanks = new HashMap<>();
        nextRanks.put("rank1", "path1");
        nextRanks.put("rank2", "path2");
        mockedStaticUtils.when(() -> Utils.getNext(anyString())).thenReturn(nextRanks);
        
        // Execute the command as a player with no arguments
        boolean result = rankupCommand.onCommand(mockPlayer, mockCommand, "rankup", new String[0]);
        
        // Verify that the command returned true
        assertTrue(result, "Command should return true");
        
        // Verify that the player was informed about multiple ranks
        verify(mockPlayer).sendMessage(contains("multiple ranks"));
    }

    @Test
    @DisplayName("Should handle single next rank")
    public void shouldHandleSingleNextRank() {
        // Set up the mock Utils.getNext to return a single rank
        HashMap<String, String> nextRanks = new HashMap<>();
        nextRanks.put("next-rank", "path");
        mockedStaticUtils.when(() -> Utils.getNext(anyString())).thenReturn(nextRanks);
        
        // Set up the mock Utils.getRequirements to return an empty list
        ArrayList<Requirement> requirements = new ArrayList<>();
        mockedStaticUtils.when(() -> Utils.getRequirements(anyString(), any(Player.class))).thenReturn(requirements);
        
        // Execute the command as a player
        boolean result = rankupCommand.onCommand(mockPlayer, mockCommand, "rankup", new String[0]);
        
        // Verify that the command returned true
        assertTrue(result, "Command should return true");
        
        // Verify that the player's rank was changed
        verify(mockPermission).playerAddGroup(mockPlayer, "next-rank");
        verify(mockPermission).playerRemoveGroup(mockPlayer, "current-rank");
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
        List<String> completions = rankupCommand.onTabComplete(mockPlayer, mockCommand, "rankup", new String[]{"r"});
        
        // Verify that the completions are not null and contain the expected values
        assertNotNull(completions, "Tab completions should not be null");
        assertEquals(2, completions.size(), "Should return 2 completions");
        assertTrue(completions.contains("rank1"), "Completions should contain 'rank1'");
        assertTrue(completions.contains("rank2"), "Completions should contain 'rank2'");
    }
}