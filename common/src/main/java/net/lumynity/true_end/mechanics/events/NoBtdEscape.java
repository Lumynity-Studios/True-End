package net.lumynity.true_end.mechanics.events;

import dev.architectury.event.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.variables.TEVariables;

import java.util.*;

import static net.lumynity.true_end.registries.TEDimKeys.BTD;

public class NoBtdEscape {
    private static final Map<UUID, ResourceKey<Level>> diedIn = new HashMap<>();

    public static EventResult onPlayerDeath(LivingEntity entity, DamageSource damageSource) {
        if (!(entity instanceof ServerPlayer player)) return EventResult.pass();
        diedIn.put(player.getUUID(), player.level().dimension());

        return EventResult.pass();
    }

    public static void onPlayerRespawn(ServerPlayer player, boolean b) {

        boolean leftBtd = player.getAdvancements()
                .getOrStartProgress(Objects.requireNonNull(
                        player.server.getAdvancements().getAdvancement(TrueEnd.asResource("go_back")))
                ).isDone();
        if (leftBtd) return;

        UUID uuid = player.getUUID();
        ResourceKey<Level> dim = diedIn.remove(uuid);
        if (dim == null || dim != BTD) return;

        BlockPos respawnPos = player.getRespawnPosition();
        ResourceKey<Level> respawnDim = player.getRespawnDimension();

        if (respawnPos != null) {
            ServerLevel targetLevel = player.server.getLevel(respawnDim);
            if (targetLevel != null) {
                Optional<Vec3> maybeSpot = Player.findRespawnPositionAndUseSpawnBlock(targetLevel, respawnPos, 0, player.isRespawnForced(), false);

                if (maybeSpot.isPresent()) {
                    Vec3 spot = maybeSpot.get();
                    player.teleportTo(targetLevel, spot.x, spot.y+0.05, spot.z, 180, 0);
                    return;
                }

                double fallbackX = respawnPos.getX()+0.5;
                double fallbackY = respawnPos.getY();
                double fallbackZ = respawnPos.getZ()+0.5;
                player.teleportTo(targetLevel, fallbackX, fallbackY+0.05, fallbackZ, 180,0);
                return;
            }
        }

        double bx = TEVariables.getLevelData(player.serverLevel()).getBtdSpawnX();
        double by = TEVariables.getLevelData(player.serverLevel()).getBtdSpawnY();
        double bz = TEVariables.getLevelData(player.serverLevel()).getBtdSpawnZ();
        ServerLevel btd = player.server.getLevel(BTD);
        if (btd == null) return;

        player.teleportTo(btd, bx, by+0.05, bz, 0, 0);
    }
}
