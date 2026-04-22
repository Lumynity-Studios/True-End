package net.lumynity.true_end.content.world.biome.fabric;

import com.mojang.datafixers.util.Pair;
import net.lumynity.true_end.content.world.biome.SeepingRealityBiomeSpec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

public class SeepingReality extends Region {
    public SeepingReality(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
        SeepingRealityBiomeSpec.biome(builder);
        builder.build().forEach(mapper);
    }


}