package net.lumynity.true_end.mechanics.randomevents;

import dev.architectury.event.EventResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.lumynity.true_end.config.TEConfig;

public class NoVoidDamage {
    public static boolean eventActive;

    public static void onPlayerJoin(ServerPlayer player) {
        if (!TEConfig.doRandomEvents) return;
        eventActive = Math.random() < 0.5;
    }

    public static EventResult onEntityDamaged(LivingEntity entity, DamageSource source, float amount) {
        if (!eventActive) return EventResult.pass();
        if (!(entity instanceof ServerPlayer player)) return EventResult.pass();

        if (player.level().dimension().equals(Level.END)) return EventResult.pass();
        if (source == null) return EventResult.pass();
        if (source.is(DamageTypes.FELL_OUT_OF_WORLD) && player.getY() >= -2010) return EventResult.interruptTrue();
        //It's set to -2010 cuz year 2010 is when Alpha 1.1.2_01 released
        return EventResult.pass();
    }
}