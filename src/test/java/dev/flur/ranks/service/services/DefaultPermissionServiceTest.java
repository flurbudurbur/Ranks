package dev.flur.ranks.service.services;

import dev.flur.ranks.service.PermissionService;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultPermissionServiceTest {

    private Permission permission;
    private Logger logger;
    private DefaultPermissionService permissionService;
    private Player player;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        permission = mock(Permission.class);
        logger = mock(Logger.class);
        player = mock(Player.class);

        // Set up player mock
        when(player.getName()).thenReturn("TestPlayer");

        // Create service
        permissionService = new DefaultPermissionService(permission, logger);
    }

    @Nested
    @DisplayName("Permission Checking Tests")
    class PermissionCheckingTests {

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @DisplayName("Should check if player has permission")
        void shouldCheckIfPlayerHasPermission(boolean hasPermission) {
            // Arrange
            String permissionNode = "ranks.test.permission";
            when(permission.has(player, permissionNode)).thenReturn(hasPermission);

            // Act
            boolean result = permissionService.hasPermission(player, permissionNode);

            // Assert
            assertEquals(hasPermission, result);
            verify(permission).has(player, permissionNode);
            verifyNoInteractions(logger);
        }

        @Test
        @DisplayName("Should handle exception when checking permission")
        void shouldHandleExceptionWhenCheckingPermission() {
            // Arrange
            String permissionNode = "ranks.test.permission";
            when(permission.has(player, permissionNode)).thenThrow(new RuntimeException("Test exception"));

            // Act
            boolean result = permissionService.hasPermission(player, permissionNode);

            // Assert
            assertFalse(result);
            verify(permission).has(player, permissionNode);
            verify(logger).warning(contains("Error checking permission"));
        }
    }

    @Nested
    @DisplayName("Group Management Tests")
    class GroupManagementTests {

        @Test
        @DisplayName("Should get primary group")
        void shouldGetPrimaryGroup() {
            // Arrange
            String groupName = "default";
            when(permission.getPrimaryGroup(player)).thenReturn(groupName);

            // Act
            String result = permissionService.getPrimaryGroup(player);

            // Assert
            assertEquals(groupName, result);
            verify(permission).getPrimaryGroup(player);
            verifyNoInteractions(logger);
        }

        @Test
        @DisplayName("Should handle null primary group")
        void shouldHandleNullPrimaryGroup() {
            // Arrange
            when(permission.getPrimaryGroup(player)).thenReturn(null);

            // Act
            String result = permissionService.getPrimaryGroup(player);

            // Assert
            assertEquals("", result);
            verify(permission).getPrimaryGroup(player);
            verifyNoInteractions(logger);
        }

        @Test
        @DisplayName("Should handle exception when getting primary group")
        void shouldHandleExceptionWhenGettingPrimaryGroup() {
            // Arrange
            when(permission.getPrimaryGroup(player)).thenThrow(new RuntimeException("Test exception"));

            // Act
            String result = permissionService.getPrimaryGroup(player);

            // Assert
            assertEquals("", result);
            verify(permission).getPrimaryGroup(player);
            verify(logger).warning(contains("Error getting primary group"));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @DisplayName("Should add player to group")
        void shouldAddPlayerToGroup(boolean success) {
            // Arrange
            String groupName = "vip";
            when(permission.playerAddGroup(player, groupName)).thenReturn(success);

            // Act
            boolean result = permissionService.addToGroup(player, groupName);

            // Assert
            assertEquals(success, result);
            verify(permission).playerAddGroup(player, groupName);
            verifyNoInteractions(logger);
        }

        @Test
        @DisplayName("Should handle exception when adding player to group")
        void shouldHandleExceptionWhenAddingPlayerToGroup() {
            // Arrange
            String groupName = "vip";
            when(permission.playerAddGroup(player, groupName)).thenThrow(new RuntimeException("Test exception"));

            // Act
            boolean result = permissionService.addToGroup(player, groupName);

            // Assert
            assertFalse(result);
            verify(permission).playerAddGroup(player, groupName);
            verify(logger).warning(contains("Error adding player"));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @DisplayName("Should remove player from group")
        void shouldRemovePlayerFromGroup(boolean success) {
            // Arrange
            String groupName = "vip";
            when(permission.playerRemoveGroup(player, groupName)).thenReturn(success);

            // Act
            boolean result = permissionService.removeFromGroup(player, groupName);

            // Assert
            assertEquals(success, result);
            verify(permission).playerRemoveGroup(player, groupName);
            verifyNoInteractions(logger);
        }

        @Test
        @DisplayName("Should handle exception when removing player from group")
        void shouldHandleExceptionWhenRemovingPlayerFromGroup() {
            // Arrange
            String groupName = "vip";
            when(permission.playerRemoveGroup(player, groupName)).thenThrow(new RuntimeException("Test exception"));

            // Act
            boolean result = permissionService.removeFromGroup(player, groupName);

            // Assert
            assertFalse(result);
            verify(permission).playerRemoveGroup(player, groupName);
            verify(logger).warning(contains("Error removing player"));
        }
    }

    @Nested
    @DisplayName("Group Existence Tests")
    class GroupExistenceTests {

        @Test
        @DisplayName("Should check if group exists")
        void shouldCheckIfGroupExists() {
            // Arrange
            String groupName = "vip";
            // Explicitly cast null to String to avoid ambiguity
            when(permission.groupHas((String)null, groupName, "")).thenReturn(true);

            // Act
            boolean result = permissionService.groupExists(groupName);

            // Assert
            assertTrue(result);
            verify(permission).groupHas((String)null, groupName, "");
        }

        @Test
        @DisplayName("Should handle exception when checking if group exists")
        void shouldHandleExceptionWhenCheckingIfGroupExists() {
            // Arrange
            String groupName = "nonexistent";
            // Explicitly cast null to String to avoid ambiguity
            when(permission.groupHas((String)null, groupName, "")).thenThrow(new RuntimeException("Group doesn't exist"));

            // Act
            boolean result = permissionService.groupExists(groupName);

            // Assert
            assertFalse(result);
            verify(permission).groupHas((String)null, groupName, "");
            // No logging for this method
            verifyNoInteractions(logger);
        }
    }
}
