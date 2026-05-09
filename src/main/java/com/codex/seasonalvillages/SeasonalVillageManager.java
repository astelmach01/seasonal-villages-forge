package com.codex.seasonalvillages;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class SeasonalVillageManager {
    private static final String MEMORY_ID = SeasonalVillagesForge.MOD_ID + "_memory";

    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel serverLevel)) {
            return;
        }

        tickLevel(serverLevel);
    }

    public static void tickLevel(ServerLevel level) {
        if (!Level.OVERWORLD.equals(level.dimension())) {
            return;
        }

        if (level.getGameTime() % SeasonalVillageConfig.tickInterval() != 0) {
            return;
        }

        for (ServerPlayer player : level.players()) {
            tickNearPlayer(level, player);
        }
    }

    public static VillageSnapshot snapshot(ServerLevel level, BlockPos center) {
        Season season = Season.fromWorldTime(level.getDayTime(), SeasonalVillageConfig.daysPerSeason());
        long yearIndex = Season.yearIndex(level.getDayTime(), SeasonalVillageConfig.daysPerSeason());
        int nearbyVillagers = findVillagers(level, center).size();

        return new VillageSnapshot(
                season,
                yearIndex,
                nearbyVillagers,
                nearbyVillagers >= SeasonalVillageConfig.minimumVillagersForVillage()
        );
    }

    private static void tickNearPlayer(ServerLevel level, ServerPlayer player) {
        VillageSnapshot snapshot = snapshot(level, player.blockPosition());
        if (!snapshot.activeVillage()) {
            return;
        }

        List<Villager> villagers = findVillagers(level, player.blockPosition());
        if (SeasonalVillageConfig.villagerBuffs()) {
            applySeasonalCare(snapshot.season(), villagers);
        }

        ChunkPos chunkPos = new ChunkPos(player.blockPosition());
        String worldId = level.dimension().location().toString();
        SeasonalVillageMemory memory = memory(level);
        if (memory.shouldDecorate(worldId, chunkPos.x, chunkPos.z, snapshot.season(), snapshot.yearIndex())) {
            if (SeasonalVillageConfig.seasonalDecorations()) {
                decorateVillage(level, player.blockPosition(), snapshot.season());
            }
            if (SeasonalVillageConfig.seasonalGifts()) {
                shareSeasonalGoods(level, villagers, snapshot.season());
            }
        }
    }

    private static SeasonalVillageMemory memory(ServerLevel level) {
        SavedData.Factory<SeasonalVillageMemory> factory = new SavedData.Factory<>(
                SeasonalVillageMemory::new,
                SeasonalVillageMemory::load,
                null
        );
        return level.getDataStorage().computeIfAbsent(factory, MEMORY_ID);
    }

    private static List<Villager> findVillagers(ServerLevel level, BlockPos center) {
        double radius = SeasonalVillageConfig.villageRadius();
        AABB searchArea = AABB.ofSize(Vec3.atCenterOf(center), radius * 2.0, 32.0, radius * 2.0);
        return level.getEntities(EntityType.VILLAGER, searchArea, Villager::isAlive);
    }

    private static void applySeasonalCare(Season season, List<Villager> villagers) {
        for (Villager villager : villagers) {
            switch (season) {
                case SPRING -> villager.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 260, 0, true, false));
                case SUMMER -> villager.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 260, 0, true, false));
                case AUTUMN -> villager.addEffect(new MobEffectInstance(MobEffects.SATURATION, 260, 0, true, false));
                case WINTER -> villager.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 260, 0, true, false));
            }
        }
    }

    private static void decorateVillage(ServerLevel level, BlockPos center, Season season) {
        int placed = 0;

        for (int attempt = 0; attempt < SeasonalVillageConfig.decorationAttempts()
                && placed < SeasonalVillageConfig.maxDecorationsPerPass(); attempt++) {
            BlockPos pos = topAirPosNear(level, center, SeasonalVillageConfig.decorationRadius());
            if (pos == null) {
                continue;
            }

            BlockState state = decorationFor(season);
            if (state.canSurvive(level, pos) && level.setBlock(pos, state, 3)) {
                placed++;
                level.sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5,
                        pos.getY() + 0.75,
                        pos.getZ() + 0.5,
                        2,
                        0.25,
                        0.2,
                        0.25,
                        0.01
                );
            }
        }
    }

    private static BlockPos topAirPosNear(ServerLevel level, BlockPos center, int radius) {
        int x = center.getX() + level.random.nextInt(radius * 2 + 1) - radius;
        int z = center.getZ() + level.random.nextInt(radius * 2 + 1) - radius;
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        BlockPos pos = new BlockPos(x, y, z);
        BlockPos support = pos.below();

        if (!level.isEmptyBlock(pos)) {
            return null;
        }

        BlockState supportState = level.getBlockState(support);
        if (!supportState.isFaceSturdy(level, support, Direction.UP) || !isVillageDecorationSupport(supportState)) {
            return null;
        }

        return pos;
    }

    private static boolean isVillageDecorationSupport(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.ROOTED_DIRT)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.MYCELIUM)
                || state.is(Blocks.DIRT_PATH)
                || state.is(Blocks.STONE)
                || state.is(Blocks.COBBLESTONE)
                || state.is(Blocks.MOSSY_COBBLESTONE)
                || state.is(Blocks.STONE_BRICKS)
                || state.is(Blocks.OAK_PLANKS)
                || state.is(Blocks.SPRUCE_PLANKS)
                || state.is(Blocks.BIRCH_PLANKS)
                || state.is(Blocks.SAND)
                || state.is(Blocks.RED_SAND);
    }

    private static BlockState decorationFor(Season season) {
        return switch (season) {
            case SPRING -> randomChoice(Blocks.POPPY.defaultBlockState(), Blocks.DANDELION.defaultBlockState());
            case SUMMER -> randomChoice(Blocks.TORCH.defaultBlockState(), Blocks.LANTERN.defaultBlockState());
            case AUTUMN -> randomChoice(Blocks.PUMPKIN.defaultBlockState(), Blocks.HAY_BLOCK.defaultBlockState());
            case WINTER -> randomChoice(Blocks.SNOW.defaultBlockState(), Blocks.LANTERN.defaultBlockState());
        };
    }

    private static BlockState randomChoice(BlockState first, BlockState second) {
        return Math.random() < 0.5 ? first : second;
    }

    private static void shareSeasonalGoods(ServerLevel level, List<Villager> villagers, Season season) {
        if (villagers.isEmpty()) {
            return;
        }

        int giftCount = Math.min(3, villagers.size());
        for (int i = 0; i < giftCount; i++) {
            Villager villager = villagers.get(level.random.nextInt(villagers.size()));
            ItemStack gift = giftFor(season, level.random.nextInt(2) == 0, level.random.nextInt(3));
            ItemEntity entity = new ItemEntity(level, villager.getX(), villager.getY() + 0.5, villager.getZ(), gift);
            entity.setPickUpDelay(20);
            level.addFreshEntity(entity);
        }
    }

    private static ItemStack giftFor(Season season, boolean firstChoice, int bonus) {
        return switch (season) {
            case SPRING -> new ItemStack(firstChoice ? Items.BEETROOT_SEEDS : Items.WHEAT_SEEDS, 2 + bonus);
            case SUMMER -> new ItemStack(firstChoice ? Items.MELON_SLICE : Items.SWEET_BERRIES, 2 + bonus);
            case AUTUMN -> new ItemStack(firstChoice ? Items.BREAD : Items.PUMPKIN_PIE, 1 + Math.min(1, bonus));
            case WINTER -> new ItemStack(firstChoice ? Items.BAKED_POTATO : Items.COOKIE, 1 + Math.min(1, bonus));
        };
    }
}
