package net.lumynity.true_end.registries;

import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.content.commands.calls.screentests.TestCredits;
import net.lumynity.true_end.config.TEConfig;

public class TEPackets {

    public static ResourceLocation SHOW_CREDITS_PACKET = TrueEnd.asResource("show_credits");
    public static ResourceLocation FOG_TOGGLE =  TrueEnd.asResource("fog_toggle");
    public static ResourceLocation OPEN_INVENTORY_PACKET = TrueEnd.asResource("open_inventory");

    public static void registerClient() {

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SHOW_CREDITS_PACKET, (buf, context) -> {
            Player player = context.getPlayer();
            // Logic
            TestCredits.execute(player.getX(), player.getY(), player.getZ());
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, FOG_TOGGLE, (buf, context) -> {
            TEConfig.showFogClient = buf.readBoolean();
        });

    }

    public static void registerServer() {



    }

}
