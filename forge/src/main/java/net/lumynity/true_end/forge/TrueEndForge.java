package net.lumynity.true_end.forge;

import dev.architectury.platform.forge.EventBuses;
import net.lumynity.true_end.content.world.biome.forge.SeepingReality;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.lumynity.true_end.TrueEnd;
import terrablender.api.Regions;

import static org.antlr.runtime.debug.DebugEventListener.PROTOCOL_VERSION;

@Mod(TrueEnd.MOD_ID)
public final class TrueEndForge {
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(
        TrueEnd.asMod(), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static IEventBus EVENT_BUS;

    public TrueEndForge(FMLJavaModLoadingContext modContext) {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(TrueEnd.MOD_ID, EVENT_BUS);
        // Run our common setup.
        TrueEnd.init();

        EVENT_BUS.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Regions.register(new SeepingReality(TrueEnd.asResource("overworld_region"), 1));
        });
    }
}
