package net.lumynity.true_end.mechanics.randomevents.entityspawning;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.config.TEConfig;
import net.lumynity.true_end.content.entity.Unknown;
import net.lumynity.true_end.registries.TEEntities;
import net.lumynity.true_end.mechanics.advancement.NotAlone;
import net.lumynity.true_end.variables.TEVariables;

import java.util.List;

public class SpawnStalking {
    private static final long TICK_INTERVAL = 1200L;
    private static final int MAX_ATTEMPTS = 16;

    public static void onPlayerTick(Player player) {
        if (!(player.level() instanceof ServerLevel serverWorld)) return;
        if (serverWorld.getGameTime() % TICK_INTERVAL != 0) return;
        if (serverWorld.dimension() == Level.END) return;
        if (!TEConfig.doRandomEvents) return;

        long daysPlayed = serverWorld.getGameTime() / 24000;
        if (daysPlayed < 10) return;
        double difficultyMultiplier = switch (serverWorld.getDifficulty()) {
            case PEACEFUL -> 3.0;
            case EASY -> 0.5;
            case NORMAL -> 1.0;
            case HARD -> 2.0;
        };
        double spawnChance = TEConfig.entitySpawnChance * difficultyMultiplier * (daysPlayed - 10);
        spawnChance = Math.min(spawnChance, 1.0); //cap at 100%
        if (!(serverWorld.random.nextDouble() < (spawnChance))) return;

        List<ServerPlayer> players = serverWorld.players();
        if (players.isEmpty()) return;
        ServerPlayer serverPlayer = players.get(serverWorld.random.nextInt(players.size()));
        double maxDistance = (serverWorld.getServer().getPlayerList().getViewDistance() * 16.0) - 48.0;
        if (maxDistance <= 32.0) return; //return if render distance too small

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            double angle = serverWorld.random.nextDouble() * Math.PI * 2.0;
            double dist = 32.0 + serverWorld.random.nextDouble() * (maxDistance - 32.0);
            double px = serverPlayer.getX(), pz = serverPlayer.getZ();
            int x = Mth.floor(px + Math.cos(angle) * dist);
            int z = Mth.floor(pz + Math.sin(angle) * dist);
            int surfaceY = serverWorld.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);

            BlockPos groundPos = new BlockPos(x, surfaceY - 1, z);
            BlockState groundState = serverWorld.getBlockState(groundPos);
            if (groundState.isAir() || groundState.getBlock() == Blocks.WATER || groundState.getBlock() == Blocks.LAVA) {
                TrueEnd.LOGGER.debug("Attempt {}: invalid ground at {}: {}", attempt, groundPos, groundState.getBlock());
                continue;
            }

            EntityType<Unknown> type = TEEntities.UNKNOWN.get();
            Unknown entity = type.create(serverWorld);

            if (entity == null) return;
            entity.moveTo(x + 0.5, surfaceY, z + 0.5, serverWorld.random.nextFloat() * 360.0F, 0.0F);
            entity.setPersistenceRequired();
            entity.behavior = Unknown.UnknownBehavior.STALKING;
            serverWorld.addFreshEntity(entity);

            TEVariables.getLevelData(serverWorld).setUnknownInWorld(true);
            NotAlone.grantAdvancement(serverPlayer);
            TrueEnd.LOGGER.info("Spawned 'Unknown' at {} on {} after {} attempts.", entity.blockPosition(), groundState, attempt + 1);
            return;
        }
    }
}