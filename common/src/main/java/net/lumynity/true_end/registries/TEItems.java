package net.lumynity.true_end.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.content.item.DreamersCompass;
import net.lumynity.true_end.content.item.MusicDiskFarlands;
import net.lumynity.true_end.content.item.MusicDiskNeverAlone;
import net.lumynity.true_end.content.item.MysteriousCube;

public class TEItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(TrueEnd.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> DIRT = block(TEBlocks.DIRT);
    public static final RegistrySupplier<Item> GRASS_BLOCK = block(TEBlocks.GRASS_BLOCK);
    public static final RegistrySupplier<Item> FARMLAND = block(TEBlocks.FARMLAND);
    public static final RegistrySupplier<Item> COBBLESTONE = block(TEBlocks.COBBLESTONE);
    public static final RegistrySupplier<Item> MOSSY_COBBLESTONE = block(TEBlocks.MOSSY_COBBLESTONE);
    public static final RegistrySupplier<Item> STONE = block(TEBlocks.STONE);
    public static final RegistrySupplier<Item> COAL_ORE = block(TEBlocks.COAL_ORE);
    public static final RegistrySupplier<Item> IRON_ORE = block(TEBlocks.IRON_ORE);
    public static final RegistrySupplier<Item> GOLD_ORE = block(TEBlocks.GOLD_ORE);
    public static final RegistrySupplier<Item> REDSTONE_ORE = block(TEBlocks.REDSTONE_ORE);
    public static final RegistrySupplier<Item> DIAMOND_ORE = block(TEBlocks.DIAMOND_ORE);
    public static final RegistrySupplier<Item> LEAVES = block(TEBlocks.LEAVES);
    public static final RegistrySupplier<Item> WOOD = block(TEBlocks.WOOD);
    public static final RegistrySupplier<Item> WOOD_6_SIDED = block(TEBlocks.WOOD_6_SIDED);
    public static final RegistrySupplier<Item> WOODEN_PLANKS = block(TEBlocks.WOODEN_PLANKS);
    public static final RegistrySupplier<Item> WOODEN_STAIRS = block(TEBlocks.WOODEN_STAIRS);
    public static final RegistrySupplier<Item> WOODEN_SLAB = block(TEBlocks.WOODEN_SLAB);
    public static final RegistrySupplier<Item> FENCE = block(TEBlocks.FENCE);
    public static final RegistrySupplier<Item> FENCE_GATE = block(TEBlocks.FENCE_GATE);
    public static final RegistrySupplier<Item> DOOR = doubleBlock(TEBlocks.DOOR);
    public static final RegistrySupplier<Item> TRAPDOOR = block(TEBlocks.TRAPDOOR);
    public static final RegistrySupplier<Item> PRESSURE_PLATE = block(TEBlocks.PRESSURE_PLATE);
    public static final RegistrySupplier<Item> BUTTON = block(TEBlocks.BUTTON);
    public static final RegistrySupplier<Item> OBSIDIAN = block(TEBlocks.OBSIDIAN);
    public static final RegistrySupplier<Item> GRAVEL = block(TEBlocks.GRAVEL);
    public static final RegistrySupplier<Item> GLASS = block(TEBlocks.GLASS);
    public static final RegistrySupplier<Item> SAND = block(TEBlocks.SAND);
    public static final RegistrySupplier<Item> VOID =  block(TEBlocks.VOID);
    public static final RegistrySupplier<Item> SAPLING =  block(TEBlocks.SAPLING);
    public static final RegistrySupplier<Item> FLOWER = block(TEBlocks.FLOWER);
    public static final RegistrySupplier<Item> ROSE = block(TEBlocks.ROSE);

    public static final RegistrySupplier<Item> MYSTERIOUS_CUBE = REGISTRY.register("mysterious_cube", MysteriousCube::new);
    public static final RegistrySupplier<Item> DREAMERS_COMPASS = REGISTRY.register("dreamers_compass", DreamersCompass::new);
    public static final RegistrySupplier<Item> MUSIC_DISK_FARLANDS = REGISTRY.register("music_disc_farlands", MusicDiskFarlands::new);
    public static final RegistrySupplier<Item> MUSIC_DISK_NEVER_ALONE = REGISTRY.register("music_disc_never_alone", MusicDiskNeverAlone::new);

    private static RegistrySupplier<Item> block(RegistrySupplier<Block> block) {
        return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static RegistrySupplier<Item> doubleBlock(RegistrySupplier<Block> block) {
        return REGISTRY.register(block.getId().getPath(), () -> new DoubleHighBlockItem(block.get(), new Item.Properties()));
    }

    public static void register() {
        REGISTRY.register();
    }
}
