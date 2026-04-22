package net.lumynity.true_end.mechanics.portal;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.lumynity.true_end.registries.TEBlocks;
import net.lumynity.true_end.registries.TEPoiTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Optional;

public class BeyondTheDreamTeleporter extends PortalForcer {

    private final ServerLevel level;
    private final BlockPos entityEnterPos;

    public BeyondTheDreamTeleporter(ServerLevel worldServer, BlockPos entityEnterPos) {
        super(worldServer);
        this.level = worldServer;
        this.entityEnterPos = entityEnterPos;
    }

    @Override
    public @NotNull Optional<BlockUtil.FoundRectangle> findPortalAround(BlockPos pos, boolean isNether, WorldBorder border) {
        PoiManager poimanager = this.level.getPoiManager();
        int radius = isNether ? 16 : 128;
        poimanager.ensureLoadedAndValid(this.level, pos, radius);
        Optional<PoiRecord> portal = poimanager.getInSquare(
                        record -> record.is(TEPoiTypes.BEYOND_THE_DREAM_PORTAL.getKey()),
                        pos, radius, PoiManager.Occupancy.ANY
                ).filter(record -> border.isWithinBounds(record.getPos()))
                .sorted(Comparator.comparingDouble(r -> r.getPos().distSqr(pos)))
                .filter(record -> level.getBlockState(record.getPos()).hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
                .findFirst();

        return portal.map(poi -> {
            BlockPos p = poi.getPos();
            BlockState state = level.getBlockState(p);
            return BlockUtil.getLargestRectangleAround(p, state.getValue(BlockStateProperties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21,
                    bp -> level.getBlockState(bp).equals(state));
        });
    }

    // createPortal, canHostFrame, canPortalReplaceBlock, and placeEntity remain mostly the same
    // (copy those from your original and adjust references to Forge utilities)

    public PortalInfo getPortalInfo(Entity entity, ServerLevel server) {
        WorldBorder worldBorder = server.getWorldBorder();
        double scale = entity.level().dimensionType().coordinateScale();
        BlockPos dest = worldBorder.clampToBounds(entity.getX() * scale, entity.getY(), entity.getZ() * scale);

        return getExitPortal(entity, dest, worldBorder).map(rectangle -> {
            BlockState portalState = entity.level().getBlockState(entityEnterPos);
            Direction.Axis axis = portalState.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
            Vec3 offset = BeyondTheDreamPortalShape.getRelativePosition(rectangle, axis, entity.position(), entity.getDimensions(entity.getPose()));
            return BeyondTheDreamPortalShape.createPortalInfo(server, rectangle, axis, offset, entity, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
        }).orElse(new PortalInfo(entity.position(), Vec3.ZERO, entity.getYRot(), entity.getXRot()));
    }

    protected Optional<BlockUtil.FoundRectangle> getExitPortal(Entity entity, BlockPos pos, WorldBorder worldBorder) {
        Optional<BlockUtil.FoundRectangle> existing = this.findPortalAround(pos, false, worldBorder);
        if (entity instanceof ServerPlayer && existing.isEmpty()) {
            Direction.Axis axis = entity.level().getBlockState(entityEnterPos).getOptionalValue(NetherPortalBlock.AXIS).orElse(Direction.Axis.X);
            return this.createPortal(pos, axis);
        }
        return existing;
    }

    private boolean canPortalReplaceBlock(BlockPos.MutableBlockPos pos) {
        BlockState state = this.level.getBlockState(pos);
        return state.canBeReplaced() && state.getFluidState().isEmpty();
    }

    public Optional<BlockUtil.FoundRectangle> createPortal(BlockPos pos, Direction.Axis axis) {
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        double d = (double)-1.0F;
        BlockPos blockPos = null;
        double e = (double)-1.0F;
        BlockPos blockPos2 = null;
        WorldBorder worldBorder = this.level.getWorldBorder();
        int i = Math.min(this.level.getMaxBuildHeight(), this.level.getMinBuildHeight() + this.level.getLogicalHeight()) - 1;
        BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();

        for(BlockPos.MutableBlockPos mutableBlockPos2 : BlockPos.spiralAround(pos, 16, Direction.EAST, Direction.SOUTH)) {
            int j = Math.min(i, this.level.getHeight(Heightmap.Types.MOTION_BLOCKING, mutableBlockPos2.getX(), mutableBlockPos2.getZ()));
            int k = 1;
            if (worldBorder.isWithinBounds(mutableBlockPos2) && worldBorder.isWithinBounds(mutableBlockPos2.move(direction, 1))) {
                mutableBlockPos2.move(direction.getOpposite(), 1);

                for(int l = j; l >= this.level.getMinBuildHeight(); --l) {
                    mutableBlockPos2.setY(l);
                    if (this.canPortalReplaceBlock(mutableBlockPos2)) {
                        int m;
                        for(m = l; l > this.level.getMinBuildHeight() && this.canPortalReplaceBlock(mutableBlockPos2.move(Direction.DOWN)); --l) {
                        }

                        if (l + 4 <= i) {
                            int n = m - l;
                            if (n <= 0 || n >= 3) {
                                mutableBlockPos2.setY(l);
                                if (this.canHostFrame(mutableBlockPos2, mutableBlockPos, direction, 0)) {
                                    double f = pos.distSqr(mutableBlockPos2);
                                    if (this.canHostFrame(mutableBlockPos2, mutableBlockPos, direction, -1) && this.canHostFrame(mutableBlockPos2, mutableBlockPos, direction, 1) && (d == (double)-1.0F || d > f)) {
                                        d = f;
                                        blockPos = mutableBlockPos2.immutable();
                                    }

                                    if (d == (double)-1.0F && (e == (double)-1.0F || e > f)) {
                                        e = f;
                                        blockPos2 = mutableBlockPos2.immutable();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (d == (double)-1.0F && e != (double)-1.0F) {
            blockPos = blockPos2;
            d = e;
        }

        if (d == (double)-1.0F) {
            int o = Math.max(this.level.getMinBuildHeight() - -1, 70);
            int p = i - 9;
            if (p < o) {
                return Optional.empty();
            }

            blockPos = (new BlockPos(pos.getX(), Mth.clamp(pos.getY(), o, p), pos.getZ())).immutable();
            Direction direction2 = direction.getClockWise();
            if (!worldBorder.isWithinBounds(blockPos)) {
                return Optional.empty();
            }

            for(int k = -1; k < 2; ++k) {
                for(int l = 0; l < 2; ++l) {
                    for(int m = -1; m < 3; ++m) {
                        BlockState blockState = m < 0 ? TEBlocks.OBSIDIAN.get().defaultBlockState() : Blocks.AIR.defaultBlockState();
                        mutableBlockPos.setWithOffset(blockPos, l * direction.getStepX() + k * direction2.getStepX(), m, l * direction.getStepZ() + k * direction2.getStepZ());
                        this.level.setBlockAndUpdate(mutableBlockPos, blockState);
                    }
                }
            }
        }

        for(int o = -1; o < 3; ++o) {
            for(int p = -1; p < 4; ++p) {
                if (o == -1 || o == 2 || p == -1 || p == 3) {
                    mutableBlockPos.setWithOffset(blockPos, o * direction.getStepX(), p, o * direction.getStepZ());
                    this.level.setBlock(mutableBlockPos, TEBlocks.OBSIDIAN.get().defaultBlockState(), 3);
                }
            }
        }

        BlockState blockState2 = (BlockState) TEBlocks.BEYOND_THE_DREAM_PORTAL.get().defaultBlockState().setValue(NetherPortalBlock.AXIS, axis);

        for(int p = 0; p < 2; ++p) {
            for(int j = 0; j < 3; ++j) {
                mutableBlockPos.setWithOffset(blockPos, p * direction.getStepX(), j, p * direction.getStepZ());
                this.level.setBlock(mutableBlockPos, blockState2, 18);
            }
        }

        return Optional.of(new BlockUtil.FoundRectangle(blockPos.immutable(), 2, 3));
    }

    private boolean canHostFrame(BlockPos originalPos, BlockPos.MutableBlockPos offsetPos, Direction direction, int offsetScale) {
        Direction direction2 = direction.getClockWise();

        for(int i = -1; i < 3; ++i) {
            for(int j = -1; j < 4; ++j) {
                offsetPos.setWithOffset(originalPos, direction.getStepX() * i + direction2.getStepX() * offsetScale, j, direction.getStepZ() * i + direction2.getStepZ() * offsetScale);
                if (j < 0 && !this.level.getBlockState(offsetPos).isSolid()) {
                    return false;
                }

                if (j >= 0 && !this.canPortalReplaceBlock(offsetPos)) {
                    return false;
                }
            }
        }

        return true;
    }
}
