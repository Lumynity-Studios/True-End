
package net.lumynity.true_end.content.commands.calls.screens;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import io.netty.buffer.ByteBufAllocator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.lumynity.true_end.registries.TEScreens;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BlackOverlay extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
    public final Level world;
    public final Player entity;
    public int x, y, z;
    private ContainerLevelAccess access = ContainerLevelAccess.NULL;
    private final Map<Integer, Slot> customSlots = new HashMap<>();
    private final Supplier<Boolean> boundItemMatcher = null;
    private final Entity boundEntity = null;
    private final BlockEntity boundBlockEntity = null;

    public BlackOverlay(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(TEScreens.BLACK_SCREEN.get(), id);
        this.entity = inv.player;
        this.world = inv.player.level();
        BlockPos pos;
    }

    @Override
    public boolean stillValid(Player player) {
        boolean bound = false;
        if (bound) {
            if (this.boundItemMatcher != null)
                return this.boundItemMatcher.get();
            else if (this.boundBlockEntity != null)
                return AbstractContainerMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
            else if (this.boundEntity != null)
                return this.boundEntity.isAlive();
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        return ItemStack.EMPTY;
    }

    public Map<Integer, Slot> get() {
        return customSlots;
    }

    public static void call(ServerPlayer player) {
        MenuRegistry.openExtendedMenu(player, new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(FriendlyByteBuf buf) {

            }

            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                var buf = new FriendlyByteBuf(ByteBufAllocator.DEFAULT.buffer());
                saveExtraData(buf);
                return new BlackOverlay(id, inventory, buf);
            }


            @Override
            public Component getDisplayName() {
                return Component.literal("black screen");
            }
        });
    }
}
