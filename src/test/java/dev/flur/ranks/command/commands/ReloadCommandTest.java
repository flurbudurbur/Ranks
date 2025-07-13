package dev.flur.ranks.command.commands;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.message.Locale;
import dev.flur.ranks.service.MessageService;
import dev.flur.ranks.service.ServiceContainer;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReloadCommandTest {

    @Mock
    private Ranks plugin;

    @Mock
    private ServiceContainer serviceContainer;

    @Mock
    private MessageService messageService;

    @Mock
    private Command command;

    @Mock
    private Player player;

    @Mock
    private ConsoleCommandSender consoleSender;

    private ReloadCommand reloadCommand;
    private final String[] emptyArgs = new String[0];
    private final String[] fullReloadArgs = new String[]{"full"};

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reloadCommand = new ReloadCommand(plugin, serviceContainer, messageService);
        
        // Setup player mock
        when(player.hasPermission("ranks.admin.reload")).thenReturn(true);
    }

    @Nested
    @DisplayName("Permission Tests")
    class PermissionTests {
        @Test
        @DisplayName("onCommand returns true and sends no permission message for player without permission")
        void testOnCommandWithPlayerWithoutPermission() {
            // Arrange
            when(player.hasPermission("ranks.admin.reload")).thenReturn(false);

            // Act
            boolean result = reloadCommand.onCommand(player, command, "reload", emptyArgs);

            // Assert
            assertTrue(result, "Command should return true for player without permission");
            verify(messageService).sendMessage(player, Locale.NO_PERMISSION);
            verifyNoMoreInteractions(plugin, serviceContainer);
        }

        @Test
        @DisplayName("onCommand processes reload for player with permission")
        void testOnCommandWithPlayerWithPermission() {
            // Arrange
            when(serviceContainer.isHealthy()).thenReturn(true);

            // Act
            boolean result = reloadCommand.onCommand(player, command, "reload", emptyArgs);

            // Assert
            assertTrue(result, "Command should return true for player with permission");
            verify(serviceContainer).reload();
            verify(serviceContainer).isHealthy();
            verify(messageService).sendMessage(player, Locale.RELOAD_COMPLETED);
        }

        @Test
        @DisplayName("onCommand processes reload for console sender")
        void testOnCommandWithConsoleSender() {
            // Arrange
            when(serviceContainer.isHealthy()).thenReturn(true);

            // Act
            boolean result = reloadCommand.onCommand(consoleSender, command, "reload", emptyArgs);

            // Assert
            assertTrue(result, "Command should return true for console sender");
            verify(serviceContainer).reload();
            verify(serviceContainer).isHealthy();
            verify(messageService).sendMessage(consoleSender, Locale.RELOAD_COMPLETED);
        }
    }

    @Nested
    @DisplayName("Regular Reload Tests")
    class RegularReloadTests {
        @Test
        @DisplayName("onCommand processes regular reload")
        void testOnCommandWithRegularReload() {
            // Arrange
            when(serviceContainer.isHealthy()).thenReturn(true);

            // Act
            boolean result = reloadCommand.onCommand(player, command, "reload", emptyArgs);

            // Assert
            assertTrue(result, "Command should return true for regular reload");
            verify(serviceContainer).reload();
            verify(serviceContainer).isHealthy();
            verify(messageService).sendMessage(player, Locale.RELOAD_COMPLETED);
            verify(plugin, never()).reloadConfig();
        }

        @Test
        @DisplayName("onCommand returns false when service container is not healthy")
        void testOnCommandWithUnhealthyServiceContainer() {
            // Arrange
            when(serviceContainer.isHealthy()).thenReturn(false);

            // Act
            boolean result = reloadCommand.onCommand(player, command, "reload", emptyArgs);

            // Assert
            assertFalse(result, "Command should return false when service container is not healthy");
            verify(serviceContainer).reload();
            verify(serviceContainer).isHealthy();
            verify(messageService, never()).sendMessage(player, Locale.RELOAD_COMPLETED);
        }
    }

    @Nested
    @DisplayName("Full Reload Tests")
    class FullReloadTests {
        @Test
        @DisplayName("onCommand processes full reload")
        void testOnCommandWithFullReload() {
            // Arrange
            when(serviceContainer.isHealthy()).thenReturn(true);

            // Act
            boolean result = reloadCommand.onCommand(player, command, "reload", fullReloadArgs);

            // Assert
            assertTrue(result, "Command should return true for full reload");
            verify(plugin).reloadConfig();
            verify(serviceContainer).reload();
            verify(serviceContainer).isHealthy();
            verify(messageService).sendMessage(player, Locale.RELOAD_FULL_COMPLETED);
            verify(messageService).sendMessage(player, Locale.RELOAD_COMPLETED);
        }

        @Test
        @DisplayName("onCommand returns false when service container is not healthy after full reload")
        void testOnCommandWithUnhealthyServiceContainerAfterFullReload() {
            // Arrange
            when(serviceContainer.isHealthy()).thenReturn(false);

            // Act
            boolean result = reloadCommand.onCommand(player, command, "reload", fullReloadArgs);

            // Assert
            assertFalse(result, "Command should return false when service container is not healthy after full reload");
            verify(plugin).reloadConfig();
            verify(serviceContainer).reload();
            verify(serviceContainer).isHealthy();
            verify(messageService).sendMessage(player, Locale.RELOAD_FULL_COMPLETED);
            verify(messageService, never()).sendMessage(player, Locale.RELOAD_COMPLETED);
        }

        @Test
        @DisplayName("onCommand processes full reload with mixed case argument")
        void testOnCommandWithMixedCaseFullReloadArg() {
            // Arrange
            when(serviceContainer.isHealthy()).thenReturn(true);
            String[] mixedCaseArgs = new String[]{"FuLl"};

            // Act
            boolean result = reloadCommand.onCommand(player, command, "reload", mixedCaseArgs);

            // Assert
            assertTrue(result, "Command should return true for full reload with mixed case argument");
            verify(plugin).reloadConfig();
            verify(serviceContainer).reload();
            verify(serviceContainer).isHealthy();
            verify(messageService).sendMessage(player, Locale.RELOAD_FULL_COMPLETED);
            verify(messageService).sendMessage(player, Locale.RELOAD_COMPLETED);
        }
    }
}