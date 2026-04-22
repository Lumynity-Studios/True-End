package net.lumynity.true_end.variables;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class TEVariables {

    @ExpectPlatform
    public static PlayerData getPlayerData(ServerPlayer player) {
        throw new AssertionError("Should never be called");
    }

    @ExpectPlatform
    public static WorldData getLevelData(Level level) {
        throw new AssertionError("Should never be called");
    }
}
