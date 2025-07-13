package dev.flur.ranks.service.services;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.message.Locale;
import dev.flur.ranks.message.MessageLoader;
import dev.flur.ranks.message.TemplateProcessor;
import dev.flur.ranks.service.ConfigurationService;
import dev.flur.ranks.service.config.TomlConfiguration;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultMessageServiceTest {

    private Ranks plugin;
    private ConfigurationService configService;
    private MessageLoader messageLoader;
    private TemplateProcessor templateProcessor;
    private BukkitAudiences audiences;
    private Logger logger;
    private TomlConfiguration config;
    private DefaultMessageService messageService;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        plugin = mock(Ranks.class);
        configService = mock(ConfigurationService.class);
        messageLoader = mock(MessageLoader.class);
        templateProcessor = mock(TemplateProcessor.class);
        audiences = mock(BukkitAudiences.class);
        logger = mock(Logger.class);
        config = mock(TomlConfiguration.class);

        // Mock ServiceContainer
        dev.flur.ranks.service.ServiceContainer serviceContainer = mock(dev.flur.ranks.service.ServiceContainer.class);
        when(serviceContainer.getConfigurationService()).thenReturn(configService);
        when(plugin.getServiceContainer()).thenReturn(serviceContainer);

        // Mock PluginDescriptionFile
        org.bukkit.plugin.PluginDescriptionFile descriptionFile = mock(org.bukkit.plugin.PluginDescriptionFile.class);
        when(descriptionFile.getName()).thenReturn("Ranks");
        when(plugin.getDescription()).thenReturn(descriptionFile);

        // Mock Server, PluginManager, and ConsoleSender
        org.bukkit.Server server = mock(org.bukkit.Server.class);
        org.bukkit.command.ConsoleCommandSender consoleSender = mock(org.bukkit.command.ConsoleCommandSender.class);
        org.bukkit.plugin.PluginManager pluginManager = mock(org.bukkit.plugin.PluginManager.class);

        when(server.getConsoleSender()).thenReturn(consoleSender);
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(plugin.getServer()).thenReturn(server);

        // Allow any event registration
        doNothing().when(pluginManager).registerEvent(
            any(Class.class), 
            any(org.bukkit.event.Listener.class), 
            any(org.bukkit.event.EventPriority.class), 
            any(org.bukkit.plugin.EventExecutor.class), 
            any(org.bukkit.plugin.Plugin.class), 
            anyBoolean()
        );

        // Set up plugin mocks
        when(plugin.getLogger()).thenReturn(logger);
        when(plugin.getConfig()).thenReturn(config);

        // Set up config mock
        when(config.getString(eq("locale"), anyString())).thenReturn("en");

        // Create service with constructor injection for testing
        messageService = new DefaultMessageService(plugin, configService) {
            // Override constructor-initialized fields for testing
            {
                try {
                    // Use reflection to set private fields
                    java.lang.reflect.Field loaderField = DefaultMessageService.class.getDeclaredField("messageLoader");
                    loaderField.setAccessible(true);
                    loaderField.set(this, messageLoader);

                    java.lang.reflect.Field processorField = DefaultMessageService.class.getDeclaredField("templateProcessor");
                    processorField.setAccessible(true);
                    processorField.set(this, templateProcessor);

                    java.lang.reflect.Field audiencesField = DefaultMessageService.class.getDeclaredField("audiences");
                    audiencesField.setAccessible(true);
                    audiencesField.set(this, audiences);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to set up test", e);
                }
            }
        };
    }

    @Nested
    @DisplayName("Message Loading Tests")
    class MessageLoadingTests {

        @Test
        @DisplayName("Should reload messages")
        void shouldReloadMessages() {
            // Act
            messageService.reload();

            // Assert
            verify(messageLoader).loadLocales();
        }

        @Test
        @DisplayName("Should get message by key")
        void shouldGetMessageByKey() {
            // Arrange
            String key = "test.key";
            String locale = "en";
            Map<String, Object> context = new HashMap<>();
            context.put("param", "value");

            String rawMessage = "Test message with {{ param }}";
            Component expectedComponent = Component.text("Test message with value");

            when(messageLoader.getRawMessage(key, locale, "en")).thenReturn(rawMessage);
            when(templateProcessor.processTemplate(rawMessage, context)).thenReturn(expectedComponent);

            // Act
            Component result = messageService.getMessage(key, locale, context);

            // Assert
            assertSame(expectedComponent, result);
            verify(messageLoader).getRawMessage(key, locale, "en");
            verify(templateProcessor).processTemplate(rawMessage, context);
        }

        @Test
        @DisplayName("Should get message by enum")
        void shouldGetMessageByEnum() {
            // Arrange
            Locale message = mock(Locale.class);
            String key = "test.enum.key";
            String locale = "fr";
            Map<String, Object> context = new HashMap<>();

            when(message.getKey()).thenReturn(key);
            String rawMessage = "Message from enum";
            Component expectedComponent = Component.text("Message from enum");

            when(messageLoader.getRawMessage(key, locale, "en")).thenReturn(rawMessage);
            when(templateProcessor.processTemplate(rawMessage, context)).thenReturn(expectedComponent);

            // Act
            Component result = messageService.getMessage(message, locale, context);

            // Assert
            assertSame(expectedComponent, result);
            verify(message).getKey();
            verify(messageLoader).getRawMessage(key, locale, "en");
            verify(templateProcessor).processTemplate(rawMessage, context);
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
            Locale message = mock(Locale.class);
            Map<String, Object> context = new HashMap<>();
            context.put("param", "value");

            String key = "test.sender.key";
            when(message.getKey()).thenReturn(key);

            String rawMessage = "Message to sender with {{ param }}";
            Component expectedComponent = Component.text("Message to sender with value");

            when(messageLoader.getRawMessage(key, "en", "en")).thenReturn(rawMessage);
            when(templateProcessor.processTemplate(rawMessage, context)).thenReturn(expectedComponent);

            Audience senderAudience = mock(Audience.class);
            when(audiences.sender(sender)).thenReturn(senderAudience);

            // Act
            messageService.sendMessage(sender, message, context);

            // Assert
            verify(audiences).sender(sender);
            verify(senderAudience).sendMessage(expectedComponent);
        }

        @Test
        @DisplayName("Should send message with empty context")
        void shouldSendMessageWithEmptyContext() {
            // Arrange
            CommandSender sender = mock(CommandSender.class);
            Locale message = mock(Locale.class);

            String key = "test.empty.key";
            when(message.getKey()).thenReturn(key);

            String rawMessage = "Message with empty context";
            Component expectedComponent = Component.text("Message with empty context");

            // Capture the context argument
            ArgumentCaptor<Map<String, Object>> contextCaptor = ArgumentCaptor.forClass(Map.class);

            when(messageLoader.getRawMessage(eq(key), eq("en"), eq("en"))).thenReturn(rawMessage);
            when(templateProcessor.processTemplate(eq(rawMessage), contextCaptor.capture())).thenReturn(expectedComponent);

            Audience senderAudience = mock(Audience.class);
            when(audiences.sender(sender)).thenReturn(senderAudience);

            // Act
            messageService.sendMessage(sender, message);

            // Assert
            verify(audiences).sender(sender);
            verify(senderAudience).sendMessage(expectedComponent);

            // Verify empty context was passed
            Map<String, Object> capturedContext = contextCaptor.getValue();
            assertNotNull(capturedContext);
            assertTrue(capturedContext.isEmpty());
        }

        @Test
        @DisplayName("Should broadcast message to all players")
        void shouldBroadcastMessageToAllPlayers() {
            // Arrange
            Locale message = mock(Locale.class);
            Map<String, Object> context = new HashMap<>();
            context.put("param", "value");

            String key = "test.broadcast.key";
            when(message.getKey()).thenReturn(key);

            String rawMessage = "Broadcast message with {{ param }}";
            Component expectedComponent = Component.text("Broadcast message with value");

            when(messageLoader.getRawMessage(key, "en", "en")).thenReturn(rawMessage);
            when(templateProcessor.processTemplate(rawMessage, context)).thenReturn(expectedComponent);

            Audience allAudience = mock(Audience.class);
            when(audiences.all()).thenReturn(allAudience);

            // Act
            messageService.broadcastMessage(message, context);

            // Assert
            verify(audiences).all();
            verify(allAudience).sendMessage(expectedComponent);
        }

        @Test
        @DisplayName("Should broadcast message with empty context")
        void shouldBroadcastMessageWithEmptyContext() {
            // Arrange
            Locale message = mock(Locale.class);

            String key = "test.broadcast.empty.key";
            when(message.getKey()).thenReturn(key);

            String rawMessage = "Broadcast with empty context";
            Component expectedComponent = Component.text("Broadcast with empty context");

            // Capture the context argument
            ArgumentCaptor<Map<String, Object>> contextCaptor = ArgumentCaptor.forClass(Map.class);

            when(messageLoader.getRawMessage(eq(key), eq("en"), eq("en"))).thenReturn(rawMessage);
            when(templateProcessor.processTemplate(eq(rawMessage), contextCaptor.capture())).thenReturn(expectedComponent);

            Audience allAudience = mock(Audience.class);
            when(audiences.all()).thenReturn(allAudience);

            // Act
            messageService.broadcastMessage(message);

            // Assert
            verify(audiences).all();
            verify(allAudience).sendMessage(expectedComponent);

            // Verify empty context was passed
            Map<String, Object> capturedContext = contextCaptor.getValue();
            assertNotNull(capturedContext);
            assertTrue(capturedContext.isEmpty());
        }
    }

    @Nested
    @DisplayName("Locale Detection Tests")
    class LocaleDetectionTests {

        @ParameterizedTest
        @ValueSource(strings = {"en_US", "fr_FR", "de_DE"})
        @DisplayName("Should detect player locale")
        void shouldDetectPlayerLocale(String playerLocale) {
            // Arrange
            Player player = mock(Player.class);
            Locale message = mock(Locale.class);
            Map<String, Object> context = new HashMap<>();

            String key = "test.locale.key";
            when(message.getKey()).thenReturn(key);

            // Player locale should be converted to just the language part
            String expectedLocale = playerLocale.split("_")[0].toLowerCase(java.util.Locale.ROOT);

            when(player.getLocale()).thenReturn(playerLocale);

            String rawMessage = "Localized message";
            Component expectedComponent = Component.text("Localized message");

            when(messageLoader.getRawMessage(key, expectedLocale, "en")).thenReturn(rawMessage);
            when(templateProcessor.processTemplate(rawMessage, context)).thenReturn(expectedComponent);

            Audience playerAudience = mock(Audience.class);
            when(audiences.sender(player)).thenReturn(playerAudience);

            // Act
            messageService.sendMessage(player, message, context);

            // Assert
            verify(player).getLocale();
            verify(messageLoader).getRawMessage(key, expectedLocale, "en");
            verify(templateProcessor).processTemplate(rawMessage, context);
            verify(audiences).sender(player);
            verify(playerAudience).sendMessage(expectedComponent);
        }

        @Test
        @DisplayName("Should use default locale for null player locale")
        void shouldUseDefaultLocaleForNullPlayerLocale() {
            // Arrange
            Player player = mock(Player.class);
            Locale message = mock(Locale.class);
            Map<String, Object> context = new HashMap<>();

            String key = "test.null.locale.key";
            when(message.getKey()).thenReturn(key);

            when(player.getLocale()).thenReturn(null);

            String rawMessage = "Default locale message";
            Component expectedComponent = Component.text("Default locale message");

            when(messageLoader.getRawMessage(key, "en", "en")).thenReturn(rawMessage);
            when(templateProcessor.processTemplate(rawMessage, context)).thenReturn(expectedComponent);

            Audience playerAudience = mock(Audience.class);
            when(audiences.sender(player)).thenReturn(playerAudience);

            // Act
            messageService.sendMessage(player, message, context);

            // Assert
            verify(player).getLocale();
            verify(messageLoader).getRawMessage(key, "en", "en");
            verify(templateProcessor).processTemplate(rawMessage, context);
            verify(audiences).sender(player);
            verify(playerAudience).sendMessage(expectedComponent);
        }

        @Test
        @DisplayName("Should use default locale for empty player locale")
        void shouldUseDefaultLocaleForEmptyPlayerLocale() {
            // Arrange
            Player player = mock(Player.class);
            Locale message = mock(Locale.class);
            Map<String, Object> context = new HashMap<>();

            String key = "test.empty.locale.key";
            when(message.getKey()).thenReturn(key);

            when(player.getLocale()).thenReturn("");

            String rawMessage = "Default locale message";
            Component expectedComponent = Component.text("Default locale message");

            when(messageLoader.getRawMessage(key, "en", "en")).thenReturn(rawMessage);
            when(templateProcessor.processTemplate(rawMessage, context)).thenReturn(expectedComponent);

            Audience playerAudience = mock(Audience.class);
            when(audiences.sender(player)).thenReturn(playerAudience);

            // Act
            messageService.sendMessage(player, message, context);

            // Assert
            verify(player).getLocale();
            verify(messageLoader).getRawMessage(key, "en", "en");
            verify(templateProcessor).processTemplate(rawMessage, context);
            verify(audiences).sender(player);
            verify(playerAudience).sendMessage(expectedComponent);
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("Should shutdown and close audiences")
        void shouldShutdownAndCloseAudiences() {
            // Act
            messageService.shutdown();

            // Assert
            verify(audiences).close();
        }

        @Test
        @DisplayName("Should handle null audiences during shutdown")
        void shouldHandleNullAudiencesDuringShutdown() {
            // Arrange
            DefaultMessageService serviceWithNullAudiences = new DefaultMessageService(plugin, configService) {
                {
                    try {
                        java.lang.reflect.Field audiencesField = DefaultMessageService.class.getDeclaredField("audiences");
                        audiencesField.setAccessible(true);
                        audiencesField.set(this, null);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to set up test", e);
                    }
                }
            };

            // Act & Assert - Should not throw exception
            assertDoesNotThrow(serviceWithNullAudiences::shutdown);
        }
    }
}
