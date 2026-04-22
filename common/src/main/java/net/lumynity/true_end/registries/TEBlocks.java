package net.lumynity.true_end.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.lumynity.true_end.content.block.*;
import net.lumynity.true_end.content.block.Void;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.lumynity.true_end.TrueEnd;

public class TEBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(TrueEnd.MOD_ID, Registries.BLOCK);

    public static final RegistrySupplier<Block> DIRT = REGISTRY.register("dirt", Dirt::new);
    public static final RegistrySupplier<Block> GRASS_BLOCK = REGISTRY.register("grass_block", GrassBlock::new);
    public static final RegistrySupplier<Block> FARMLAND = REGISTRY.register("farmland", Farmland::new);
    public static final RegistrySupplier<Block> COBBLESTONE = REGISTRY.register("cobblestone", Cobblestone::new);
    public static final RegistrySupplier<Block> MOSSY_COBBLESTONE = REGISTRY.register("mossy_cobblestone", MossyCobblestone::new);
    public static final RegistrySupplier<Block> STONE = REGISTRY.register("stone", Stone::new);
    public static final RegistrySupplier<Block> COAL_ORE = REGISTRY.register("coal_ore", CoalOre::new);
    public static final RegistrySupplier<Block> IRON_ORE = REGISTRY.register("iron_ore", IronOre::new);
    public static final RegistrySupplier<Block> GOLD_ORE = REGISTRY.register("gold_ore", GoldOre::new);
    public static final RegistrySupplier<Block> REDSTONE_ORE = REGISTRY.register("redstone_ore", RedstoneOre::new);
    public static final RegistrySupplier<Block> DIAMOND_ORE = REGISTRY.register("diamond_ore", DiamondOre::new);
    public static final RegistrySupplier<Block> WOOD = REGISTRY.register("wood", Wood::new);
    public static final RegistrySupplier<Block> WOODEN_PLANKS = REGISTRY.register("wooden_planks", WoodenPlanks::new);
    public static final RegistrySupplier<Block> WOODEN_STAIRS = REGISTRY.register("wooden_stairs", WoodenStairs::new);
    public static final RegistrySupplier<Block> WOODEN_SLAB = REGISTRY.register("wooden_slab", WoodenSlab::new);
    public static final RegistrySupplier<Block> FENCE = REGISTRY.register("fence", WoodenFence::new);
    public static final RegistrySupplier<Block> FENCE_GATE = REGISTRY.register("fence_gate", WoodenFenceGate::new);
    public static final RegistrySupplier<Block> DOOR = REGISTRY.register("door", WoodenDoor::new);
    public static final RegistrySupplier<Block> TRAPDOOR = REGISTRY.register("trapdoor", WoodenTrapdoor::new);
    public static final RegistrySupplier<Block> PRESSURE_PLATE = REGISTRY.register("pressure_plate", WoodenPressurePlate::new);
    public static final RegistrySupplier<Block> BUTTON = REGISTRY.register("button", WoodenButton::new);
    public static final RegistrySupplier<Block> LEAVES = REGISTRY.register("leaves", Leaves::new);
    public static final RegistrySupplier<Block> OBSIDIAN = REGISTRY.register("obsidian", Obsidian::new);
    public static final RegistrySupplier<Block> GRAVEL = REGISTRY.register("gravel", Gravel::new);
    public static final RegistrySupplier<Block> WOOD_6_SIDED = REGISTRY.register("wood_6_sided", Wood6Sided::new);
    public static final RegistrySupplier<Block> BEYOND_THE_DREAM_PORTAL = REGISTRY.register("beyond_the_dream_portal", BeyondTheDreamPortal::new);
    public static final RegistrySupplier<Block> GLASS = REGISTRY.register("glass", Glass::new);
    public static final RegistrySupplier<Block> SAND = REGISTRY.register("sand", Sand::new);
    public static final RegistrySupplier<Block> FLOWER = REGISTRY.register("flower", Flower::new);
    public static final RegistrySupplier<Block> ROSE = REGISTRY.register("rose", Rose::new);
    public static final RegistrySupplier<Block> SAPLING = REGISTRY.register("sapling", Sapling::new);
    public static final RegistrySupplier<Block> VOID = REGISTRY.register("void", Void::new);

    public static void register() {
        TrueEnd.LOGGER.info("[TRUE_END] Registering TrueEnd Blocks");
        REGISTRY.register();
    }
}
