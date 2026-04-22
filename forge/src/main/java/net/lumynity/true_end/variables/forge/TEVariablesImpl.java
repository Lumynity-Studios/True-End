package net.lumynity.true_end.variables.forge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.variables.PlayerData;
import net.lumynity.true_end.variables.WorldData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TEVariablesImpl {

    public static PlayerData getPlayerData(ServerPlayer player) {
        return player.getCapability(PLAYER_VARS_CAP).orElse(new PlayerData());
    }

    public static WorldData getLevelData(Level level) {
        return WorldData.get(level);
    }

    public static final Capability<PlayerData> PLAYER_VARS_CAP = CapabilityManager.get(new CapabilityToken<PlayerData>() {});
    public static final Capability<WorldData> MAP_VARIABLES_CAP = CapabilityManager.get(new CapabilityToken<WorldData>() {});

    @SubscribeEvent
    public static void onRegisterCaps(RegisterCapabilitiesEvent evt) {
        evt.register(PlayerData.class);
        evt.register(WorldData.class);
    }

    @Mod.EventBusSubscriber
    public static class PlayerVariablesProvider implements ICapabilitySerializable<CompoundTag> {
        private PlayerData vars = new PlayerData();
        private final LazyOptional<PlayerData> opt = LazyOptional.of(() -> vars);

        @SubscribeEvent
        public static void attachCaps(AttachCapabilitiesEvent<Entity> evt) {
            if (evt.getObject() instanceof ServerPlayer) {
                evt.addCapability(
                        TrueEnd.asResource("player_vars"),
                        new PlayerVariablesProvider()
                );
            }
        }

        @SubscribeEvent
        public static void cloneOnDeath(PlayerEvent.Clone evt) {
            if (evt.isWasDeath()) {
                evt.getOriginal().reviveCaps();
                evt.getOriginal().getCapability(PLAYER_VARS_CAP).ifPresent(oldV ->
                        evt.getEntity().getCapability(PLAYER_VARS_CAP).ifPresent(newV ->
                                newV.setBeenBeyond(oldV.getBeenBeyond())
                        )
                );
            }
        }

        @SubscribeEvent
        public static void syncOnLogin(PlayerEvent.PlayerLoggedInEvent evt) {
            if (evt.getEntity() instanceof ServerPlayer player) {
            }
        }

        @SubscribeEvent
        public static void syncOnRespawn(PlayerEvent.PlayerRespawnEvent evt) {
            if (evt.getEntity() instanceof ServerPlayer player) {
            }
        }

        @SubscribeEvent
        public static void syncOnDimChange(PlayerEvent.PlayerChangedDimensionEvent evt) {
            if (evt.getEntity() instanceof ServerPlayer player) {
            }
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
            return cap == PLAYER_VARS_CAP ? opt.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            vars.save(nbt);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            vars.load(nbt);
        }
    }
}
