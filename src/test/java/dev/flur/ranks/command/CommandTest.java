package dev.flur.ranks.command;

import dev.flur.ranks.command.commands.RanksCommandTest;
import dev.flur.ranks.command.commands.RankupCommandTest;
import dev.flur.ranks.command.commands.RequirementsCommandTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

/**
 * Main command test class that serves as a coordinator for all command tests.
 * The actual tests have been divided into logical files:
 * <p>
 * - AnnotationCommandManagerTest.java: Tests for the command manager
 * - BaseCommandTest.java: Tests for the base command class
 * - CommandInfoTest.java: Tests for the command info annotation
 * - RanksCommandTest.java: Tests for the ranks command
 * - RankupCommandTest.java: Tests for the rankup command
 * - RequirementsCommandTest.java: Tests for the requirements command
 * </p>
 */
@DisplayName("Command Tests")
public class CommandTest {

    @Nested
    @DisplayName("Annotation Command Manager Tests")
    class AnnotationCommandManagerTests extends AnnotationCommandManagerTest {}

    @Nested
    @DisplayName("Base Command Tests")
    class BaseCommandTests extends BaseCommandTest {}

    @Nested
    @DisplayName("Command Info Tests")
    class CommandInfoTests extends CommandInfoTest {}

    @Nested
    @DisplayName("Ranks Command Tests")
    class RanksCommandTests extends RanksCommandTest {}

    @Nested
    @DisplayName("Rankup Command Tests")
    class RankupCommandTests extends RankupCommandTest {}

    @Nested
    @DisplayName("Requirements Command Tests")
    class RequirementsCommandTests extends RequirementsCommandTest {}
}
