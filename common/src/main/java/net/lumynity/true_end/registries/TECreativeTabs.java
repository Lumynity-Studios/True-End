package net.lumynity.true_end.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.lumynity.true_end.TrueEnd;

public class TECreativeTabs {

    public static final DeferredRegister<CreativeModeTab> REGISTRY =
            DeferredRegister.create(TrueEnd.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final DeferredSupplier<CreativeModeTab> TRUE_END = REGISTRY.register("true_end", () ->
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0).title(Component.translatable("item_group.true_end.true_end")).icon(() -> new ItemStack(TEBlocks.GRASS_BLOCK.get())).displayItems((parameters, tabData) -> {
                tabData.accept(TEBlocks.GRASS_BLOCK.get().asItem());
                tabData.accept(TEBlocks.DIRT.get().asItem());
                tabData.accept(TEBlocks.FARMLAND.get().asItem());
                tabData.accept(TEBlocks.FLOWER.get().asItem());
                tabData.accept(TEBlocks.ROSE.get().asItem());
                tabData.accept(TEBlocks.SAPLING.get().asItem());
                tabData.accept(TEBlocks.LEAVES.get().asItem());
                tabData.accept(TEBlocks.WOOD.get().asItem());
                tabData.accept(TEBlocks.WOOD_6_SIDED.get().asItem());
                tabData.accept(TEBlocks.WOODEN_PLANKS.get().asItem());
                tabData.accept(TEBlocks.WOODEN_STAIRS.get().asItem());
                tabData.accept(TEBlocks.WOODEN_SLAB.get().asItem());
                tabData.accept(TEBlocks.FENCE.get().asItem());
                tabData.accept(TEBlocks.FENCE_GATE.get().asItem());
                tabData.accept(TEBlocks.DOOR.get().asItem());
                tabData.accept(TEBlocks.TRAPDOOR.get().asItem());
                tabData.accept(TEBlocks.PRESSURE_PLATE.get().asItem());
                tabData.accept(TEBlocks.BUTTON.get().asItem());
                tabData.accept(TEBlocks.STONE.get().asItem());
                tabData.accept(TEBlocks.COAL_ORE.get().asItem());
                tabData.accept(TEBlocks.IRON_ORE.get().asItem());
                tabData.accept(TEBlocks.GOLD_ORE.get().asItem());
                tabData.accept(TEBlocks.REDSTONE_ORE.get().asItem());
                tabData.accept(TEBlocks.DIAMOND_ORE.get().asItem());
                tabData.accept(TEBlocks.COBBLESTONE.get().asItem());
                tabData.accept(TEBlocks.MOSSY_COBBLESTONE.get().asItem());
                tabData.accept(TEBlocks.GRAVEL.get().asItem());
                tabData.accept(TEBlocks.SAND.get().asItem());
                tabData.accept(TEBlocks.GLASS.get().asItem());
                tabData.accept(TEBlocks.OBSIDIAN.get().asItem());
                tabData.accept(TEItems.MYSTERIOUS_CUBE.get());
                tabData.accept(TEItems.DREAMERS_COMPASS.get());
                tabData.accept(TEItems.MUSIC_DISK_FARLANDS.get());
                tabData.accept(TEItems.MUSIC_DISK_NEVER_ALONE.get());
                tabData.accept(TEItems.VOID.get());
            }).build());
    public static void register() {
        // Register the tab itself first
        REGISTRY.register();
    }
}
