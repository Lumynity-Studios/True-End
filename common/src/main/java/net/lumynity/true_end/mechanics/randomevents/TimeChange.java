package net.lumynity.true_end.mechanics.randomevents;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.lumynity.true_end.config.TEConfig;

public class TimeChange {
    public static final int DAY = 1000;
    public static final int NOON = 6000;
    public static final int NIGHT = 13000;
    public static final int MIDNIGHT = 18000;

    public static void onPlayerTick(Player player) {
        if (!TEConfig.doRandomEvents) return;
        if (!TEConfig.doDaytimeChange) return;
        if (!(player.level() instanceof ServerLevel serverWorld)) return;

        long totalDays = serverWorld.getDayTime() / 24000;
        if (totalDays < 3) return;
        if (totalDays % 4 != 0) return;

        makeNight(serverWorld);
        makeDay(serverWorld);
    }

    public static void makeNight(ServerLevel world) {
        if (!(Math.random() < (TEConfig.randomEventChance/64))) return;
        long time = world.getDayTime() % 24000;
        if (time > DAY && time < NIGHT) {
            long newTime = NIGHT + (long) (Math.random() * (MIDNIGHT - NIGHT));
            long total = world.getDayTime() - time + newTime;
            world.setDayTime(total);
        }
    }

    public static void makeDay(ServerLevel world) {
        if (!(Math.random() < (TEConfig.randomEventChance/64))) return;
        long time = world.getDayTime() % 24000;
        boolean isNight = time >= MIDNIGHT || time < DAY;
        if (isNight) {
            long newTime = DAY + (long) (Math.random() * (NOON - DAY));
            long total = world.getDayTime() - time + newTime;
            world.setDayTime(total);
        }
    }
}
