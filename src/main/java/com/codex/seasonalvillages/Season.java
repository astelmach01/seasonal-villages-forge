package com.codex.seasonalvillages;

import java.util.Locale;

public enum Season {
    SPRING("Spring", "fresh gardens and new trades"),
    SUMMER("Summer", "busy market days and quick feet"),
    AUTUMN("Autumn", "harvest stores and warm meals"),
    WINTER("Winter", "lanterns, snow, and sturdy shelter");

    public static final long TICKS_PER_DAY = 24_000L;

    private final String displayName;
    private final String villageMood;

    Season(String displayName, String villageMood) {
        this.displayName = displayName;
        this.villageMood = villageMood;
    }

    public static Season fromWorldTime(long timeOfDay, int daysPerSeason) {
        if (daysPerSeason <= 0) {
            throw new IllegalArgumentException("daysPerSeason must be positive");
        }

        long day = Math.floorDiv(timeOfDay, TICKS_PER_DAY);
        long seasonIndex = Math.floorMod(Math.floorDiv(day, daysPerSeason), values().length);
        return values()[(int) seasonIndex];
    }

    public static long yearIndex(long timeOfDay, int daysPerSeason) {
        if (daysPerSeason <= 0) {
            throw new IllegalArgumentException("daysPerSeason must be positive");
        }

        long day = Math.floorDiv(timeOfDay, TICKS_PER_DAY);
        return Math.floorDiv(day, (long) daysPerSeason * values().length);
    }

    public String displayName() {
        return displayName;
    }

    public String villageMood() {
        return villageMood;
    }

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }
}
