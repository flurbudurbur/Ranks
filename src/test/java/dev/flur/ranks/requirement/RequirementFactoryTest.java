package dev.flur.ranks.requirement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequirementFactoryTest {

    @Test
    public void testGetRequirementName() {
        // Create an xp-level requirement
        Requirement xpLevelRequirement = RequirementFactory.createRequirement("xp-level 10");
        // Get its name using the new method
        String name = RequirementFactory.getRequirementName(xpLevelRequirement);
        // Verify the name is correct
        assertEquals("xp-level", name);
    }
}
