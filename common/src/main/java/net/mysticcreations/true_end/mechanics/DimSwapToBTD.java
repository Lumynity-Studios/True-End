package net.mysticcreations.true_end.mechanics;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.mysticcreations.true_end.TrueEnd;
import net.mysticcreations.true_end.config.TEConfig;
import net.mysticcreations.true_end.init.TEBlocks;
import net.mysticcreations.true_end.mechanics.logic.PlayerInvManager;
import net.mysticcreations.true_end.variables.TEVariables;
import net.mysticcreations.true_end.variables.WorldData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

import static net.mysticcreations.true_end.init.TEDimKeys.BTD;

public class DimSwapToBTD {
    private static final Map<ServerPlayer, Boolean> HAS_PROCESSED = new HashMap<>();
    public static final int HOUSE_PLATEAU_WIDTH = 9;
    public static final int HOUSE_PLATEAU_LENGTH = 7;
    public static final int MAX_TERRAIN_DROP = 7;
    public static final int MAX_TERRAIN_ASCENT = 3;
    public static final int TERRAIN_ADAPT_EXTENSION = 10;
    public static final int MAX_FALLBACK_SEARCH_TRIES = 32;
    public static final BlockPos ABSOLUTE_FALLBACK_POS = new BlockPos(0, 120, 12550832);

