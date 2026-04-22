package net.lumynity.true_end.fabric;

import net.fabricmc.api.ModInitializer;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.content.world.biome.fabric.SeepingReality;
import terrablender.api.Regions;
import terrablender.api.TerraBlenderApi;

public final class TrueEndFabric implements ModInitializer, TerraBlenderApi {

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        TrueEnd.init();

    }
    @Override
    public void onTerraBlenderInitialized()
    {
        TrueEnd.LOGGER.info("TERRABLENDER");
        Regions.register(new SeepingReality(TrueEnd.asResource("overworld_region"), 1));
    }
}
