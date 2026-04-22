package net.lumynity.true_end.mechanics.logic;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.lumynity.true_end.config.TEConfig;
import net.lumynity.true_end.registries.TEPackets;

import static net.lumynity.true_end.registries.TEDimKeys.BTD;

public class PlayCredits {
    private static boolean hasShownCreditsThisSession = false;

    public static void onDimensionChange(ServerPlayer player, ResourceKey<Level> fromWorld, ResourceKey<Level> toWorld) {
        if (TEConfig.showCredits) { hasShownCreditsThisSession = false; } else { return; }
        if (hasShownCreditsThisSession) return;

        if (fromWorld == BTD && toWorld == Level.OVERWORLD) {
            hasShownCreditsThisSession = true;

            // todo: maybe do something about this
            //TEConfig.updateConfig("creditsToggle", false);

            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            NetworkManager.sendToPlayer(player, TEPackets.SHOW_CREDITS_PACKET, buf);
        }
    }
}