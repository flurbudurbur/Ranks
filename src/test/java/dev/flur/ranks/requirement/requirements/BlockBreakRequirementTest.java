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
 * Tests for the BlockBreakRequirement class.
 */
@DisplayName("Block Break Requirement Tests")
public class BlockBreakRequirementTest extends AbstractRequirementTest {

    @Test
    @DisplayName("Player has broken more blocks than required should pass")
    void playerHasBrokenMoreBlocksThanRequiredShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("block-break");
        assertNotNull(info, "Block Break requirement should be registered");
        when(mockPlayerEntity.getStatistic(eq(Statistic.MINE_BLOCK), eq(Material.STONE))).thenReturn(20);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("block-break STONE 10");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player with 20 STONE blocks broken should meet requirement of 10");
    }

    @Test
    @DisplayName("Player has broken exactly the required number of blocks should pass")
    void playerHasBrokenExactlyRequiredNumberOfBlocksShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("block-break");
        assertNotNull(info, "Block Break requirement should be registered");
        when(mockPlayerEntity.getStatistic(eq(Statistic.MINE_BLOCK), eq(Material.STONE))).thenReturn(20);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("block-break STONE 20");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player with 20 STONE blocks broken should meet requirement of 20");
    }

    @Test
    @DisplayName("Player has broken multiple block types should pass")
    void playerHasBrokenMultipleBlockTypesShouldPass() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("block-break");
        assertNotNull(info, "Block Break requirement should be registered");
        when(mockPlayerEntity.getStatistic(eq(Statistic.MINE_BLOCK), eq(Material.STONE))).thenReturn(20);
        when(mockPlayerEntity.getStatistic(eq(Statistic.MINE_BLOCK), eq(Material.DIRT))).thenReturn(10);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("block-break STONE DIRT 15");

        // Verify
        assertTrue(requirement.meetsRequirement(mockPlayer),
                "Player with 20 STONE and 10 DIRT blocks broken (30 total) should meet requirement of 15");
    }

    @Test
    @DisplayName("Player has broken fewer blocks than required should fail")
    void playerHasBrokenFewerBlocksThanRequiredShouldFail() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("block-break");
        assertNotNull(info, "Block Break requirement should be registered");
        when(mockPlayerEntity.getStatistic(eq(Statistic.MINE_BLOCK), eq(Material.STONE))).thenReturn(20);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("block-break STONE 30");

        // Verify
        assertFalse(requirement.meetsRequirement(mockPlayer),
                "Player with 20 STONE blocks broken should not meet requirement of 30");
    }

    @Test
    @DisplayName("Player has broken no blocks of the required type should fail")
    void playerHasBrokenNoBlocksOfRequiredTypeShouldFail() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("block-break");
        assertNotNull(info, "Block Break requirement should be registered");
        when(mockPlayerEntity.getStatistic(eq(Statistic.MINE_BLOCK), eq(Material.DIAMOND_BLOCK))).thenReturn(0);

        // Create requirement
        Requirement requirement = RequirementFactory.createRequirement("block-break DIAMOND_BLOCK 5");

        // Verify
        assertFalse(requirement.meetsRequirement(mockPlayer),
                "Player with 0 DIAMOND_BLOCK blocks broken should not meet requirement of 5");
    }

    @Test
    @DisplayName("Invalid block material should be invalid")
    void invalidBlockMaterialShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("block-break");
        assertNotNull(info, "Block Break requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("block-break INVALID_MATERIAL 10");
        }, "Invalid block material should not be valid");
    }

    @Test
    @DisplayName("Non-block material should be invalid")
    void nonBlockMaterialShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("block-break");
        assertNotNull(info, "Block Break requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("block-break DIAMOND_SWORD 10");
        }, "Non-block material should not be valid");
    }

    @Test
    @DisplayName("Invalid amount format should be invalid")
    void invalidAmountFormatShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("block-break");
        assertNotNull(info, "Block Break requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("block-break STONE abc");
        }, "Non-numeric amount should not be valid");
    }

    @Test
    @DisplayName("Missing amount parameter should be invalid")
    void missingAmountParameterShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("block-break");
        assertNotNull(info, "Block Break requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("block-break STONE");
        }, "Missing amount parameter should not be valid");
    }

    @Test
    @DisplayName("Missing block parameter should be invalid")
    void missingBlockParameterShouldBeInvalid() {
        // Setup
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("block-break");
        assertNotNull(info, "Block Break requirement should be registered");

        // Verify
        assertThrows(IllegalArgumentException.class, () -> {
            RequirementFactory.createRequirement("block-break 10");
        }, "Missing block parameter should not be valid");
    }

    @Test
    @DisplayName("Block Break requirement should be registered")
    void shouldBeRegistered() {
        RequirementRegistry.RequirementInfo info = RequirementRegistry.fromName("block-break");
        assertNotNull(info, "Block Break requirement should be registered");
        assertEquals(BlockBreakRequirement.class, info.requirementClass(), 
                "Block Break requirement should be registered with correct class");
    }

    @Test
    @DisplayName("Block Break requirement should validate parameters")
    void shouldValidateParameters() {
        // Valid parameters - single block
        assertDoesNotThrow(() -> new BlockBreakRequirement(new String[] { "STONE", "10" }), 
                "Valid block and amount should not throw exception");

        // Valid parameters - multiple blocks
        assertDoesNotThrow(() -> new BlockBreakRequirement(new String[] { "STONE", "DIRT", "10" }), 
                "Multiple valid blocks and amount should not throw exception");

        // Invalid parameters - missing block
        assertThrows(IllegalArgumentException.class, 
                () -> new BlockBreakRequirement(new String[] { "10" }), 
                "Missing block should throw exception");

        // Invalid parameters - missing amount
        assertThrows(IllegalArgumentException.class, 
                () -> new BlockBreakRequirement(new String[] { "STONE" }), 
                "Missing amount should throw exception");

        // Invalid parameters - invalid block material
        assertThrows(IllegalArgumentException.class, 
                () -> new BlockBreakRequirement(new String[] { "INVALID_MATERIAL", "10" }), 
                "Invalid block material should throw exception");

        // Invalid parameters - non-block material
        assertThrows(IllegalArgumentException.class, 
                () -> new BlockBreakRequirement(new String[] { "DIAMOND_SWORD", "10" }), 
                "Non-block material should throw exception");

        // Invalid parameters - non-numeric amount
        assertThrows(IllegalArgumentException.class, 
                () -> new BlockBreakRequirement(new String[] { "STONE", "abc" }), 
                "Non-numeric amount should throw exception");
    }

    @Test
    @DisplayName("Block Break requirement should check block break statistics correctly")
    void shouldCheckBlockBreakStatisticsCorrectly() {
        // Setup mock statistics
        when(mockPlayerEntity.getStatistic(eq(Statistic.MINE_BLOCK), eq(Material.STONE))).thenReturn(20);
        when(mockPlayerEntity.getStatistic(eq(Statistic.MINE_BLOCK), eq(Material.DIRT))).thenReturn(10);
        when(mockPlayerEntity.getStatistic(eq(Statistic.MINE_BLOCK), eq(Material.DIAMOND_BLOCK))).thenReturn(0);

        // Single block, requirement met
        BlockBreakRequirement singleBlockMet = new BlockBreakRequirement(new String[] { "STONE", "10" });
        assertTrue(singleBlockMet.meetsRequirement(mockPlayer), 
                "Player with 20 STONE blocks broken should meet requirement of 10");

        // Single block, requirement exactly met
        BlockBreakRequirement singleBlockExact = new BlockBreakRequirement(new String[] { "STONE", "20" });
        assertTrue(singleBlockExact.meetsRequirement(mockPlayer), 
                "Player with 20 STONE blocks broken should meet requirement of 20");

        // Single block, requirement not met
        BlockBreakRequirement singleBlockNotMet = new BlockBreakRequirement(new String[] { "STONE", "30" });
        assertFalse(singleBlockNotMet.meetsRequirement(mockPlayer), 
                "Player with 20 STONE blocks broken should not meet requirement of 30");

        // Multiple blocks, requirement met
        BlockBreakRequirement multipleBlocksMet = new BlockBreakRequirement(new String[] { "STONE", "DIRT", "15" });
        assertTrue(multipleBlocksMet.meetsRequirement(mockPlayer), 
                "Player with 20 STONE and 10 DIRT blocks broken (30 total) should meet requirement of 15");

        // Multiple blocks, requirement not met
        BlockBreakRequirement multipleBlocksNotMet = new BlockBreakRequirement(new String[] { "STONE", "DIRT", "35" });
        assertFalse(multipleBlocksNotMet.meetsRequirement(mockPlayer), 
                "Player with 20 STONE and 10 DIRT blocks broken (30 total) should not meet requirement of 35");

        // Block with zero breaks, requirement not met
        BlockBreakRequirement zeroBreaksMet = new BlockBreakRequirement(new String[] { "DIAMOND_BLOCK", "5" });
        assertFalse(zeroBreaksMet.meetsRequirement(mockPlayer), 
                "Player with 0 DIAMOND_BLOCK blocks broken should not meet requirement of 5");
    }
}
