package net.lumynity.true_end.registries;

import net.lumynity.true_end.registries.replicates.FlammabilityProperty;

public class TEFireBlocks {
    public static FlammabilityProperty REGISTRY = FlammabilityProperty.createInstance();

    public static FlammabilityProperty.ArsonRegistryReference WOOD = REGISTRY.registerBlockEntry(TEBlocks.WOOD.get(), new FlammabilityProperty.Arsonability(20, 40));
    public static FlammabilityProperty.ArsonRegistryReference WOOD_SIX_SIDED = REGISTRY.registerBlockEntry(TEBlocks.WOOD_6_SIDED.get(), new FlammabilityProperty.Arsonability(20, 40));
    public static FlammabilityProperty.ArsonRegistryReference FENCE = REGISTRY.registerBlockEntry(TEBlocks.FENCE.get(), new FlammabilityProperty.Arsonability(20, 40));
    public static FlammabilityProperty.ArsonRegistryReference FENCE_GATE = REGISTRY.registerBlockEntry(TEBlocks.FENCE_GATE.get(), new FlammabilityProperty.Arsonability(20, 40));
    public static FlammabilityProperty.ArsonRegistryReference DOOR = REGISTRY.registerBlockEntry(TEBlocks.DOOR.get(), new FlammabilityProperty.Arsonability(20, 40));
    public static FlammabilityProperty.ArsonRegistryReference STAIRS = REGISTRY.registerBlockEntry(TEBlocks.WOODEN_STAIRS.get(), new FlammabilityProperty.Arsonability(20, 40));
    public static FlammabilityProperty.ArsonRegistryReference TRAPDOOR = REGISTRY.registerBlockEntry(TEBlocks.TRAPDOOR.get(), new FlammabilityProperty.Arsonability(20, 40));
    public static FlammabilityProperty.ArsonRegistryReference PRESSURE_PLATE = REGISTRY.registerBlockEntry(TEBlocks.PRESSURE_PLATE.get(), new FlammabilityProperty.Arsonability(20, 40));
    public static FlammabilityProperty.ArsonRegistryReference SAPLING = REGISTRY.registerBlockEntry(TEBlocks.SAPLING.get(), new FlammabilityProperty.Arsonability(20, 40));
    public static FlammabilityProperty.ArsonRegistryReference BUTTON = REGISTRY.registerBlockEntry(TEBlocks.BUTTON.get(), new FlammabilityProperty.Arsonability(20, 40));
    public static FlammabilityProperty.ArsonRegistryReference LEAVES = REGISTRY.registerBlockEntry(TEBlocks.LEAVES.get(), new FlammabilityProperty.Arsonability(20, 40));

    public static void register() {
        FlammabilityProperty.register(REGISTRY);
    }

}
