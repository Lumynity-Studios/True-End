package net.lumynity.true_end.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.lumynity.true_end.TrueEnd;

import java.util.Set;

public class TEPoiTypes {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(TrueEnd.MOD_ID, Registries.POINT_OF_INTEREST_TYPE);

    public static final RegistrySupplier<PoiType> BEYOND_THE_DREAM_PORTAL = POI_TYPES.register(
            "beyond_the_dream_portal",
            () -> new PoiType(
                    Set.copyOf(TEBlocks.BEYOND_THE_DREAM_PORTAL.get().getStateDefinition().getPossibleStates()),
                    0, 1
            )
    );

    public static void register() {
        POI_TYPES.register();
    }
}
