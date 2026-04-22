package net.lumynity.true_end.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class PortalPlatformHelper {

    @ExpectPlatform
    public static void portalSpawn(Level level, BlockPos pos) {
        throw new AssertionError("ExpectPlatform method not implemented");
    }

    @ExpectPlatform
    public static void playPortalSound(Level level, BlockPos pos) {
        throw new AssertionError("ExpectPlatform method not implemented");
    }
}
