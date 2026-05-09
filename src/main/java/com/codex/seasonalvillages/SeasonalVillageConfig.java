package com.codex.seasonalvillages;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = SeasonalVillagesForge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SeasonalVillageConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue DAYS_PER_SEASON = BUILDER
            .comment("Number of Minecraft days in each season.")
            .defineInRange("daysPerSeason", 8, 1, 120);

    private static final ForgeConfigSpec.IntValue VILLAGE_RADIUS = BUILDER
            .comment("Horizontal radius used to detect nearby villagers.")
            .defineInRange("villageRadius", 48, 12, 128);

    private static final ForgeConfigSpec.IntValue MINIMUM_VILLAGERS_FOR_VILLAGE = BUILDER
            .comment("Minimum nearby villagers required for an active seasonal village.")
            .defineInRange("minimumVillagersForVillage", 3, 1, 32);

    private static final ForgeConfigSpec.IntValue TICK_INTERVAL = BUILDER
            .comment("How often, in server ticks, each level checks nearby players for seasonal village updates.")
            .defineInRange("tickInterval", 200, 20, 12_000);

    private static final ForgeConfigSpec.IntValue DECORATION_RADIUS = BUILDER
            .comment("Horizontal radius around active players where decorations may appear.")
            .defineInRange("decorationRadius", 28, 4, 128);

    private static final ForgeConfigSpec.IntValue DECORATION_ATTEMPTS = BUILDER
            .comment("Placement attempts per decoration pass.")
            .defineInRange("decorationAttempts", 80, 1, 512);

    private static final ForgeConfigSpec.IntValue MAX_DECORATIONS_PER_PASS = BUILDER
            .comment("Maximum decorations placed in one chunk for one season/year.")
            .defineInRange("maxDecorationsPerPass", 12, 0, 64);

    private static final ForgeConfigSpec.BooleanValue SEASONAL_DECORATIONS = BUILDER
            .comment("Enable seasonal village decorations.")
            .define("seasonalDecorations", true);

    private static final ForgeConfigSpec.BooleanValue SEASONAL_GIFTS = BUILDER
            .comment("Enable seasonal gift drops around villagers.")
            .define("seasonalGifts", true);

    private static final ForgeConfigSpec.BooleanValue VILLAGER_BUFFS = BUILDER
            .comment("Enable small seasonal villager buffs.")
            .define("villagerBuffs", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private static int daysPerSeason = 8;
    private static int villageRadius = 48;
    private static int minimumVillagersForVillage = 3;
    private static int tickInterval = 200;
    private static int decorationRadius = 28;
    private static int decorationAttempts = 80;
    private static int maxDecorationsPerPass = 12;
    private static boolean seasonalDecorations = true;
    private static boolean seasonalGifts = true;
    private static boolean villagerBuffs = true;

    private SeasonalVillageConfig() {
    }

    @SubscribeEvent
    static void onConfigLoad(ModConfigEvent event) {
        daysPerSeason = DAYS_PER_SEASON.get();
        villageRadius = VILLAGE_RADIUS.get();
        minimumVillagersForVillage = MINIMUM_VILLAGERS_FOR_VILLAGE.get();
        tickInterval = TICK_INTERVAL.get();
        decorationRadius = Math.min(DECORATION_RADIUS.get(), villageRadius);
        decorationAttempts = DECORATION_ATTEMPTS.get();
        maxDecorationsPerPass = MAX_DECORATIONS_PER_PASS.get();
        seasonalDecorations = SEASONAL_DECORATIONS.get();
        seasonalGifts = SEASONAL_GIFTS.get();
        villagerBuffs = VILLAGER_BUFFS.get();
    }

    public static int daysPerSeason() {
        return daysPerSeason;
    }

    public static int villageRadius() {
        return villageRadius;
    }

    public static int minimumVillagersForVillage() {
        return minimumVillagersForVillage;
    }

    public static int tickInterval() {
        return tickInterval;
    }

    public static int decorationRadius() {
        return decorationRadius;
    }

    public static int decorationAttempts() {
        return decorationAttempts;
    }

    public static int maxDecorationsPerPass() {
        return maxDecorationsPerPass;
    }

    public static boolean seasonalDecorations() {
        return seasonalDecorations;
    }

    public static boolean seasonalGifts() {
        return seasonalGifts;
    }

    public static boolean villagerBuffs() {
        return villagerBuffs;
    }
}
