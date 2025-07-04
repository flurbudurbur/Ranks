package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.requirement.AbstractRequirementTest;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.requirement.RequirementFactory;
import dev.flur.ranks.requirement.RequirementRegistry;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Tests for the ItemUseRequirement class.
 */
@DisplayName("Item Use Requirement Tests")
public class ItemUseRequirementTest extends AbstractRequirementTest {

    @Test
    @DisplayName("Player has used more items than required should pass")
    void playerHasUsedMoreItemsThanRequiredShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("item-use");
        assertNotNull(info, "Item Use requirement should be registered");
        when(mockPlayerEntity.getStatistic(eq(Statistic.USE_ITEM), eq(Material.DIAMOND_SWORD))).thenReturn(20);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("item-use DIAMOND_SWORD 10");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player with 20 DIAMOND_SWORD uses should meet requirement of 10");
    }

    @Test
    @DisplayName("Player has used exactly the required number of items should pass")
    void playerHasUsedExactlyRequiredNumberOfItemsShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("item-use");
        assertNotNull(info, "Item Use requirement should be registered");
        when(mockPlayerEntity.getStatistic(eq(Statistic.USE_ITEM), eq(Material.DIAMOND_SWORD))).thenReturn(20);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("item-use DIAMOND_SWORD 20");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player with 20 DIAMOND_SWORD uses should meet requirement of 20");
    }

    @Test
    @DisplayName("Player has used multiple item types should pass")
    void playerHasUsedMultipleItemTypesShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("item-use");
        assertNotNull(info, "Item Use requirement should be registered");
        when(mockPlayerEntity.getStatistic(eq(Statistic.USE_ITEM), eq(Material.DIAMOND_SWORD))).thenReturn(20);
        when(mockPlayerEntity.getStatistic(eq(Statistic.USE_ITEM), eq(Material.BOW))).thenReturn(10);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("item-use DIAMOND_SWORD BOW 15");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player with 20 DIAMOND_SWORD and 10 BOW uses (30 total) should meet requirement of 15");
    }

    @Test
    @DisplayName("Player has used fewer items than required should fail")
    void playerHasUsedFewerItemsThanRequiredShouldFail() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("item-use");
        assertNotNull(info, "Item Use requirement should be registered");
        when(mockPlayerEntity.getStatistic(eq(Statistic.USE_ITEM), eq(Material.DIAMOND_SWORD))).thenReturn(20);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("item-use DIAMOND_SWORD 30");

        // Verify
        assertFalse(requirement.meetsRequirement(mockPlayer),
                "Player with 20 DIAMOND_SWORD uses should not meet requirement of 30");
    }

    @Test
    @DisplayName("Player has used no items of the required type should fail")
    void playerHasUsedNoItemsOfRequiredTypeShouldFail() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("item-use");
        assertNotNull(info, "Item Use requirement should be registered");
        when(mockPlayerEntity.getStatistic(eq(Statistic.USE_ITEM), eq(Material.GOLDEN_APPLE))).thenReturn(0);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("item-use GOLDEN_APPLE 5");

        // Verify
        assertFalse(requirement.meetsRequirement(mockPlayer),
                "Player with 0 GOLDEN_APPLE uses should not meet requirement of 5");
    }

    @Test
    @DisplayName("Invalid item material should be invalid")
    void invalidItemMaterialShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("item-use");
        assertNotNull(info, "Item Use requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("item-use INVALID_MATERIAL 10");
        }, "Invalid item material should not be valid");
    }

    @Test
    @DisplayName("Non-item material should be invalid")
    void nonItemMaterialShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("item-use");
        assertNotNull(info, "Item Use requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("item-use STONE 10");
        }, "Non-item material should not be valid");
    }

    @Test
    @DisplayName("Invalid amount format should be invalid")
    void invalidAmountFormatShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("item-use");
        assertNotNull(info, "Item Use requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("item-use DIAMOND_SWORD abc");
        }, "Non-numeric amount should not be valid");
    }

    @Test
    @DisplayName("Missing amount parameter should be invalid")
    void missingAmountParameterShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("item-use");
        assertNotNull(info, "Item Use requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("item-use DIAMOND_SWORD");
        }, "Missing amount parameter should not be valid");
    }

    @Test
    @DisplayName("Missing item parameter should be invalid")
    void missingItemParameterShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("item-use");
        assertNotNull(info, "Item Use requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("item-use 10");
        }, "Missing item parameter should not be valid");
    }

    @Test
    @DisplayName("Too many items should be invalid")
    void tooManyItemsShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("item-use");
        assertNotNull(info, "Item Use requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("item-use DIAMOND_SWORD IRON_SWORD GOLDEN_SWORD STONE_SWORD WOODEN_SWORD BOW CROSSBOW TRIDENT SHIELD FISHING_ROD 10");
        }, "Too many items should not be valid (exceeds maximum of 10 parameters)");
    }

    @Test
    @DisplayName("Item Use requirement should be registered")
    void shouldBeRegistered() {
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("item-use");
        assertNotNull(info, "Item Use requirement should be registered");
        assertEquals(ItemUseRequirement.class, info.requirementClass(), 
                "Item Use requirement should be registered with correct class");
    }

    @Test
    @DisplayName("Item Use requirement should validate parameters")
    void shouldValidateParameters() {
        // Valid parameters - single item
        assertDoesNotThrow(() -> new ItemUseRequirement(new String[] { "DIAMOND_SWORD", "10" }), 
                "Valid item and amount should not throw exception");

        // Valid parameters - multiple items
        assertDoesNotThrow(() -> new ItemUseRequirement(new String[] { "DIAMOND_SWORD", "BOW", "10" }), 
                "Multiple valid items and amount should not throw exception");

        // Invalid parameters - missing item
        assertThrows(IllegalArgumentException.class, 
                () -> new ItemUseRequirement(new String[] { "10" }), 
                "Missing item should throw exception");

        // Invalid parameters - missing amount
        assertThrows(IllegalArgumentException.class, 
                () -> new ItemUseRequirement(new String[] { "DIAMOND_SWORD" }), 
                "Missing amount should throw exception");

        // Invalid parameters - invalid item material
        assertThrows(IllegalArgumentException.class, 
                () -> new ItemUseRequirement(new String[] { "INVALID_MATERIAL", "10" }), 
                "Invalid item material should throw exception");

        // Invalid parameters - non-item material
        assertThrows(IllegalArgumentException.class, 
                () -> new ItemUseRequirement(new String[] { "GRASS_BLOCK", "10" }),
                "Non-item material should throw exception");

        // Invalid parameters - non-numeric amount
        assertThrows(IllegalArgumentException.class, 
                () -> new ItemUseRequirement(new String[] { "DIAMOND_SWORD", "abc" }), 
                "Non-numeric amount should throw exception");

        // Invalid parameters - too many items
        String[] tooManyItems = new String[] { 
            "DIAMOND_SWORD", "IRON_SWORD", "GOLDEN_SWORD", "STONE_SWORD", "WOODEN_SWORD", 
            "BOW", "CROSSBOW", "TRIDENT", "SHIELD", "FISHING_ROD", "10" 
        };
        assertThrows(IllegalArgumentException.class, 
                () -> new ItemUseRequirement(tooManyItems), 
                "Too many items should throw exception");
    }

    @Test
    @DisplayName("Item Use requirement should check item use statistics correctly")
    void shouldCheckItemUseStatisticsCorrectly() {
        // Setup mock statistics
        when(mockPlayerEntity.getStatistic(eq(Statistic.USE_ITEM), eq(Material.DIAMOND_SWORD))).thenReturn(20);
        when(mockPlayerEntity.getStatistic(eq(Statistic.USE_ITEM), eq(Material.BOW))).thenReturn(10);
        when(mockPlayerEntity.getStatistic(eq(Statistic.USE_ITEM), eq(Material.GOLDEN_APPLE))).thenReturn(0);

        // Single item, requirement met
        ItemUseRequirement singleItemMet = new ItemUseRequirement(new String[] { "DIAMOND_SWORD", "10" });
        assertTrue(singleItemMet.meetsRequirement(mockPlayer), 
                "Player with 20 DIAMOND_SWORD uses should meet requirement of 10");

        // Single item, requirement exactly met
        ItemUseRequirement singleItemExact = new ItemUseRequirement(new String[] { "DIAMOND_SWORD", "20" });
        assertTrue(singleItemExact.meetsRequirement(mockPlayer), 
                "Player with 20 DIAMOND_SWORD uses should meet requirement of 20");

        // Single item, requirement not met
        ItemUseRequirement singleItemNotMet = new ItemUseRequirement(new String[] { "DIAMOND_SWORD", "30" });
        assertFalse(singleItemNotMet.meetsRequirement(mockPlayer), 
                "Player with 20 DIAMOND_SWORD uses should not meet requirement of 30");

        // Multiple items, requirement met
        ItemUseRequirement multipleItemsMet = new ItemUseRequirement(new String[] { "DIAMOND_SWORD", "BOW", "15" });
        assertTrue(multipleItemsMet.meetsRequirement(mockPlayer), 
                "Player with 20 DIAMOND_SWORD and 10 BOW uses (30 total) should meet requirement of 15");

        // Multiple items, requirement not met
        ItemUseRequirement multipleItemsNotMet = new ItemUseRequirement(new String[] { "DIAMOND_SWORD", "BOW", "35" });
        assertFalse(multipleItemsNotMet.meetsRequirement(mockPlayer), 
                "Player with 20 DIAMOND_SWORD and 10 BOW uses (30 total) should not meet requirement of 35");

        // Item with zero uses, requirement not met
        ItemUseRequirement zeroUsesMet = new ItemUseRequirement(new String[] { "GOLDEN_APPLE", "5" });
        assertFalse(zeroUsesMet.meetsRequirement(mockPlayer), 
                "Player with 0 GOLDEN_APPLE uses should not meet requirement of 5");
    }
}
