package net.lumynity.true_end.mechanics.randomevents;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.config.TEConfig;
import net.lumynity.true_end.registries.TEBlocks;

import static net.minecraft.world.level.block.Blocks.*;
import static net.lumynity.true_end.registries.TEDimKeys.NWAD;
import static net.lumynity.true_end.mechanics.DimSwapToBTD.BlockPosRandomX;
import static net.lumynity.true_end.mechanics.DimSwapToBTD.BlockPosRandomZ;

public class SoundPlayer {
    public static void onPlayerTick(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (player.level().dimension() != NWAD) if (player.level().dimension() != Level.OVERWORLD) return;

        if (!TEConfig.doRandomEvents) return;
        if (!(Math.random() < TEConfig.randomEventChance)) return;

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        Level level = player.level();

        //Sound Players
        if (groundBlock(level, x, y, z) == TEBlocks.GRASS_BLOCK.get() || groundBlock(level, x, y, z) == GRASS_BLOCK) {
            if (Math.random() < 0.90) {
                repeatSound(serverPlayer, 8, SoundEvents.GRASS_STEP);
            } else {
                repeatSound(serverPlayer, 8, SoundEvents.GRASS_BREAK);
            }
        }
        if (groundBlock(level, x, y, z) == SAND) {
            if (Math.random() < 0.90) {
                repeatSound(serverPlayer, 8, SoundEvents.SAND_STEP);
            } else {
                repeatSound(serverPlayer, 8, SoundEvents.SAND_BREAK);
            }
        }
        if (groundBlock(level, x, y, z) == TEBlocks.DIRT.get()
                || groundBlock(level, x, y, z) == TEBlocks.GRAVEL.get()
                || groundBlock(level, x, y, z) == DIRT
                || groundBlock(level, x, y, z) == GRAVEL) {
            if (Math.random() < 0.90) {
                repeatSound(serverPlayer, 8, SoundEvents.GRAVEL_STEP);
            } else {
                repeatSound(serverPlayer, 12,  SoundEvents.GRAVEL_BREAK);
            }
        }
        if (groundBlock(level, x, y, z) == TEBlocks.STONE.get() || groundBlock(level, x, y, z) == STONE) {
            if (Math.random() < 0.40) {
                repeatSound(serverPlayer, 8,  SoundEvents.STONE_STEP);
            } else {
                repeatSound(serverPlayer, 10,  SoundEvents.STONE_BREAK);
            }
        }
        if (groundBlock(level, x, y, z) == DEEPSLATE) {
            if (Math.random() < 0.60) {
                repeatSound(serverPlayer, 8,  SoundEvents.DEEPSLATE_STEP);
            } else {
                repeatSound(serverPlayer, 16,  SoundEvents.DEEPSLATE_BREAK);
            }
        }
    }

    public static Block groundBlock(Level level, double x, double y, double z) {
        return level.getBlockState(BlockPos.containing(x, y - 0.5, z)).getBlock();
    }

    public static void repeatSound(ServerPlayer player, Integer delay, SoundEvent soundEvent) {
        int soundX = BlockPosRandomX / 4;
        int soundY = 1 + (int) (Math.random() * ((8 - 1) + 1));
        int soundZ = BlockPosRandomZ / 4;
        Level level = player.level();
        if (level.isClientSide()) return;

        TrueEnd.queueServerWork(delay, () -> {
            level.playSound(
                    null,
                    BlockPos.containing(soundX, soundY, soundZ),
                    soundEvent,
                    SoundSource.NEUTRAL, 1, 1);
        });
    }
}