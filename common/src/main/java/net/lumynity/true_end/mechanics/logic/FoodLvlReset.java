package net.lumynity.true_end.mechanics.logic;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.lumynity.true_end.registries.TEDimKeys;

public class FoodLvlReset {
    public static void onChangeDimension(ServerPlayer player, ResourceKey<Level> fromDimension, ResourceKey<Level> toDimension) {

        if (fromDimension == TEDimKeys.BTD && toDimension == Level.OVERWORLD) {
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(10.0f);
        }
    }
}
