package com.codex.seasonalvillages;

public record VillageSnapshot(Season season, long yearIndex, int nearbyVillagers, boolean activeVillage) {
    public String summary() {
        if (!activeVillage) {
            return "No active village nearby. Find a place with at least "
                    + SeasonalVillageConfig.minimumVillagersForVillage()
                    + " villagers to see seasonal village life.";
        }

        return season.displayName() + " village: " + season.villageMood()
                + " (" + nearbyVillagers + " villagers nearby).";
    }
}
