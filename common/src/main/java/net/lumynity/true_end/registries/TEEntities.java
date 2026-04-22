package net.lumynity.true_end.registries;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.content.entity.Unknown;

public class TEEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(TrueEnd.MOD_ID, Registries.ENTITY_TYPE);
    public static final RegistrySupplier<EntityType<Unknown>> UNKNOWN = REGISTRY.register("unknown",  () -> EntityType.Builder.<Unknown>of(Unknown::new, MobCategory.CREATURE)
            .sized(0.6F, 1.95F)
            .build("unknown")
    );

    public static void register() {
        REGISTRY.register();

        EntityAttributeRegistry.register(UNKNOWN, Unknown::createAttributes);
    }
}