package net.lumynity.true_end.mechanics.advancement;

import dev.architectury.event.EventResult;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.lumynity.true_end.TrueEnd;

public class WhenPigsFly {

    public static EventResult onPigFallDeath(LivingEntity entity, DamageSource damageSource) {
        if (!(entity instanceof Pig pig)) return EventResult.pass();
        if (!damageSource.is(DamageTypes.FALL)) return EventResult.pass();

        for (Entity passenger : pig.getPassengers()) {
            if (passenger instanceof ServerPlayer player) {
                Advancement advancement = player.server.getAdvancements().getAdvancement(
                    TrueEnd.asResource("story/flying_pig"));
                if (advancement == null) return EventResult.pass();
                AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
                if (!progress.isDone()) {
                    for (String criterion : progress.getRemainingCriteria()) {
                        player.getAdvancements().award(advancement, criterion);
                    }
                }
            }
        }
        return EventResult.pass();
    }
}