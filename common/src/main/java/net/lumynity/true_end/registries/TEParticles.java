package net.lumynity.true_end.registries;

import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.content.particle.PortalParticle;
import net.lumynity.true_end.content.particle.PortalParticleType;

public class TEParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(TrueEnd.MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<SimpleParticleType> DREAM_PORTAL_PARTICLE =
        PARTICLE_TYPES.register("dream_portal_particle", () -> new PortalParticleType(false));

    public static void registerParticles() {
        ParticleProviderRegistry.register(DREAM_PORTAL_PARTICLE.get(), PortalParticle::provider);
    }

    public static void registerParticleTypes() {
        PARTICLE_TYPES.register();
    }

}
