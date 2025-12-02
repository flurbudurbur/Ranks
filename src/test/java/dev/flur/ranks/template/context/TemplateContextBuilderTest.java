package dev.flur.ranks.template.context;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TemplateContextBuilder.
 */
class TemplateContextBuilderTest {

    private ServerMock server;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Nested
    @DisplayName("Fluent API")
    class FluentAPI {

        @Test
        @DisplayName("create returns new builder")
        void create_ReturnsNewBuilder() {
            TemplateContextBuilder builder = TemplateContextBuilder.create();
            assertNotNull(builder);
        }

        @Test
        @DisplayName("with adds single value")
        void with_AddsValue() {
            Map<String, Object> context = TemplateContextBuilder.create()
                    .with("key", "value")
                    .build();

            assertEquals("value", context.get("key"));
        }

        @Test
        @DisplayName("with allows chaining")
        void with_Chained_AddsAllValues() {
            Map<String, Object> context = TemplateContextBuilder.create()
                    .with("name", "Steve")
                    .with("rank", "Knight")
                    .with("level", 10)
                    .build();

            assertEquals("Steve", context.get("name"));
            assertEquals("Knight", context.get("rank"));
            assertEquals(10, context.get("level"));
        }

        @Test
        @DisplayName("withAll adds all map entries")
        void withAll_AddsAllEntries() {
            Map<String, Object> values = new HashMap<>();
            values.put("a", 1);
            values.put("b", 2);

            Map<String, Object> context = TemplateContextBuilder.create()
                    .withAll(values)
                    .build();

            assertEquals(1, context.get("a"));
            assertEquals(2, context.get("b"));
        }

        @Test
        @DisplayName("withIfPresent adds non-null value")
        void withIfPresent_NonNull_AddsValue() {
            Map<String, Object> context = TemplateContextBuilder.create()
                    .withIfPresent("key", "value")
                    .build();

            assertEquals("value", context.get("key"));
        }

        @Test
        @DisplayName("withIfPresent skips null value")
        void withIfPresent_Null_SkipsValue() {
            Map<String, Object> context = TemplateContextBuilder.create()
                    .withIfPresent("key", null)
                    .build();

            assertFalse(context.containsKey("key"));
        }

        @Test
        @DisplayName("build returns copy of context")
        void build_ReturnsCopy() {
            TemplateContextBuilder builder = TemplateContextBuilder.create()
                    .with("key", "original");

            Map<String, Object> context1 = builder.build();
            context1.put("key", "modified");

            Map<String, Object> context2 = builder.build();
            assertEquals("original", context2.get("key"));
        }

        @Test
        @DisplayName("getContext returns mutable view")
        void getContext_ReturnsMutableView() {
            TemplateContextBuilder builder = TemplateContextBuilder.create()
                    .with("key", "value");

            Map<String, Object> context = builder.getContext();
            context.put("newKey", "newValue");

            Map<String, Object> built = builder.build();
            assertEquals("newValue", built.get("newKey"));
        }
    }

    @Nested
    @DisplayName("Player Context")
    class PlayerContextTests {

        @Test
        @DisplayName("withPlayer adds player context")
        void withPlayer_AddsPlayerContext() {
            PlayerMock player = server.addPlayer("TestPlayer");

            Map<String, Object> context = TemplateContextBuilder.create()
                    .withPlayer(player)
                    .build();

            assertNotNull(context.get("player"));
            assertTrue(context.get("player") instanceof MinecraftContext.PlayerContext);
        }

        @Test
        @DisplayName("withPlayer adds convenience properties")
        void withPlayer_AddsConvenienceProperties() {
            PlayerMock player = server.addPlayer("Steve");

            Map<String, Object> context = TemplateContextBuilder.create()
                    .withPlayer(player)
                    .build();

            assertEquals("Steve", context.get("playerName"));
            assertNotNull(context.get("playerDisplayName"));
            assertNotNull(context.get("playerUuid"));
        }

        @Test
        @DisplayName("withPlayer with custom key")
        void withPlayer_CustomKey_AddsWithKey() {
            PlayerMock player = server.addPlayer("Target");

            Map<String, Object> context = TemplateContextBuilder.create()
                    .withPlayer("target", player)
                    .build();

            assertNotNull(context.get("target"));
            assertTrue(context.get("target") instanceof MinecraftContext.PlayerContext);
        }
    }

    @Nested
    @DisplayName("Location Context")
    class LocationContextTests {

        @Test
        @DisplayName("withLocation adds location context")
        void withLocation_AddsLocationContext() {
            World world = server.addSimpleWorld("world");
            Location location = new Location(world, 100, 64, 200);

            Map<String, Object> context = TemplateContextBuilder.create()
                    .withLocation("spawn", location)
                    .build();

            assertNotNull(context.get("spawn"));
            assertTrue(context.get("spawn") instanceof MinecraftContext.LocationContext);
        }

        @Test
        @DisplayName("withLocationExpanded adds expanded properties")
        void withLocationExpanded_AddsExpandedProperties() {
            World world = server.addSimpleWorld("world");
            Location location = new Location(world, 100, 64, 200);

            Map<String, Object> context = TemplateContextBuilder.create()
                    .withLocationExpanded("spawn", location)
                    .build();

            assertNotNull(context.get("spawn"));
            assertEquals("world", context.get("spawnWorld"));
            assertEquals(100, context.get("spawnX"));
            assertEquals(64, context.get("spawnY"));
            assertEquals(200, context.get("spawnZ"));
        }
    }

    @Nested
    @DisplayName("Rank Context")
    class RankContextTests {

        @Test
        @DisplayName("withRank adds rank name")
        void withRank_AddsRankName() {
            Map<String, Object> context = TemplateContextBuilder.create()
                    .withRank("currentRank", "Knight")
                    .build();

            assertEquals("Knight", context.get("currentRank"));
        }
    }

    @Nested
    @DisplayName("Locale Context")
    class LocaleContextTests {

        @Test
        @DisplayName("withLocale adds locale")
        void withLocale_AddsLocale() {
            Map<String, Object> context = TemplateContextBuilder.create()
                    .withLocale("es")
                    .build();

            assertEquals("es", context.get("locale"));
        }
    }
}
