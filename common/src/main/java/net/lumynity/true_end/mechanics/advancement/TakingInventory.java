package net.lumynity.true_end.mechanics.advancement;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.lumynity.true_end.registries.TEPackets;

public class TakingInventory {
    private static boolean hasOpenedInventory = false;
    public static void onClientTick(Minecraft mc) {
        if (mc.screen instanceof InventoryScreen) {
            if (!hasOpenedInventory) {
                hasOpenedInventory = true;
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                NetworkManager.sendToServer(TEPackets.OPEN_INVENTORY_PACKET, buf);
            }
        } else {
            hasOpenedInventory = false;
        }
    }
}