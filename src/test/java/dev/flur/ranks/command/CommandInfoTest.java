package dev.flur.ranks.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Command Info Tests")
public class CommandInfoTest {

    @Test
    @DisplayName("Should retrieve command info from annotation")
    public void shouldRetrieveCommandInfoFromAnnotation() {
        // Get the CommandInfo annotation from the test class
        CommandInfo info = TestCommand.class.getAnnotation(CommandInfo.class);
        
        // Verify that the annotation is not null
        assertNotNull(info, "CommandInfo annotation should not be null");
        
        // Verify that the annotation values are correct
        assertEquals("test", info.name(), "Command name should be 'test'");
        assertEquals("test.permission", info.permission(), "Command permission should be 'test.permission'");
        assertEquals("Test command", info.description(), "Command description should be 'Test command'");
    }

    @Test
    @DisplayName("Should use default values when not specified")
    public void shouldUseDefaultValuesWhenNotSpecified() {
        // Get the CommandInfo annotation from the test class with defaults
        CommandInfo info = TestCommandWithDefaults.class.getAnnotation(CommandInfo.class);
        
        // Verify that the annotation is not null
        assertNotNull(info, "CommandInfo annotation should not be null");
        
        // Verify that the annotation values are correct
        assertEquals("test-defaults", info.name(), "Command name should be 'test-defaults'");
        assertEquals("", info.permission(), "Command permission should be empty by default");
        assertEquals("", info.description(), "Command description should be empty by default");
    }

    // Test class with CommandInfo annotation
    @CommandInfo(name = "test", permission = "test.permission", description = "Test command")
    private static class TestCommand extends BaseCommand {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
            return true;
        }
    }

    // Test class with CommandInfo annotation using default values
    @CommandInfo(name = "test-defaults")
    private static class TestCommandWithDefaults extends BaseCommand {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String[] args) {
            return true;
        }
    }
}