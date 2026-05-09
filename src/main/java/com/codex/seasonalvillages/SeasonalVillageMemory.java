package com.codex.seasonalvillages;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

public final class SeasonalVillageMemory extends SavedData {
    private static final String DECORATED_CHUNKS_KEY = "decoratedChunks";
    private static final String CHUNK_KEY = "chunk";
    private static final String MARKER_KEY = "marker";

    private final Map<String, String> decoratedChunkMarkers = new HashMap<>();

    public static SeasonalVillageMemory load(CompoundTag tag, HolderLookup.Provider registries) {
        SeasonalVillageMemory memory = new SeasonalVillageMemory();
        ListTag chunks = tag.getList(DECORATED_CHUNKS_KEY, Tag.TAG_COMPOUND);

        for (int i = 0; i < chunks.size(); i++) {
            CompoundTag entry = chunks.getCompound(i);
            String chunk = entry.getString(CHUNK_KEY);
            String marker = entry.getString(MARKER_KEY);
            if (!chunk.isBlank() && !marker.isBlank()) {
                memory.decoratedChunkMarkers.put(chunk, marker);
            }
        }

        return memory;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag chunks = new ListTag();
        decoratedChunkMarkers.forEach((chunk, marker) -> {
            CompoundTag entry = new CompoundTag();
            entry.putString(CHUNK_KEY, chunk);
            entry.putString(MARKER_KEY, marker);
            chunks.add(entry);
        });
        tag.put(DECORATED_CHUNKS_KEY, chunks);
        return tag;
    }

    public boolean shouldDecorate(String worldId, int chunkX, int chunkZ, Season season, long yearIndex) {
        String chunkKey = worldId + ":" + chunkX + ":" + chunkZ;
        String marker = season.id() + ":" + yearIndex;
        String previous = decoratedChunkMarkers.put(chunkKey, marker);
        boolean shouldDecorate = !marker.equals(previous);
        if (shouldDecorate) {
            setDirty();
        }
        return shouldDecorate;
    }

    public int decoratedChunkCount() {
        return decoratedChunkMarkers.size();
    }
}
