package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.AbstractRequirementTest;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.requirement.RequirementFactory;
import dev.flur.ranks.requirement.RequirementRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Block Break Requirement Tests")
public class BlockBreakRequirementTest extends AbstractRequirementTest {

    /**
     * Provider method for block break requirement test cases
     */
    public static Stream<Arguments> blockBreakRequirementTestCases() {
        RequirementRegistry.RequirementInfo blockBreakInfo = RequirementRegistry.fromName("block-break");
        assertNotNull(blockBreakInfo, "block-break requirement should be registered");

        List<RequirementTestCase> testCases = Arrays.asList(
                // Block break requirement test cases
                new RequirementTestCase(blockBreakInfo, "block-break STONE 10", true, "valid block and amount"),
                new RequirementTestCase(blockBreakInfo, "block-break STONE DIRT 10", true, "multiple blocks"),
                new RequirementTestCase(blockBreakInfo, "block-break STONE 0", true, "zero amount"),
                new RequirementTestCase(blockBreakInfo, "block-break INVALID_BLOCK 10", false, "invalid block material"),
                new RequirementTestCase(blockBreakInfo, "block-break STONE abc", false, "invalid amount format"),
                new RequirementTestCase(blockBreakInfo, "block-break STONE", false, "missing amount"),
                new RequirementTestCase(blockBreakInfo, "block-break", false, "missing block and amount"),
                new RequirementTestCase(blockBreakInfo, "block-break DIAMOND_SWORD 10", false, "non-block material")
        );

        return testCases.stream().map(Arguments::of);
    }

    @Nested
    @DisplayName("Block Break Requirement Creation Tests")
    class BlockBreakRequirementCreationTests {

        @ParameterizedTest
        @MethodSource("dev.flur.ranks.requirement.requirements.BlockBreakRequirementTest#blockBreakRequirementTestCases")
        @DisplayName("Should handle block break requirement creation as expected")
        void shouldHandleBlockBreakRequirementCreationAsExpected(RequirementTestCase testCase) {
            if (testCase.shouldSucceed()) {
                // Expected to succeed
                assertDoesNotThrow(() -> {
                    Requirement requirement = RequirementFactory.createRequirement(testCase.input());
                    assertNotNull(requirement, "Requirement should be created for: " + testCase.description());
                    assertEquals(testCase.requirementInfo().requirementClass(), requirement.getClass(),
                            "Should create correct requirement class for: " + testCase.description());

                    // Verify name retrieval
                    String name = RequirementFactory.getRequirementName(requirement);
                    assertEquals(testCase.requirementInfo().name(), name,
                            "Should return correct name for: " + testCase.description());
                }, "Should succeed for: " + testCase.description());
            } else {
                // Expected to fail
                assertThrows(Exception.class, () -> RequirementFactory.createRequirement(testCase.input()),
                        "Should throw exception for: " + testCase.description());
            }
        }
    }

    @Nested
    @DisplayName("Block Break Requirement Validation Tests")
    class BlockBreakRequirementValidationTests {

        @TestFactory
        @DisplayName("Should validate all block break success cases work correctly")
        Stream<DynamicTest> shouldValidateAllBlockBreakSuccessCasesWorkCorrectly() {
            return blockBreakRequirementTestCases()
                    .map(args -> (RequirementTestCase) args.get()[0])
                    .filter(RequirementTestCase::shouldSucceed)
                    .map(testCase -> DynamicTest.dynamicTest(
                            "Success: " + testCase,
                            () -> {
                                Requirement requirement = RequirementFactory.createRequirement(testCase.input());
                                assertNotNull(requirement, "Requirement should be created");

                                // Test string representation
                                String stringRep = requirement.toString();
                                assertNotNull(stringRep, "String representation should not be null");
                                assertFalse(stringRep.isEmpty(), "String representation should not be empty");

                                // Test requirement validation (may fail due to mocking, but shouldn't throw)
                                assertDoesNotThrow(() -> requirement.meetsRequirement(mockPlayer), 
                                "Should not throw when checking requirement");
                            }
                    ));
        }

        @TestFactory
        @DisplayName("Should validate all block break failure cases fail as expected")
        Stream<DynamicTest> shouldValidateAllBlockBreakFailureCasesFailAsExpected() {
            return blockBreakRequirementTestCases()
                    .map(args -> (RequirementTestCase) args.get()[0])
                    .filter(testCase -> !testCase.shouldSucceed())
                    .map(testCase -> DynamicTest.dynamicTest(
                            "Failure: " + testCase,
                            () -> assertThrows(Exception.class, 
                                    () -> RequirementFactory.createRequirement(testCase.input()),
                                    "Should throw exception for: " + testCase.description())
                    ));
        }
    }
}
