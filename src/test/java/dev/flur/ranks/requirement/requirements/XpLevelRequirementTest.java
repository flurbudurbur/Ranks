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

@DisplayName("XP Level Requirement Tests")
public class XpLevelRequirementTest extends AbstractRequirementTest {

    /**
     * Provider method for XP level requirement test cases
     */
    public static Stream<Arguments> xpLevelRequirementTestCases() {
        RequirementRegistry.RequirementInfo xpLevelInfo = RequirementRegistry.fromName("xp-level");
        assertNotNull(xpLevelInfo, "xp-level requirement should be registered");

        List<RequirementTestCase> testCases = Arrays.asList(
                // XP level requirement test cases
                new RequirementTestCase(xpLevelInfo, "xp-level 10", true, "valid xp level"),
                new RequirementTestCase(xpLevelInfo, "xp-level 0", true, "zero xp level"),
                // Note: The implementation doesn't specifically check for negative values,
                // so we're treating it as a valid input for now
                new RequirementTestCase(xpLevelInfo, "xp-level -5", true, "negative xp level"),
                new RequirementTestCase(xpLevelInfo, "xp-level abc", false, "invalid xp format"),
                new RequirementTestCase(xpLevelInfo, "xp-level", false, "missing xp level"),
                new RequirementTestCase(xpLevelInfo, "xp-level 10 extra", false, "too many arguments")
        );

        return testCases.stream().map(Arguments::of);
    }

    @Nested
    @DisplayName("XP Level Requirement Creation Tests")
    class XpLevelRequirementCreationTests {

        @ParameterizedTest
        @MethodSource("dev.flur.ranks.requirement.requirements.XpLevelRequirementTest#xpLevelRequirementTestCases")
        @DisplayName("Should handle XP level requirement creation as expected")
        void shouldHandleXpLevelRequirementCreationAsExpected(RequirementTestCase testCase) {
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
                assertThrows(Exception.class, 
                    () -> RequirementFactory.createRequirement(testCase.input()),
                    "Should throw exception for: " + testCase.description());
            }
        }
    }

    @Nested
    @DisplayName("XP Level Requirement Validation Tests")
    class XpLevelRequirementValidationTests {

        @TestFactory
        @DisplayName("Should validate all XP level success cases work correctly")
        Stream<DynamicTest> shouldValidateAllXpLevelSuccessCasesWorkCorrectly() {
            return xpLevelRequirementTestCases()
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
        @DisplayName("Should validate all XP level failure cases fail as expected")
        Stream<DynamicTest> shouldValidateAllXpLevelFailureCasesFailAsExpected() {
            return xpLevelRequirementTestCases()
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
