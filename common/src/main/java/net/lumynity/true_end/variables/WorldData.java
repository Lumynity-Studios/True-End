package net.lumynity.true_end.variables;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;

public class WorldData extends SavedData {
    public static final String DATA_NAME = "true_end_mapvars";

    private double btdSpawnX = 0.0;
    private double btdSpawnY = 0.0;
    private double btdSpawnZ = 0.0;
    private boolean unknownInWorld = false;
    private boolean hasShownCredits = false;

    public boolean isUnknownInWorld() { return unknownInWorld; }
    public double getBtdSpawnX() { return btdSpawnX; }
    public double getBtdSpawnY() { return btdSpawnY; }
    public double getBtdSpawnZ() { return btdSpawnZ; }
    public boolean hasShownCredits() { return hasShownCredits;}

    public void setUnknownInWorld(boolean v) { unknownInWorld = v ;setDirty(); }
    public void setBtdSpawn(double x, double y, double z) { btdSpawnX = x; btdSpawnY = y; btdSpawnZ = z; setDirty(); }
    public void setShownCredits(boolean v) { hasShownCredits = v; }

    public static WorldData load(CompoundTag nbt) {
        WorldData m = new WorldData();
        m.btdSpawnX = nbt.getDouble("btdSpawnX");
        m.btdSpawnY = nbt.getDouble("btdSpawnY");
        m.btdSpawnZ = nbt.getDouble("btdSpawnZ");
        m.unknownInWorld = nbt.getBoolean("unknownInWorld");
        return m;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putDouble("btdSpawnX", btdSpawnX);
        nbt.putDouble("btdSpawnY", btdSpawnY);
        nbt.putDouble("btdSpawnZ", btdSpawnZ);
        nbt.putBoolean("unknownInWorld", unknownInWorld);
        return nbt;
    }

    public static WorldData get(LevelAccessor world) {
        if (!(world instanceof ServerLevel lvl)) return new WorldData();
        ServerLevel overworld = lvl.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return new WorldData();
        }
        return overworld.getDataStorage().computeIfAbsent(WorldData::load, WorldData::new, DATA_NAME);
    }
}