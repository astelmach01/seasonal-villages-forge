package com.codex.seasonalvillages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class SeasonalVillageMemoryTest {
    @Test
    void decoratesEachChunkOnlyOncePerSeasonPerYear() {
        SeasonalVillageMemory memory = new SeasonalVillageMemory();

        assertTrue(memory.shouldDecorate("minecraft:overworld", 2, -4, Season.SPRING, 0));
        assertFalse(memory.shouldDecorate("minecraft:overworld", 2, -4, Season.SPRING, 0));
        assertTrue(memory.shouldDecorate("minecraft:overworld", 2, -4, Season.SUMMER, 0));
        assertTrue(memory.shouldDecorate("minecraft:overworld", 2, -4, Season.SPRING, 1));
        assertEquals(1, memory.decoratedChunkCount());
    }
}
