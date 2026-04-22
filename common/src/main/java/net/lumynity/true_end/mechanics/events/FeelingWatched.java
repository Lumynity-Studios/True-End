package net.lumynity.true_end.mechanics.events;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.lumynity.true_end.registries.TEDimKeys;
public class FeelingWatched {
	public static void onEntityEndSleep(Player player) {
		if (!player.level().isClientSide() && (player.level().dimension()) == TEDimKeys.BTD)
			player.displayClientMessage(Component.translatable("events.true_end.feelingwatched"), true);
	}
}