    public static void onAdvancement(ServerPlayer player, Advancement advancement) {
        if (!advancement.getId().equals(TrueEnd.asResource("stop_dreaming"))) return;
        if (HAS_PROCESSED.getOrDefault(player, false)) return;
        if (TEVariables.getPlayerData(player).getBeenBeyond()) return;
        if (player.level().dimension() != Level.OVERWORLD) return;
        if (!(player.level() instanceof ServerLevel overworld)) return;
        if (!TrueEnd.hasAdvancement(player, "stop_dreaming")) return;

        ServerLevel nextLevel = player.server.getLevel(BTD);
        if (nextLevel == null) return;
        if (player.level().dimension() == BTD) return;

        HAS_PROCESSED.put(player, true);
        player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
        WorldData levelData = TEVariables.getLevelData(nextLevel.getLevel());
        double btdSpawnX = levelData.getBtdSpawnX();
        double btdSpawnY = levelData.getBtdSpawnY();
        double btdSpawnZ = levelData.getBtdSpawnZ();

        if (btdSpawnY > 0) {
            handleKnownSpawn(player, nextLevel, btdSpawnX, btdSpawnY, btdSpawnZ);
        } else {
            handleNewSpawnSearch(player, nextLevel, overworld);
        }
    }
    private static void handleKnownSpawn(ServerPlayer player, ServerLevel nextLevel, double spawnX, double spawnY, double spawnZ) {
        player.teleportTo(nextLevel, spawnX, spawnY, spawnZ, 0, 0);
        syncClientAfterTeleport(player);
        sendFirstEntryConversation(player);
        executeCommand(nextLevel, player, "function true_end:spawn/global_spawn");

        TEVariables.getPlayerData(player).setBeenBeyond(true);
        HAS_PROCESSED.remove(player);

        PlayerInvManager.saveInvBTD(player);
        // PlayerInvManager.clearCuriosSlots(player);
        if (TEConfig.clearDreamItems) player.getInventory().clearContent();
        player.getInventory().setChanged();
    }
    private static void handleNewSpawnSearch(ServerPlayer player, ServerLevel nextLevel, ServerLevel overworld) {
        PlayerInvManager.saveInvBTD(player);
        // PlayerInvManager.clearCuriosSlots(player);
        if (TEConfig.clearDreamItems) player.getInventory().clearContent();

        TrueEnd.wait(5, () -> {
            BlockPos worldSpawn = overworld.getSharedSpawnPos();
            BlockPos primarySearchPos = locateBiome(nextLevel, worldSpawn, TrueEnd.asStringResource("plains"));
            if (primarySearchPos == null) primarySearchPos = worldSpawn;
            BlockPos spawnPos = findSpawn(nextLevel, primarySearchPos);
            BlockPos secondarySearchPos = locateBiome(nextLevel,
                new BlockPos(16 + (int)(Math.random() * 33), 128 + (int)(Math.random() * 129), 16 + (int)(Math.random() * 33)),
                TrueEnd.asStringResource("plains"));

            if (spawnPos == null) {
                Random rng = new Random();
                for (int i = 0; i <= MAX_FALLBACK_SEARCH_TRIES; i++) {
                    int fx = 16 + rng.nextInt(33);
                    int fy = 128 + rng.nextInt(129);
                    int fz = 16 + rng.nextInt(33);
                    secondarySearchPos = new BlockPos(fx, fy, fz);
                    spawnPos = fallbackSpawn(nextLevel, secondarySearchPos);
                    if (spawnPos != null) break;
                }
            }

            final boolean adaptTerrain;
            final boolean absoluteFallbackPlatform;
            final BlockPos finalSpawnPos;
            if (spawnPos != null) {
                adaptTerrain = true;
                absoluteFallbackPlatform = false;
                finalSpawnPos = spawnPos;
            } else {
                adaptTerrain = false;
                absoluteFallbackPlatform = true;
                finalSpawnPos = ABSOLUTE_FALLBACK_POS;
            }
            final BlockPos finalSecondarySearchPos = secondarySearchPos;

            player.teleportTo(nextLevel, finalSpawnPos.getX() + 0.5, finalSpawnPos.getY(), finalSpawnPos.getZ() + 0.5, player.getYRot(), player.getXRot());
            syncClientAfterTeleport(player);

            TrueEnd.wait(4, () -> {
                if (absoluteFallbackPlatform) executeCommand(nextLevel, player, "function true_end:spawn/farlands_spawn");
                if (adaptTerrain) adaptTerrain(nextLevel, player.blockPosition());

                removeNearbyTrees(nextLevel, player.blockPosition(), 15);
                executeCommand(nextLevel, player, "function true_end:home/build_home");
                setGlobalSpawn(nextLevel, player);
                sendFirstEntryConversation(player);
                TEVariables.getPlayerData(player).setBeenBeyond(true);
                BlockPos spawnToSave = finalSecondarySearchPos != null ? finalSecondarySearchPos : finalSpawnPos;
                TEVariables.getLevelData(nextLevel).setBtdSpawn(spawnToSave.getX(), spawnToSave.getY(), spawnToSave.getZ());

                HAS_PROCESSED.remove(player);
            });
        });
    }
    private static void syncClientAfterTeleport(ServerPlayer player) {
        player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
        for (MobEffectInstance effect : player.getActiveEffects()) {
            player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), effect));
        }
    }

    public static BlockPos findSpawn(ServerLevel world, BlockPos centerPos) {
        int searchRadius = 24;
        for (int y = 75; y >= 64; y--) {
            for (int x = -searchRadius; x <= searchRadius; x++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    BlockPos candidate = centerPos.offset(x, y - centerPos.getY(), z);
                    BlockPos above = candidate.above();
                    BlockPos above2 = above.above();

                    if (world.getBlockState(candidate).is(TEBlocks.GRASS_BLOCK.get())
                            && world.getBiome(candidate).is(TrueEnd.asResource("plains"))
                            && notOnAfuckingHill(world, candidate)
                            && isYInSpawnRange(candidate)
                            && noBadBlocks(world, candidate)
                            && world.isEmptyBlock(above)
                            && world.isEmptyBlock(above2)
                            && world.getBrightness(LightLayer.SKY, above) >= 15
                            && isValidSpawnArea(world, candidate)) {
                        TrueEnd.LOGGER.info("Found ideal spawn: {}", above);
                        return above;
                    }
                }
            }
        }
        return null;
    }
    public static BlockPos fallbackSpawn(ServerLevel world, BlockPos centerPos) {
        int searchRadius = 32;
        for (int y = world.getMaxBuildHeight() - 16; y >= world.getMinBuildHeight() + 8; y--) {
            for (int x = -searchRadius; x <= searchRadius; x++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    BlockPos candidate = centerPos.offset(x, y - centerPos.getY(), z);
                    BlockPos above = candidate.above();
                    if (world.getBlockState(candidate).is(TEBlocks.GRASS_BLOCK.get())
                            && world.getBiome(candidate).is(TrueEnd.asResource("plains"))
                            && notOnAfuckingHill(world, candidate)
                            && isYInSpawnRange(candidate)
                            && noBadBlocks(world, candidate)
                            && world.isEmptyBlock(above)
                            && isValidSpawnArea(world, candidate)) {
                        TrueEnd.LOGGER.info("Found fallback spawn: {}", above);
                        return above;
                    }
                }
            }
        }
        return null;
    }
    public static void setGlobalSpawn(ServerLevel world, ServerPlayer player) {
        TEVariables.getLevelData(world).setBtdSpawn(player.getX(), player.getY(), player.getZ());
    }

    private static Predicate<Holder<Biome>> isBiome(String biomeNamespaced) {
        return biomeHolder -> biomeHolder.unwrapKey()
                .map(biomeKey -> biomeKey.location().toString().equals(biomeNamespaced))
                .orElse(false);
    }
    public static BlockPos locateBiome(ServerLevel world, BlockPos startPosition, String biomeNamespaced) {
        Pair<BlockPos, Holder<Biome>> result = world.getLevel()
                .findClosestBiome3d(isBiome(biomeNamespaced), startPosition, 6400, 32, 64);
        if (result == null) return null;
        return result.getFirst();
    }

    public static void adaptTerrain(ServerLevel world, BlockPos centerPos) {
        BlockPos placePos = new BlockPos(centerPos.getX() - HOUSE_PLATEAU_WIDTH / 2, centerPos.getY() - 1, centerPos.getZ() - HOUSE_PLATEAU_LENGTH / 2);
        int plateauHeight = placePos.getY();
        for (int x = 0; x < HOUSE_PLATEAU_WIDTH; x++) {
            for (int z = 0; z < HOUSE_PLATEAU_LENGTH; z++) {
                BlockPos grassPos = new BlockPos(x + placePos.getX(), plateauHeight, z + placePos.getZ());
                BlockState existing = world.getBlockState(grassPos);
                if (existing.isAir()
                        || existing.getFluidState().is(FluidTags.WATER)
                        || existing.getFluidState().is(FluidTags.LAVA)
                        || existing.is(TEBlocks.GRASS_BLOCK.get())
                        || existing.is(TEBlocks.SAND.get())
                        || existing.is(TEBlocks.FLOWER.get())
                        || existing.is(TEBlocks.ROSE.get())) {

                    placeGrassWithDirt(world, grassPos);
                }
            }
        }
        int radius = TERRAIN_ADAPT_EXTENSION;
        int centerX = placePos.getX() + HOUSE_PLATEAU_WIDTH / 2;
        int centerZ = placePos.getZ() + HOUSE_PLATEAU_LENGTH / 2;
        int maxDist = radius + Math.max(HOUSE_PLATEAU_WIDTH, HOUSE_PLATEAU_LENGTH) / 2;
        // circular terrain adaptation
        for (int dx = -maxDist; dx <= maxDist; dx++) {
            for (int dz = -maxDist; dz <= maxDist; dz++) {
                double distFromCenter = Math.sqrt(dx * dx + dz * dz);
                if (distFromCenter > radius + (double) Math.max(HOUSE_PLATEAU_WIDTH, HOUSE_PLATEAU_LENGTH) / 2)
                    continue;

                int worldX = centerX + dx;
                int worldZ = centerZ + dz;
                int localX = worldX - placePos.getX();
                int localZ = worldZ - placePos.getZ();
                boolean insidePlateau = localX >= 0 && localX < HOUSE_PLATEAU_WIDTH && localZ >= 0 && localZ < HOUSE_PLATEAU_LENGTH;
                if (insidePlateau) continue;

                BlockPos checkPos = new BlockPos(worldX, plateauHeight, worldZ);
                int targetHeight = getLocalMax(world, checkPos);

                int dist = (int) Math.round(distFromCenter) - Math.max(HOUSE_PLATEAU_WIDTH, HOUSE_PLATEAU_LENGTH) / 2;
                if (dist < 0 || dist > radius) continue;

                int height = gradient(targetHeight, plateauHeight, radius, dist);
                height = Math.max(world.getMinBuildHeight(), Math.min(height, world.getMaxBuildHeight() - 1));

                BlockPos grassPos = new BlockPos(worldX, height, worldZ);
                BlockState existing = world.getBlockState(grassPos);
                if (existing.isAir()
                        || existing.getFluidState().is(FluidTags.WATER)
                        || existing.getFluidState().is(FluidTags.LAVA)
                        || existing.is(TEBlocks.GRASS_BLOCK.get())
                        || existing.is(TEBlocks.SAND.get())
                        || existing.is(TEBlocks.FLOWER.get())
                        || existing.is(TEBlocks.ROSE.get())) {

                    placeGrassWithDirt(world, grassPos);
                }
            }
        }
    }
    // Helper method to place grass and fill with dirt until hitting ground
    private static void placeGrassWithDirt(ServerLevel world, BlockPos pos) {
        int y = Math.max(world.getMinBuildHeight(), Math.min(pos.getY(), world.getMaxBuildHeight() - 1));
        BlockPos clampedPos = new BlockPos(pos.getX(), y, pos.getZ());
        BlockState existing = world.getBlockState(clampedPos);
        if (!existing.isAir()
                && !existing.getFluidState().is(FluidTags.WATER)
                && !existing.getFluidState().is(FluidTags.LAVA)
                && !existing.is(TEBlocks.GRASS_BLOCK.get())
                && !existing.is(TEBlocks.SAND.get())
                && !existing.is(TEBlocks.FLOWER.get())
                && !existing.is(TEBlocks.ROSE.get())) {
            return;
        }
        world.setBlock(clampedPos, TEBlocks.GRASS_BLOCK.get().defaultBlockState(), 3);
        BlockPos.MutableBlockPos mutablePos = clampedPos.mutable();

        for (int yy = clampedPos.getY() - 1; yy >= world.getMinBuildHeight(); yy--) {
            mutablePos.setY(yy);
            BlockState current = world.getBlockState(mutablePos);
            // stop when hitting non-replaceable block
            if (!current.isAir()
                    && !current.getFluidState().is(FluidTags.WATER)
                    && !current.getFluidState().is(FluidTags.LAVA)
                    && !current.is(TEBlocks.GRASS_BLOCK.get())
                    && !current.is(TEBlocks.DIRT.get())
                    && !current.is(TEBlocks.SAND.get())
                    && !current.is(TEBlocks.FLOWER.get())
                    && !current.is(TEBlocks.ROSE.get())) {
                break;
            }
            world.setBlock(mutablePos, TEBlocks.DIRT.get().defaultBlockState(), 3);
        }
    }
    // Smooth gradient function
    private static int gradient(int targetHeight, int centerHeight, int maxDist, int dist) {
        float t = (float) dist / maxDist;
        return Math.round(centerHeight * (1 - t) + targetHeight * t);
    }
    public static int getLocalMax(ServerLevel world, BlockPos pos) {
        int maxY = world.getMaxBuildHeight() - 1;
        int max = maxY;

        for (int y = maxY; y >= 0; y--) {
            BlockPos checkPos = new BlockPos(pos.getX(), y, pos.getZ());
            Block block = world.getBlockState(checkPos).getBlock();
            if (block != net.minecraft.world.level.block.Blocks.AIR
                && block != TEBlocks.WOOD.get()
                && block != TEBlocks.LEAVES.get()) {
                if (y < pos.getY()) {
                    return y - 1;
                } else {
                    max = y - 1;
                }
            }
        }
        return max;
    }

    private static boolean isValidSpawnArea(ServerLevel world, BlockPos center) {
        int centerY = getLocalMax(world, new BlockPos(center.getX(), world.getMaxBuildHeight() - 1, center.getZ()));
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                BlockPos pos = new BlockPos(center.getX() + dx, world.getMaxBuildHeight() - 1, center.getZ() + dz);
                int terrainY = getLocalMax(world, pos);

                int deltaY = terrainY - centerY;
                if (deltaY > MAX_TERRAIN_ASCENT || -deltaY > MAX_TERRAIN_DROP) {
                    return false;
                }
            }
        }
        return true;
    }
    public static boolean noBadBlocks(ServerLevel world, BlockPos center) {
        final int R = 3;
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        for (int dx = -R; dx <= R; dx++) {
            for (int dz = -R; dz <= R; dz++) {
                BlockPos atFeet = new BlockPos(cx + dx, cy, cz + dz);
                BlockPos below = new BlockPos(cx + dx, cy - 1, cz + dz);
                BlockPos below2 = below.below();
                BlockState stateAtFeet = world.getBlockState(atFeet);
                BlockState stateBelow = world.getBlockState(below);
                BlockState stateBelow2 = world.getBlockState(below2);

                // Return false if any of these are found in the area
                if (stateAtFeet.is(net.minecraft.world.level.block.Blocks.WATER)
                        || stateBelow.is(net.minecraft.world.level.block.Blocks.WATER)
                        || stateBelow2.is(net.minecraft.world.level.block.Blocks.WATER)
                        || stateAtFeet.is(TEBlocks.SAND.get())
                        || stateBelow.is(TEBlocks.SAND.get())
                        || stateBelow2.is(TEBlocks.SAND.get())) {
                    return false;
                }
            }
        }
        return true;
    }
    public static boolean notOnAfuckingHill(ServerLevel world, BlockPos center) {
        final int STEEPNESS_LIMIT = 2;
        int centerGroundY = getLocalMax(world, new BlockPos(center.getX(), world.getMaxBuildHeight() - 1, center.getZ()));
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                if (dx == 0 && dz == 0)
                    continue;
                BlockPos neighborColumn = new BlockPos(center.getX() + dx, world.getMaxBuildHeight() - 1, center.getZ() + dz);
                int neighborGroundY = getLocalMax(world, neighborColumn);

                if (Math.abs(neighborGroundY - centerGroundY) > STEEPNESS_LIMIT) {
                    return false;
                }
            }
        }
        return true;
    }
    public static boolean isYInSpawnRange(BlockPos pos) {
        int y = pos.getY();
        return y >= 66 && y <= 80;
    }

    // Helper method to identify tree blocks (leaves or logs)
    private static boolean isTreeBlock(Block block) {
        return block.defaultBlockState().is(BlockTags.LEAVES) || block.defaultBlockState().is(BlockTags.LOGS);
    }
    public static void removeNearbyTrees(ServerLevel world, BlockPos center, int radius) {
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        // Initial scan around the center to enqueue leaves/logs
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    mutablePos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    BlockState state = world.getBlockState(mutablePos);
                    Block block = state.getBlock();

                    if (isTreeBlock(block)) {
                        BlockPos foundPos = mutablePos.immutable();
                        world.removeBlock(foundPos, true);
                        queue.add(foundPos);
                        visited.add(foundPos);
                    }
                }
            }
        }
        // Breadth-first loop to remove connected tree blocks
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (Direction direction : Direction.values()) {
                BlockPos neighbor = current.relative(direction);

                if (!visited.contains(neighbor)) {
                    BlockState neighborState = world.getBlockState(neighbor);
                    Block neighborBlock = neighborState.getBlock();

                    if (isTreeBlock(neighborBlock)) {
                        world.removeBlock(neighbor, true);
                        queue.add(neighbor);
                        visited.add(neighbor);
                    }
                }
            }
        }
    }

    private static void executeCommand(ServerLevel world, ServerPlayer player, String command) {
        world.getServer().getCommands().performPrefixedCommand(player.createCommandSourceStack().withSuppressedOutput(), command);
    }

    private static void sendFirstEntryConversation(ServerPlayer player) {
        List<String> jsonLines = new ArrayList<>();

        InputStream file = DimSwapToBTD.class.getClassLoader().getResourceAsStream("assets/true_end/texts/first_entry.txt");
        if (file == null) return;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8))) {
            String line1;
            while ((line1 = br.readLine()) != null) {
                String raw1 = line1.replace("PLAYERNAME", player.getName().getString());
                String line2 = br.readLine();
                String raw2 = (line2 != null) ? line2.replace("PLAYERNAME", player.getName().getString()) : "";
                String combined = raw1 + (raw2.isEmpty() ? "" : "\n" + raw2);
                String escaped = combined.replace("\"", "\\\"");
                jsonLines.add("{\"text\":\"" + escaped + "\"}");
            }
        } catch (Exception e) {
            TrueEnd.LOGGER.error("Error reading {} with exception {}", file, e.getMessage());
            return;
        }

        TrueEnd.wait(45, () ->
            TrueEnd.messageWithCooldown(player, jsonLines.toArray(new String[0]), TEConfig.btdConversationDelay)
        );
    }
}