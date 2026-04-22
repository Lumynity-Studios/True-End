package net.lumynity.true_end.registries.replicates;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.lumynity.true_end.mixin.FireBlockAccessor;

import java.util.*;

/**
 * Registry for custom flammability (arson) properties of blocks.
 */
public class FlammabilityProperty {

    private final Map<Block, Arsonability> items;
    private final List<Block> registeredBlocks;

    private FlammabilityProperty() {
        this.items = new LinkedHashMap<>();
        this.registeredBlocks = new ArrayList<>();
    }

    public static FlammabilityProperty createInstance() {
        return new FlammabilityProperty();
    }

    /**
     * Attempts to register a block with its flammability properties.
     */
    private ArsonRegistryRegistrationResult registerBlock(Block block, Arsonability arsonability) {
        if (items.containsKey(block)) {
            return ArsonRegistryRegistrationResult.FAILURE;
        }
        items.put(block, arsonability);
        registeredBlocks.add(block);
        return ArsonRegistryRegistrationResult.SUCCESS;
    }

    /**
     * Public-facing method to register a block entry.
     */
    public ArsonRegistryReference registerBlockEntry(Block block, Arsonability arsonability) {
        ArsonRegistryRegistrationResult result = registerBlock(block, arsonability);
        if (result == ArsonRegistryRegistrationResult.SUCCESS) {
            int index = registeredBlocks.indexOf(block);
            return new ArsonRegistryReference(result, index);
        }
        return new ArsonRegistryReference(result, -1);
    }

    /**
     * Gets the flammability data of a registered block by reference.
     */
    public Arsonability getRegisteredBlockItem(ArsonRegistryReference reference) {
        if (reference.get() < 0 || reference.get() >= registeredBlocks.size()) {
            return null;
        }
        Block block = registeredBlocks.get(reference.get());
        return items.get(block);
    }

    public List<Block> getRegisteredBlocks() {
        return Collections.unmodifiableList(registeredBlocks);
    }

    /**
     * Apply all registered flammability values to the FireBlock instance.
     */
    public static void register(FlammabilityProperty registry) {
        for (Block block : registry.registeredBlocks) {
            Arsonability data = registry.items.get(block);
            if (data == null) continue;

            FireBlock fireBlock = (FireBlock) Blocks.FIRE;
            ((FireBlockAccessor) fireBlock).invokeSetFlammable(block, data.encouragement, data.flammability);

            //TrueEndCommon.LOGGER.info("Registered fire properties for block: {}", block);
        }
    }

    // --- Helper classes/enums ---

    public static class Arsonability {
        public final int encouragement;
        public final int flammability;

        public Arsonability(int encouragement, int flammability) {
            this.encouragement = encouragement;
            this.flammability = flammability;
        }
    }

    public enum ArsonRegistryRegistrationResult {
        SUCCESS,
        FAILURE
    }

    public static class ArsonRegistryReference {
        private final ArsonRegistryRegistrationResult result;
        private final int index;

        public ArsonRegistryReference(ArsonRegistryRegistrationResult result, int index) {
            this.result = result;
            this.index = index;
        }

        public ArsonRegistryRegistrationResult getResult() {
            return result;
        }

        public int get() {
            return index;
        }
    }
}
