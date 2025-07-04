package dev.flur.ranks.requirement;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.vault.VaultProvider;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * Base class for requirement tests with common functionality
 */
public abstract class AbstractRequirementTest {

    @Mock
    protected OfflinePlayer mockPlayer;

    @Mock
    protected org.bukkit.entity.Player mockPlayerEntity;

    @Mock
    protected VaultProvider mockVaultProvider;

    @Mock
    protected Economy mockEconomy;

    @Mock
    protected Permission mockPermission;

    // Store the original VaultProvider to restore it after tests
    private VaultProvider originalVaultProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Store the original VaultProvider
        originalVaultProvider = Ranks.getVaultProvider();

        // Mock player.getPlayer() to return mockPlayerEntity
        when(mockPlayer.getPlayer()).thenReturn(mockPlayerEntity);

        // Mock other common methods that might be needed
        when(mockPlayerEntity.getLevel()).thenReturn(10);
        when(mockPlayerEntity.getStatistic(any(org.bukkit.Statistic.class))).thenReturn(3600);

        // Mock VaultProvider methods
        when(mockVaultProvider.getEconomy()).thenReturn(mockEconomy);
        when(mockVaultProvider.getPermissions()).thenReturn(mockPermission);

        // Mock Economy methods
        when(mockEconomy.getBalance(any(OfflinePlayer.class))).thenReturn(1000.0);

        // Set the mock VaultProvider
        Ranks.setVaultProvider(mockVaultProvider);
    }

    @AfterEach
    void tearDown() {
        // Restore the original VaultProvider
        Ranks.setVaultProvider(originalVaultProvider);
    }

    /**
         * Test case data for each requirement type with expected outcomes
         */
        public record RequirementTestCase(RequirementRegistry.RequirementInfo requirementInfo, String input,
                                          boolean shouldSucceed, String description) {

        @Override
            public String toString() {
                return requirementInfo.name() + " - " + description;
            }
        }

    /**
     * Validates that all registered requirements are covered in the test cases
     */
    protected static void validateAllRequirementsCovered(List<RequirementTestCase> testCases) {
        Collection<RequirementRegistry.RequirementInfo> allRequirements = RequirementRegistry.getAllRequirements();

        // Get unique types from test cases
        List<RequirementRegistry.RequirementInfo> coveredRequirements = testCases.stream()
                .map(RequirementTestCase::requirementInfo)
                .distinct()
                .toList();

        // Check if all registered requirements are covered
        for (RequirementRegistry.RequirementInfo info : allRequirements) {
            if (!coveredRequirements.contains(info)) {
                throw new IllegalStateException(
                        "Requirement " + info.name() + " is not covered in test cases. " +
                                "Please add test cases for this requirement type."
                );
            }
        }

        // Check if there are extra types (shouldn't happen but good to verify)
        for (RequirementRegistry.RequirementInfo info : coveredRequirements) {
            if (!allRequirements.contains(info)) {
                throw new IllegalStateException(
                        "Test case contains unknown requirement: " + info.name()
                );
            }
        }

        System.out.println("✓ All " + allRequirements.size() + " registered requirements are covered in test cases");
    }

    /**
     * Get test cases for a specific requirement type
     */
    protected static Stream<RequirementTestCase> getTestCasesForRequirement(Stream<RequirementTestCase> allTestCases, RequirementRegistry.RequirementInfo requirementInfo) {
        return allTestCases.filter(testCase -> testCase.requirementInfo().equals(requirementInfo));
    }
}