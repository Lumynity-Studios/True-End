package net.lumynity.true_end.client.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import net.lumynity.true_end.client.CommonClient;
import net.lumynity.true_end.content.entity.renderer.UnknownEntityRenderer;
import net.lumynity.true_end.registries.TEBlocks;
import net.lumynity.true_end.registries.TEEntities;
import net.lumynity.true_end.registries.TEParticles;

public class FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(TEBlocks.SAPLING.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TEBlocks.GLASS.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TEBlocks.LEAVES.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TEBlocks.ROSE.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TEBlocks.FLOWER.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TEBlocks.TRAPDOOR.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TEBlocks.DOOR.get(), RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(TEBlocks.BEYOND_THE_DREAM_PORTAL.get(), RenderType.cutout());
        EntityRendererRegistry.register(TEEntities.UNKNOWN.get(), UnknownEntityRenderer.UnknownRenderer::new);

        CommonClient.init();

        TEParticles.registerParticles();
    }
}
