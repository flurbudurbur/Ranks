package dev.flur.ranks.command.commands;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.command.commands.subcommands.ReloadSubCommand;
import dev.flur.ranks.message.Messages;
import dev.flur.ranks.service.MessageService;
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

class ReloadSubCommandTest {

    @Mock
    private Ranks plugin;

    @Mock
    private MessageService messageService;

    @Mock
    private CommandSender sender;

    private ReloadSubCommand reloadSubCommand;
    private String[] emptyArgs = new String[0];
    private String[] fullReloadArgs = new String[]{"full"};

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reloadSubCommand = new ReloadSubCommand(plugin, messageService);
    }

    @Test
    @DisplayName("getPermission returns correct permission")
    void testGetPermission() {
        assertEquals("ranks.admin.reload", reloadSubCommand.getPermission());
    }

    @Nested
    @DisplayName("Regular Reload Tests")
    class RegularReloadTests {
        @Test
        @DisplayName("execute processes regular reload")
        void testExecuteWithRegularReload() {
            // Act
            boolean result = reloadSubCommand.execute(sender, emptyArgs);

            // Assert
            assertTrue(result, "Command should return true for regular reload");
            verify(plugin).reload();
            verify(messageService).sendMessage(sender, Messages.RELOAD_COMPLETED);
            verify(plugin, never()).reloadConfig();
        }
    }

    @Nested
    @DisplayName("Full Reload Tests")
    class FullReloadTests {
        @Test
        @DisplayName("execute processes full reload")
        void testExecuteWithFullReload() {
            // Act
            boolean result = reloadSubCommand.execute(sender, fullReloadArgs);

            // Assert
            assertTrue(result, "Command should return true for full reload");
            verify(plugin).reloadConfig();
            verify(plugin).reload();
            verify(messageService).sendMessage(sender, Messages.RELOAD_FULL_COMPLETED);
            verify(messageService).sendMessage(sender, Messages.RELOAD_COMPLETED);
        }

        @Test
        @DisplayName("execute processes full reload with mixed case argument")
        void testExecuteWithMixedCaseFullReloadArg() {
            // Arrange
            String[] mixedCaseArgs = new String[]{"FuLl"};

            // Act
            boolean result = reloadSubCommand.execute(sender, mixedCaseArgs);

            // Assert
            assertTrue(result, "Command should return true for full reload with mixed case argument");
            verify(plugin).reloadConfig();
            verify(plugin).reload();
            verify(messageService).sendMessage(sender, Messages.RELOAD_FULL_COMPLETED);
            verify(messageService).sendMessage(sender, Messages.RELOAD_COMPLETED);
        }
    }

    @Nested
    @DisplayName("Tab Complete Tests")
    class TabCompleteTests {
        @Test
        @DisplayName("tabComplete returns 'full' option when no args")
        void testTabCompleteNoArgs() {
            List<String> completions = reloadSubCommand.tabComplete(sender, new String[]{""});
            assertEquals(List.of("full"), completions);
        }

        @Test
        @DisplayName("tabComplete filters by prefix")
        void testTabCompleteWithPrefix() {
            List<String> completions = reloadSubCommand.tabComplete(sender, new String[]{"fu"});
            assertEquals(List.of("full"), completions);
        }

        @Test
        @DisplayName("tabComplete returns empty when prefix doesn't match")
        void testTabCompleteNoMatch() {
            List<String> completions = reloadSubCommand.tabComplete(sender, new String[]{"xyz"});
            assertTrue(completions.isEmpty());
        }

        @Test
        @DisplayName("tabComplete returns empty for second argument")
        void testTabCompleteSecondArg() {
            List<String> completions = reloadSubCommand.tabComplete(sender, new String[]{"full", ""});
            assertTrue(completions.isEmpty());
        }
    }
}
