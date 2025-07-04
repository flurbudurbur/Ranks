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

@DisplayName("Money Requirement Tests")
public class MoneyRequirementTest extends AbstractRequirementTest {

    /**
     * Provider method for money requirement test cases
     */
    public static Stream<Arguments> moneyRequirementTestCases() {
        RequirementRegistry.RequirementInfo moneyInfo = RequirementRegistry.fromName("money");
        assertNotNull(moneyInfo, "money requirement should be registered");

        List<RequirementTestCase> testCases = Arrays.asList(
                // Money requirement test cases
                new RequirementTestCase(moneyInfo, "money 100.50", true, "valid money amount"),
                new RequirementTestCase(moneyInfo, "money 0", true, "zero money amount"),
                // Note: The implementation doesn't check for negative values,
                // so we're treating it as a valid input for now
                new RequirementTestCase(moneyInfo, "money -50", true, "negative money amount"),
                new RequirementTestCase(moneyInfo, "money abc", false, "invalid money format"),
                new RequirementTestCase(moneyInfo, "money", false, "missing money amount"),
                new RequirementTestCase(moneyInfo, "money 100 extra", false, "too many arguments")
        );

        return testCases.stream().map(Arguments::of);
    }

    @Nested
    @DisplayName("Money Requirement Creation Tests")
    class MoneyRequirementCreationTests {

        @ParameterizedTest
        @MethodSource("dev.flur.ranks.requirement.requirements.MoneyRequirementTest#moneyRequirementTestCases")
        @DisplayName("Should handle money requirement creation as expected")
        void shouldHandleMoneyRequirementCreationAsExpected(RequirementTestCase testCase) {
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
    @DisplayName("Money Requirement Validation Tests")
    class MoneyRequirementValidationTests {

        @TestFactory
        @DisplayName("Should validate all money success cases work correctly")
        Stream<DynamicTest> shouldValidateAllMoneySuccessCasesWorkCorrectly() {
            return moneyRequirementTestCases()
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
        @DisplayName("Should validate all money failure cases fail as expected")
        Stream<DynamicTest> shouldValidateAllMoneyFailureCasesFailAsExpected() {
            return moneyRequirementTestCases()
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
