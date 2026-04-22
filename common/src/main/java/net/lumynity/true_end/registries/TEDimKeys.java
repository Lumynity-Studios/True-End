package net.lumynity.true_end.registries;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.lumynity.true_end.TrueEnd;

public class TEDimKeys {
    public static final ResourceKey<Level> BTD = ResourceKey.create(
        net.minecraft.core.registries.Registries.DIMENSION,
        TrueEnd.asResource("beyond_the_dream")
    );
    public static final ResourceKey<Level> NWAD = ResourceKey.create(
        net.minecraft.core.registries.Registries.DIMENSION,
        TrueEnd.asResource("nightmare_within_a_dream")
    );

    public static final ResourceKey<Level> OVERWORLD = Level.OVERWORLD;
}
