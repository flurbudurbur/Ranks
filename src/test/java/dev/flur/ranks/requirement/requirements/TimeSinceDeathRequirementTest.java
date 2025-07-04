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

@DisplayName("Time Since Death Requirement Tests")
public class TimeSinceDeathRequirementTest extends AbstractRequirementTest {

    /**
     * Provider method for time since death requirement test cases
     */
    public static Stream<Arguments> timeSinceDeathRequirementTestCases() {
        RequirementRegistry.RequirementInfo timeSinceDeathInfo = RequirementRegistry.fromName("time-since-death");
        assertNotNull(timeSinceDeathInfo, "time-since-death requirement should be registered");

        List<RequirementTestCase> testCases = Arrays.asList(
                // TIME_SINCE_DEATH requirement test cases - valid formats
                new RequirementTestCase(timeSinceDeathInfo, "time-since-death h12", true, "valid time in hours"),
                new RequirementTestCase(timeSinceDeathInfo, "time-since-death m30", true, "valid time in minutes"),
                new RequirementTestCase(timeSinceDeathInfo, "time-since-death s45", true, "valid time in seconds"),
                new RequirementTestCase(timeSinceDeathInfo, "time-since-death d1h2m30", true, "multiple time units"),
                new RequirementTestCase(timeSinceDeathInfo, "time-since-death M1w1d1h1m1s1", true, "all time units"),

                // TIME_SINCE_DEATH requirement test cases - invalid formats
                new RequirementTestCase(timeSinceDeathInfo, "time-since-death -1h", false, "negative time"),
                new RequirementTestCase(timeSinceDeathInfo, "time-since-death abc", false, "invalid time format"),
                new RequirementTestCase(timeSinceDeathInfo, "time-since-death", false, "missing time value"),
                new RequirementTestCase(timeSinceDeathInfo, "time-since-death invalid_unit", false, "completely invalid unit"),
                new RequirementTestCase(timeSinceDeathInfo, "time-since-death h12 extra", false, "too many arguments")
        );

        return testCases.stream().map(Arguments::of);
    }

    @Nested
    @DisplayName("Time Since Death Requirement Creation Tests")
    class TimeSinceDeathRequirementCreationTests {

        @ParameterizedTest
        @MethodSource("dev.flur.ranks.requirement.requirements.TimeSinceDeathRequirementTest#timeSinceDeathRequirementTestCases")
        @DisplayName("Should handle time since death requirement creation as expected")
        void shouldHandleTimeSinceDeathRequirementCreationAsExpected(RequirementTestCase testCase) {
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
    @DisplayName("Time Since Death Requirement Validation Tests")
    class TimeSinceDeathRequirementValidationTests {

        @TestFactory
        @DisplayName("Should validate all time since death success cases work correctly")
        Stream<DynamicTest> shouldValidateAllTimeSinceDeathSuccessCasesWorkCorrectly() {
            return timeSinceDeathRequirementTestCases()
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
        @DisplayName("Should validate all time since death failure cases fail as expected")
        Stream<DynamicTest> shouldValidateAllTimeSinceDeathFailureCasesFailAsExpected() {
            return timeSinceDeathRequirementTestCases()
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
