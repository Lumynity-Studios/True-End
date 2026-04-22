package net.lumynity.true_end.variables.fabric;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.lumynity.true_end.fabric.ServerPlayerExt;
import net.lumynity.true_end.variables.PlayerData;
import net.lumynity.true_end.variables.WorldData;

public class TEVariablesImpl {

    public static PlayerData getPlayerData(ServerPlayer player) {
        PlayerData playerData = ((ServerPlayerExt) player).true_end$getPlayerData();

        return ((ServerPlayerExt) player).true_end$getPlayerData();
    }

    public static WorldData getLevelData(Level level) {
        return WorldData.get(level);
    }
}
