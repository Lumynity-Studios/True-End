package net.lumynity.true_end.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class WoodenDoor extends DoorBlock {
    public WoodenDoor() {
        super(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR).sound(SoundType.WOOD).strength(3f).noOcclusion().isRedstoneConductor((state, reader, pos) -> false), BlockSetType.OAK);
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

// block path type equivalent for door



}
