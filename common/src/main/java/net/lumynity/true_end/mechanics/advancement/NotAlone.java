package net.lumynity.true_end.mechanics.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.level.ServerPlayer;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.config.TEConfig;

import static net.lumynity.true_end.registries.TEDimKeys.BTD;

public class NotAlone {
    public static void grantAdvancement(ServerPlayer player) {
        if (!((player.level().dimension()) == BTD)) return;
        if (!TEConfig.doRandomEvents) return;
        if (!(Math.random() < TEConfig.randomEventChance)) return;

        Advancement advancement = player.server.getAdvancements().getAdvancement(TrueEnd.asResource("not_alone"));
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) {
            for (String criteria : progress.getRemainingCriteria())
                player.getAdvancements().award(advancement, criteria);
        }
    }
}