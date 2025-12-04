package dev.flur.ranks.service.services;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.message.Messages;
import dev.flur.ranks.result.Result;
import dev.flur.ranks.template.service.TemplateService;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultMessageServiceTest {

    private Ranks plugin;
    private TemplateService templateService;
    private Logger logger;
    private FileConfiguration config;
    private Server server;
    private DefaultMessageService messageService;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        plugin = mock(Ranks.class);
        templateService = mock(TemplateService.class);
        logger = mock(Logger.class);
        config = mock(FileConfiguration.class);
        server = mock(Server.class);

        // Set up plugin mocks
        when(plugin.getLogger()).thenReturn(logger);
        when(plugin.getConfig()).thenReturn(config);
        when(plugin.getServer()).thenReturn(server);

        // Set up config mock
        when(config.getString(eq("locale"), anyString())).thenReturn("en");

        // Create service
        messageService = new DefaultMessageService(plugin, templateService);
    }

    @Nested
    @DisplayName("Message Loading Tests")
    class MessageLoadingTests {

        @Test
        @DisplayName("Should reload via template service")
        void shouldReloadMessages() {
            // Act
            messageService.reload();

            // Assert
            verify(templateService).reload();
        }

        @Test
        @DisplayName("Should get message by key")
        void shouldGetMessageByKey() {
            // Arrange
            String key = "test.key";
            String locale = "en";
            Map<String, Object> context = new HashMap<>();
            context.put("param", "value");

            Component expectedComponent = Component.text("Test message with value");
            when(templateService.renderMessage(key, locale, context))
                    .thenReturn(Result.success(expectedComponent));

            // Act
            Component result = messageService.getMessage(key, locale, context);

            // Assert
            assertEquals(expectedComponent, result);
            verify(templateService).renderMessage(key, locale, context);
        }

        @Test
        @DisplayName("Should get message by enum")
        void shouldGetMessageByEnum() {
            // Arrange
            Messages message = mock(Messages.class);
            String key = "test.enum.key";
            String locale = "fr";
            Map<String, Object> context = new HashMap<>();

            when(message.getKey()).thenReturn(key);
            Component expectedComponent = Component.text("Message from enum");
            when(templateService.renderMessage(key, locale, context))
                    .thenReturn(Result.success(expectedComponent));

            // Act
            Component result = messageService.getMessage(message, locale, context);

            // Assert
            assertEquals(expectedComponent, result);
            verify(message).getKey();
            verify(templateService).renderMessage(key, locale, context);
        }

        @Test
        @DisplayName("Should return fallback for failed render")
        void shouldReturnFallbackForFailedRender() {
            // Arrange
            String key = "missing.key";
            String locale = "en";
            Map<String, Object> context = new HashMap<>();

            when(templateService.renderMessage(key, locale, context))
                    .thenReturn(Result.failure("Message not found"));

            // Act
            Component result = messageService.getMessage(key, locale, context);

            // Assert
            assertNotNull(result);
            // Verify the fallback format matches "Missing: <key>"
            assertEquals(Component.text("Missing: missing.key"), result);
            verify(logger).warning(contains("missing.key"));
        }
    }

    @Nested
    @DisplayName("Message Sending Tests")
    class MessageSendingTests {

        @Test
        @DisplayName("Should send message to command sender")
        void shouldSendMessageToCommandSender() {
            // Arrange
            CommandSender sender = mock(CommandSender.class);
            Messages message = mock(Messages.class);
            Map<String, Object> context = new HashMap<>();
            context.put("param", "value");

            String key = "test.sender.key";
            when(message.getKey()).thenReturn(key);

            Component expectedComponent = Component.text("Message to sender with value");
            when(templateService.renderMessage(eq(key), eq("en"), eq(context)))
                    .thenReturn(Result.success(expectedComponent));

            // Act
            messageService.sendMessage(sender, message, context);

            // Assert
            verify(sender).sendMessage(expectedComponent);
        }

        @Test
        @DisplayName("Should send message with empty context")
        void shouldSendMessageWithEmptyContext() {
            // Arrange
            CommandSender sender = mock(CommandSender.class);
            Messages message = mock(Messages.class);

            String key = "test.empty.key";
            when(message.getKey()).thenReturn(key);

            Component expectedComponent = Component.text("Message with empty context");

            // Capture the context argument
            ArgumentCaptor<Map<String, Object>> contextCaptor = ArgumentCaptor.forClass(Map.class);
            when(templateService.renderMessage(eq(key), eq("en"), contextCaptor.capture()))
                    .thenReturn(Result.success(expectedComponent));

            // Act
            messageService.sendMessage(sender, message);

            // Assert
            verify(sender).sendMessage(expectedComponent);

            // Verify empty context was passed
            Map<String, Object> capturedContext = contextCaptor.getValue();
            assertNotNull(capturedContext);
            assertTrue(capturedContext.isEmpty());
        }
    }

    @Nested
    @DisplayName("Locale Detection Tests")
    class LocaleDetectionTests {

        @Test
        @DisplayName("Should detect player locale")
        void shouldDetectPlayerLocale() {
            // Arrange
            Player player = mock(Player.class);
            Messages message = mock(Messages.class);
            Map<String, Object> context = new HashMap<>();

            String key = "test.locale.key";
            when(message.getKey()).thenReturn(key);

            Locale playerLocale = Locale.FRENCH;
            when(player.locale()).thenReturn(playerLocale);

            Component expectedComponent = Component.text("Localized message");
            when(templateService.renderMessage(eq(key), eq("fr"), eq(context)))
                    .thenReturn(Result.success(expectedComponent));

            // Act
            messageService.sendMessage(player, message, context);

            // Assert
            verify(player).locale();
            verify(templateService).renderMessage(key, "fr", context);
            verify(player).sendMessage(expectedComponent);
        }

        @Test
        @DisplayName("Should use default locale for null player locale")
        void shouldUseDefaultLocaleForNullPlayerLocale() {
            // Arrange
            Player player = mock(Player.class);
            Messages message = mock(Messages.class);
            Map<String, Object> context = new HashMap<>();

            String key = "test.null.locale.key";
            when(message.getKey()).thenReturn(key);

            when(player.locale()).thenReturn(null);

            Component expectedComponent = Component.text("Default locale message");
            when(templateService.renderMessage(eq(key), eq("en"), eq(context)))
                    .thenReturn(Result.success(expectedComponent));

            // Act
            messageService.sendMessage(player, message, context);

            // Assert
            verify(player).locale();
            verify(templateService).renderMessage(key, "en", context);
            verify(player).sendMessage(expectedComponent);
        }

        @Test
        @DisplayName("Should use default locale for non-player sender")
        void shouldUseDefaultLocaleForNonPlayerSender() {
            // Arrange
            CommandSender sender = mock(CommandSender.class);
            Messages message = mock(Messages.class);
            Map<String, Object> context = new HashMap<>();

            String key = "test.console.key";
            when(message.getKey()).thenReturn(key);

            Component expectedComponent = Component.text("Console message");
            when(templateService.renderMessage(eq(key), eq("en"), eq(context)))
                    .thenReturn(Result.success(expectedComponent));

            // Act
            messageService.sendMessage(sender, message, context);

            // Assert
            verify(templateService).renderMessage(key, "en", context);
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("Should shutdown gracefully")
        void shouldShutdownGracefully() {
            // Act
            messageService.shutdown();

            // Assert
            // shutdown() has no observable side effects - Paper manages Adventure lifecycle
            // Verify no exceptions are thrown and service remains operational
            assertDoesNotThrow(() -> messageService.shutdown());

            // Verify service still functions after shutdown (no cleanup performed)
            String key = "test.post.shutdown";
            String locale = "en";
            Component expectedComponent = Component.text("Still works");
            when(templateService.renderMessage(key, locale, Collections.emptyMap()))
                    .thenReturn(Result.success(expectedComponent));

            Component result = messageService.getMessage(key, locale, Collections.emptyMap());
            assertEquals(expectedComponent, result);
        }
    }
}
