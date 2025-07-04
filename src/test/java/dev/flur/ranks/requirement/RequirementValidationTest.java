package dev.flur.ranks.requirement;

import dev.flur.ranks.requirement.requirements.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Requirement Validation Tests")
public class RequirementValidationTest extends AbstractRequirementTest {

    /**
     * Combines all test cases from individual requirement test classes
     */
    public Stream<RequirementTestCase> getAllRequirementTestCases() {
        return Stream.of(
                MoneyRequirementTest.moneyRequirementTestCases(),
                XpLevelRequirementTest.xpLevelRequirementTestCases(),
                TimeSinceDeathRequirementTest.timeSinceDeathRequirementTestCases(),
                BlockBreakRequirementTest.blockBreakRequirementTestCases(),
                ItemUseRequirementTest.itemUseRequirementTestCases()
        ).flatMap(stream -> stream.map(args -> (RequirementTestCase) args.get()[0]));
    }

    @Nested
    @DisplayName("Requirement Type Coverage Tests")
    class RequirementTypeCoverageTests {

        @Test
        @DisplayName("Should have consistent registry count")
        void shouldHaveConsistentRegistryCount() {
            Collection<RequirementRegistry.RequirementInfo> allRequirements = RequirementRegistry.getAllRequirements();
            List<RequirementTestCase> testCases = getAllRequirementTestCases().toList();

            List<RequirementRegistry.RequirementInfo> uniqueInfosInTests = testCases.stream()
                    .map(RequirementTestCase::requirementInfo)
                    .distinct()
                    .toList();

            assertEquals(allRequirements.size(), uniqueInfosInTests.size(),
                    "Number of unique requirement types in test cases should match registry count");
        }

        @ParameterizedTest
        @MethodSource("dev.flur.ranks.requirement.RequirementValidationTest#getAllRequirementInfos")
        @DisplayName("Should have test cases for each registered requirement")
        void shouldHaveTestCasesForEachRegisteredRequirement(RequirementRegistry.RequirementInfo info) {
            List<RequirementTestCase> testCases = getAllRequirementTestCases()
                    .filter(testCase -> testCase.requirementInfo().equals(info))
                    .toList();

            assertFalse(testCases.isEmpty(),
                    "Should have at least one test case for " + info.name());
        }

        @Test
        @DisplayName("Should validate all registered requirements are covered")
        void shouldValidateAllRegisteredRequirementsCovered() {
            List<RequirementTestCase> testCases = getAllRequirementTestCases().toList();

            // This will throw an exception if any requirement is not covered
            validateAllRequirementsCovered(testCases);

            // If we get here, all requirements are covered
            assertTrue(true, "All registered requirements are covered in test cases");
        }
    }

    /**
     * Provider method for all requirement infos
     */
    public static Stream<RequirementRegistry.RequirementInfo> getAllRequirementInfos() {
        return RequirementRegistry.getAllRequirements().stream();
    }

    @Nested
    @DisplayName("Success and Failure Case Validation")
    class SuccessAndFailureCaseValidation {

        @ParameterizedTest
        @MethodSource("dev.flur.ranks.requirement.RequirementValidationTest#getAllRequirementInfos")
        @DisplayName("Should have both success and failure cases for each type")
        void shouldHaveBothSuccessAndFailureCasesForEachType(RequirementRegistry.RequirementInfo info) {
            List<RequirementTestCase> typeCases = getAllRequirementTestCases()
                    .filter(testCase -> testCase.requirementInfo().equals(info))
                    .toList();

            // Verify we have both success and failure cases
            boolean hasSuccess = typeCases.stream().anyMatch(RequirementTestCase::shouldSucceed);
            boolean hasFailure = typeCases.stream().anyMatch(tc -> !tc.shouldSucceed());

            assertTrue(hasSuccess,
                    "Should have at least one success case for " + info.name());
            assertTrue(hasFailure,
                    "Should have at least one failure case for " + info.name());
        }
    }
}
