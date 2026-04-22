package net.lumynity.true_end.mechanics.randomevents.entityspawning;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.lumynity.true_end.config.TEConfig;
import net.lumynity.true_end.content.entity.Unknown;
import net.lumynity.true_end.registries.TEEntities;
import net.lumynity.true_end.mechanics.advancement.NotAlone;
import net.lumynity.true_end.variables.TEVariables;

import java.util.List;

public class SpawnShortAttack {

    public static void onPlayerTick(Player player) {
        Level level = player.level();
        if (level.isClientSide() || !(level instanceof ServerLevel world)) return;
        if (TEVariables.getLevelData(world).isUnknownInWorld()) return;
        if (world.getDayTime() % 1200L != 0) return; //Tick Interval
        if (world.dimension() == Level.END) return;

        if (!TEConfig.doRandomEvents) return;
        if (!(world.random.nextDouble() < (TEConfig.entitySpawnChance / 50))) return;
        if (world.getDifficulty() == Difficulty.PEACEFUL) return;

        long daysPlayed = world.getDayTime() / 24000;
        if (daysPlayed < 24) return;

        List<ServerPlayer> players = world.players();
        if (players.isEmpty()) return;
        ServerPlayer selectedPlayer = players.get(world.random.nextInt(players.size()));

        double playerX = selectedPlayer.getX();
        double playerY = selectedPlayer.getY();
        double playerZ = selectedPlayer.getZ();

        EntityType<Unknown> type = TEEntities.UNKNOWN.get();
        Unknown entity = type.create(player.level());
        if (entity == null) return;
        entity.setPersistenceRequired();
        entity.behavior = Unknown.UnknownBehavior.STALKING;

        //Random direction pick
        java.util.List<String> directions = new java.util.ArrayList<>();
        directions.add("EAST");
        directions.add("WEST");
        directions.add("SOUTH");
        directions.add("NORTH");
        java.util.Collections.shuffle(directions);

        for (String dir : directions) {
            if (world.random.nextDouble() >= 0.25) { continue; }

            double spawnX = playerX;
            double spawnZ = playerZ;

            spawnZ = switch (dir) {
                case "EAST" -> {
                    spawnX = playerX + 1.0;
                    yield playerZ;
                }
                case "WEST" -> {
                    spawnX = playerX - 1.0;
                    yield playerZ;
                }
                case "SOUTH" -> {
                    spawnX = playerX;
                    yield playerZ + 1.0;
                }
                case "NORTH" -> {
                    spawnX = playerX;
                    yield playerZ - 1.0;
                }
                default -> spawnZ;
            };

            double spawnY = playerY + 0.2;
            double entityDirZ = playerZ - spawnZ;
            double entityDirX = playerX - spawnX;
            float entityYaw = (float) (Math.toDegrees(Math.atan2(entityDirZ, entityDirX)) - 90.0);

            entity.moveTo(spawnX, spawnY, spawnZ, entityYaw, 0.0F);
            break;
        }

        //Spawn entity
        world.addFreshEntity(entity);
        if (!entity.isRemoved() && entity.level() == world) { //Make sure it won't set a false 'true'
            TEVariables.getLevelData(entity.level()).setUnknownInWorld(true);
            NotAlone.grantAdvancement(selectedPlayer);
        }
    }
}
