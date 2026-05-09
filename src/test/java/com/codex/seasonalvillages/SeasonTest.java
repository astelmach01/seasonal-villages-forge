package com.codex.seasonalvillages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class SeasonTest {
    @Test
    void cyclesThroughFourSeasonsByConfiguredDayLength() {
        int daysPerSeason = 8;

        assertEquals(Season.SPRING, Season.fromWorldTime(0, daysPerSeason));
        assertEquals(Season.SPRING, Season.fromWorldTime(7 * Season.TICKS_PER_DAY, daysPerSeason));
        assertEquals(Season.SUMMER, Season.fromWorldTime(8 * Season.TICKS_PER_DAY, daysPerSeason));
        assertEquals(Season.AUTUMN, Season.fromWorldTime(16 * Season.TICKS_PER_DAY, daysPerSeason));
        assertEquals(Season.WINTER, Season.fromWorldTime(24 * Season.TICKS_PER_DAY, daysPerSeason));
        assertEquals(Season.SPRING, Season.fromWorldTime(32 * Season.TICKS_PER_DAY, daysPerSeason));
    }

    @Test
    void reportsYearIndexAfterFullSeasonCycle() {
        int daysPerSeason = 8;

        assertEquals(0, Season.yearIndex(31 * Season.TICKS_PER_DAY, daysPerSeason));
        assertEquals(1, Season.yearIndex(32 * Season.TICKS_PER_DAY, daysPerSeason));
        assertEquals(2, Season.yearIndex(64 * Season.TICKS_PER_DAY, daysPerSeason));
    }

    @Test
    void rejectsInvalidSeasonLength() {
        assertThrows(IllegalArgumentException.class, () -> Season.fromWorldTime(0, 0));
        assertThrows(IllegalArgumentException.class, () -> Season.yearIndex(0, -1));
    }
}
