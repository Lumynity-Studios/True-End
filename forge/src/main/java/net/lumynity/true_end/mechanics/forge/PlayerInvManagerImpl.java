package net.lumynity.true_end.mechanics.forge;

import dev.architectury.platform.Platform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Map;
import java.util.Random;

public class PlayerInvManagerImpl {

    public static void saveCuriosSlots(CompoundTag root, Player player) {
        CuriosApi.getCuriosInventory(player).ifPresent(curiosInv -> {
            ListTag curiosList = new ListTag();
            Map<String, ICurioStacksHandler> curios = curiosInv.getCurios();
            curios.forEach((id, stackHandler) -> {
                if (stackHandler == null) return;
                var stacks = stackHandler.getStacks();
                for (int i = 0; i < stacks.getSlots(); i++) {
                    ItemStack s = stacks.getStackInSlot(i);
                    if (!s.isEmpty()) {
                        CompoundTag entry = new CompoundTag();
                        entry.putString("Handler", id);
                        entry.putInt("Slot", i);
                        entry.put("Item", s.save(new CompoundTag()));
                        curiosList.add(entry);
                    }
                }
            });
            root.put("CuriosInventory", curiosList);
        });
    }

    public static void clearCuriosSlots(ServerPlayer player) {
        if (!Platform.isModLoaded("curios")) return;
        CuriosApi.getCuriosInventory(player).ifPresent(curiosInv -> {
            Map<String, ICurioStacksHandler> curios = curiosInv.getCurios();
            curios.forEach((id, stackHandler) -> {
                if (stackHandler == null) return;
                var stacks = stackHandler.getStacks();
                int slots = stacks.getSlots();
                for (int i = 0; i < slots; i++) {
                    stacks.setStackInSlot(i, ItemStack.EMPTY);
                }
            });
        });
    }

    public static void restoreCuriosSlots(ServerPlayer player, CompoundTag root) {
        if (root.contains("CuriosInventory", Tag.TAG_LIST) && Platform.isModLoaded("curios")) {
            ListTag curiosList = root.getList("CuriosInventory", Tag.TAG_COMPOUND);
            CuriosApi.getCuriosInventory(player).ifPresent(curiosInv -> {
                Map<String, ICurioStacksHandler> curios = curiosInv.getCurios();
                for (Tag t : curiosList) {
                    CompoundTag entry = (CompoundTag) t;
                    String handlerId = entry.getString("Handler");
                    int slot = entry.getInt("Slot");
                    ItemStack stack = ItemStack.of(entry.getCompound("Item"));
                    ICurioStacksHandler handler = curios.get(handlerId);
                    if (handler == null) continue;
                    var stacks = handler.getStacks();
                    if (slot >= 0 && slot < stacks.getSlots()) {
                        if (new Random().nextDouble() < 0.90) {
                            stacks.setStackInSlot(slot, stack);
                        } else {
                            stacks.setStackInSlot(slot, ItemStack.EMPTY);
                        }
                    }
                }
            });
        }
    }
}
