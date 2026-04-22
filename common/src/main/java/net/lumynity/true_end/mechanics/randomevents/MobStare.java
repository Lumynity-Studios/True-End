package net.lumynity.true_end.mechanics.randomevents;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.phys.AABB;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.config.TEConfig;

import java.util.*;

public class MobStare {
    private static final Map<Mob, Integer> stareMap = new HashMap<>();
    private static final Random RANDOM = new Random();

    private static long lastEventTick = 0;
    private static final long EVENT_COOLDOWN = 4L * 24000L;

    public static void onWorldTick(ServerLevel level) {
            long worldTick = level.getDayTime();
            if (worldTick < lastEventTick + EVENT_COOLDOWN) {
                updateStare(level);
                return;
            }
            if (TEConfig.doRandomEvents) {
                if (stareMap.isEmpty() && RANDOM.nextDouble() < TEConfig.randomEventChance) {
                    lastEventTick = worldTick;
                    TrueEnd.LOGGER.info("[MobStare] Starting stare event");

                    List<ServerPlayer> players = level.players();
                    int maxDuration = 0;
                    for (ServerPlayer player : players) {
                        AABB area = new AABB(player.blockPosition()).inflate(32.0);
                        for (Mob mob : level.getEntitiesOfClass(Mob.class, area)) {
                            int duration = 200 + RANDOM.nextInt(18000 - 200 + 1);
                            stareMap.put(mob, duration);
                            if (duration > maxDuration) maxDuration = duration;
                        }
                    }
                    double seconds = maxDuration / 20.0;
                    String human = seconds >= 60 ? String.format("%.1f minutes", seconds / 60.0)
                            : String.format("%.1f seconds", seconds);
                    TrueEnd.LOGGER.info("MobStare event executed / Lasting {} ticks (~{})", maxDuration, human);
                }
            }
            updateStare(level);
        }

    private static void updateStare(ServerLevel server) {
        Iterator<Map.Entry<Mob, Integer>> iter = stareMap.entrySet().iterator();
        List<ServerPlayer> players = server.players();
        while (iter.hasNext()) {
            Map.Entry<Mob, Integer> entry = iter.next();
            Mob mob = entry.getKey();
            int ticksLeft = entry.getValue() - 1;
            if (ticksLeft <= 0) {
                iter.remove();
                continue;
            }
            entry.setValue(ticksLeft);

            // Freeze movement
            PathNavigation nav = mob.getNavigation();
            nav.stop();

            // Look at nearest player
            ServerPlayer closest = null;
            double bestDist = Double.MAX_VALUE;
            for (ServerPlayer p : players) {
                double d = mob.distanceTo(p);
                if (d < bestDist) {
                    bestDist = d;
                    closest = p;
                }
            }
            if (closest != null) {
                mob.getLookControl().setLookAt(closest, 30.0F, 30.0F);
            }
        }
    }
}
