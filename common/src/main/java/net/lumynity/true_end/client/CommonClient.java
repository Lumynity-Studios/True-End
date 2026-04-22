package net.lumynity.true_end.client;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.client.screens.BlackScreen;
import net.lumynity.true_end.client.screens.FunnyScreen;
import net.lumynity.true_end.content.entity.renderer.UnknownEntityRenderer;
import net.lumynity.true_end.registries.TEEntities;
import net.lumynity.true_end.registries.TEItems;
import net.lumynity.true_end.registries.TEPackets;
import net.lumynity.true_end.registries.TEScreens;
import net.lumynity.true_end.mechanics.advancement.TakingInventory;
import net.lumynity.true_end.mixin.DimSpecialEffectsAccessor;
import net.lumynity.true_end.client.dimension.BeyondTheDream;

@Environment(EnvType.CLIENT)
public final class CommonClient {

    public static void init() {

        EntityRendererRegistry.register(
                TEEntities.UNKNOWN,
                UnknownEntityRenderer.UnknownRenderer::new
        );

        ItemPropertiesRegistry.register(
                TEItems.DREAMERS_COMPASS.get(), TrueEnd.asPath("angle"),
                (stack, world, entity, seed) -> {

                    if (world == null || entity == null) {
                        return 0F;
                    }
                    CompoundTag tag = stack.getOrCreateTag();

                    if (!tag.getBoolean("LodestoneTracked")) {
                        return 0F;
                    }

                    BlockPos targetPos = new BlockPos(
                            tag.getInt("LodestonePosX"),
                            tag.getInt("LodestonePosY"),
                            tag.getInt("LodestonePosZ")
                    );
                    BlockPos playerPos = entity.blockPosition();
                    float playerYaw = entity.getYRot(); // in degrees

                    // Delta vector from player to target
                    double dx = targetPos.getX() - playerPos.getX();
                    double dz = targetPos.getZ() - playerPos.getZ();

                    // Angle to target in degrees (0 = east, 90 = south)
                    double targetAngle = Math.toDegrees(Math.atan2(dz, dx));
                    targetAngle = (targetAngle - 90.0) % 360.0; // Fix 90° counter-clockwise offset

                    // Normalize both angles
                    targetAngle = (targetAngle + 360.0) % 360.0;
                    playerYaw = (playerYaw + 360.0f) % 360.0f;

                    // Relative angle between where player is looking and where target is
                    double relative = (targetAngle - playerYaw + 360.0) % 360.0;

                    // Now convert to 0.0 - 1.0 float for the predicate
                    float angleValue = (float) (relative / 360.0);

                    return angleValue;
                }
        );

        MenuRegistry.registerScreenFactory(TEScreens.BLACK_SCREEN.get(), BlackScreen::new);
        MenuRegistry.registerScreenFactory(TEScreens.FUNNY_SCREEN.get(), FunnyScreen::new);

        ClientTickEvent.CLIENT_POST.register(VersionOverlay::onClientTick);
        ClientTickEvent.CLIENT_POST.register(TakingInventory::onClientTick);

        TEPackets.registerClient();
        DimSpecialEffectsAccessor.getEffects().put(TrueEnd.asResource("btd"), new BeyondTheDream());
        DimSpecialEffectsAccessor.getEffects().put(TrueEnd.asResource("nwad"), new BeyondTheDream());
    }
}
