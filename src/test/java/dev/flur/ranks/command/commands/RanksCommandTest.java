package dev.flur.ranks.command.commands;

import dev.flur.ranks.command.CommandInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Ranks Command Tests")
public class RanksCommandTest {

    @Mock
    private CommandSender mockSender;

    @Mock
    private Player mockPlayer;

    @Mock
    private Command mockCommand;

    private RanksCommand ranksCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ranksCommand = new RanksCommand();

        // Set up the mock player
        when(mockPlayer.getName()).thenReturn("TestPlayer");
    }

    @Test
    @DisplayName("Should have correct command info annotation")
    public void shouldHaveCorrectCommandInfoAnnotation() {
        // Get the CommandInfo annotation
        CommandInfo info = RanksCommand.class.getAnnotation(CommandInfo.class);

        // Verify that the annotation is not null
        assertNotNull(info, "CommandInfo annotation should not be null");

        // Verify that the annotation values are correct
        assertEquals("ranks", info.name(), "Command name should be 'ranks'");
        assertEquals("ranks.view", info.permission(), "Command permission should be 'ranks.view'");
        assertEquals("View available ranks", info.description(), "Command description should be 'View available ranks'");
    }
}
