package net.lumynity.true_end.client.dimension;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;

public class NightmareWithinADream extends DimensionSpecialEffects {
    public NightmareWithinADream() {
        super(255f, false, SkyType.NONE, true, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
        return new Vec3(0, 0, 0);
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return true;
    }
}
