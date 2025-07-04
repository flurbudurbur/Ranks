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

@DisplayName("Item Use Requirement Tests")
public class ItemUseRequirementTest extends AbstractRequirementTest {

    /**
     * Provider method for item use requirement test cases
     */
    public static Stream<Arguments> itemUseRequirementTestCases() {
        RequirementRegistry.RequirementInfo itemUseInfo = RequirementRegistry.fromName("item-use");
        assertNotNull(itemUseInfo, "item-use requirement should be registered");

        List<RequirementTestCase> testCases = Arrays.asList(
                // Item use requirement test cases - valid formats
                new RequirementTestCase(itemUseInfo, "item-use STONE 10", true, "valid single item"),
                new RequirementTestCase(itemUseInfo, "item-use DIAMOND_SWORD 5", true, "valid single tool"),
                new RequirementTestCase(itemUseInfo, "item-use STONE DIAMOND_SWORD 15", true, "multiple items"),

                // Item use requirement test cases - invalid formats
                new RequirementTestCase(itemUseInfo, "item-use INVALID_ITEM 10", false, "invalid item material"),
                new RequirementTestCase(itemUseInfo, "item-use STONE abc", false, "invalid amount format"),
                new RequirementTestCase(itemUseInfo, "item-use STONE", false, "missing amount"),
                new RequirementTestCase(itemUseInfo, "item-use", false, "missing item and amount"),
                new RequirementTestCase(itemUseInfo, "item-use AIR 10", false, "non-item material")
        );

        return testCases.stream().map(Arguments::of);
    }

    @Nested
    @DisplayName("Item Use Requirement Creation Tests")
    class ItemUseRequirementCreationTests {

        @ParameterizedTest
        @MethodSource("dev.flur.ranks.requirement.requirements.ItemUseRequirementTest#itemUseRequirementTestCases")
        @DisplayName("Should handle item use requirement creation as expected")
        void shouldHandleItemUseRequirementCreationAsExpected(RequirementTestCase testCase) {
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
    @DisplayName("Item Use Requirement Validation Tests")
    class ItemUseRequirementValidationTests {

        @TestFactory
        @DisplayName("Should validate all item use success cases work correctly")
        Stream<DynamicTest> shouldValidateAllItemUseSuccessCasesWorkCorrectly() {
            return itemUseRequirementTestCases()
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
        @DisplayName("Should validate all item use failure cases fail as expected")
        Stream<DynamicTest> shouldValidateAllItemUseFailureCasesFailAsExpected() {
            return itemUseRequirementTestCases()
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
