package net.lumynity.true_end.mechanics.alphafeatures;

import net.minecraft.world.entity.player.Player;
import net.lumynity.true_end.registries.TEDimKeys;

public class NoSprint {
    public static void onPlayerTick(Player player) {
        if (player.level().dimension() == TEDimKeys.BTD) {
            player.getFoodData().setFoodLevel(4);
            player.getFoodData().setSaturation(0.0F);
        }
    }
}