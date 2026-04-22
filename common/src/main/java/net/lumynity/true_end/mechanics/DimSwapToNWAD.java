package net.lumynity.true_end.mechanics;

import dev.architectury.event.EventResult;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.config.TEConfig;
import net.lumynity.true_end.mechanics.logic.PlayerInvManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.lumynity.true_end.registries.TEDimKeys.BTD;
import static net.lumynity.true_end.registries.TEDimKeys.NWAD;

public class DimSwapToNWAD {
    private static final Map<UUID, ResourceKey<Level>> diedIn = new HashMap<>();

    public static EventResult onEntityAttacked(LivingEntity entity, DamageSource source, float amount) {
        Level world = entity.level();
        if (entity.level().dimension() == BTD || entity.level().dimension() == NWAD) return EventResult.pass();
        if (source == null) return EventResult.pass();
        if (!(entity instanceof ServerPlayer player)) return EventResult.pass();
        if (!(world.dimension().equals(Level.OVERWORLD) || world.dimension().equals(Level.NETHER))) return EventResult.pass();
        if (!(source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.FELL_OUT_OF_WORLD))) return EventResult.pass();
        if (Math.random() < TEConfig.randomEventChance * 2) return EventResult.pass();

        if (TrueEnd.hasAdvancement(player, "true_end:leave_the_nightmare_within_a_dream")) return EventResult.pass();

        PlayerInvManager.saveInvNWAD(player);
        if (!world.isClientSide()) {
            player.setGameMode(GameType.ADVENTURE);
            teleportToNWAD(player);
        }
        return EventResult.pass();
    }

    public static EventResult onPlayerDeath(LivingEntity entity, DamageSource source) {
        if (!(entity instanceof ServerPlayer player)) return EventResult.pass();
        ResourceKey<Level> dim = player.level().dimension();
        if (dim == NWAD) diedIn.put(player.getUUID(), dim);
        return EventResult.pass();
    }
    public static void onPlayerRespawn(ServerPlayer player,  boolean bl) {

        UUID uuid = player.getUUID();
        ResourceKey<Level> dim = diedIn.remove(uuid);
        if (dim == null || dim != NWAD) return;

        PlayerInvManager.restoreInv(player);
        player.setGameMode(GameType.SURVIVAL);

        Advancement advancement = player.server.getAdvancements()
                .getAdvancement(TrueEnd.asResource("leave_the_nightmare_within_a_dream"));
        assert advancement != null;
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) {
            for (String criteria : progress.getRemainingCriteria()) {
                player.getAdvancements().award(advancement, criteria);
            }
        }
    }

    private static void teleportToNWAD(ServerPlayer player) {
        if (player.level().dimension() == NWAD) return;
        ServerLevel next = player.server.getLevel(NWAD);
        if (next == null) return;

        player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));

        int x = player.getBlockX(), z = player.getBlockZ(), y = 120;
        boolean found = false;
        while (y > 0) {
            BlockPos pos = new BlockPos(x, y, z);
            if (next.isEmptyBlock(pos)
                    && next.isEmptyBlock(pos.above())
                    && !next.isEmptyBlock(pos.below())) {
                found = true;
                break;
            }
            y--;
        }
        if (!found) y = 129;

        player.teleportTo(next, x, y + 1, z, player.getYRot(), player.getXRot());
        player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
        player.getActiveEffects().forEach(e ->
                player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), e))
        );
    }
}