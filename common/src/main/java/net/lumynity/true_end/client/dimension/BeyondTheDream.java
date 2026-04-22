package net.lumynity.true_end.client.dimension;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import net.lumynity.true_end.config.TEConfig;
import org.jetbrains.annotations.NotNull;

public class BeyondTheDream extends DimensionSpecialEffects {
    public BeyondTheDream() {
        super(112f, true, SkyType.NONE, false, false);
    }

    @Override
    public @NotNull Vec3 getBrightnessDependentFogColor(@NotNull Vec3 color, float sunHeight) {
        return color;
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return TEConfig.showFogClient;
    }
}
