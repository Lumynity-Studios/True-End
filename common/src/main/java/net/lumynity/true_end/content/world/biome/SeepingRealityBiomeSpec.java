package net.lumynity.true_end.content.world.biome;

import net.lumynity.true_end.registries.TEBiomes;
import terrablender.api.ParameterUtils;
import terrablender.api.VanillaParameterOverlayBuilder;

public class SeepingRealityBiomeSpec {
    // Common biome code
    public static void biome(VanillaParameterOverlayBuilder builder) {
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.NEUTRAL) // moderate
                .humidity(ParameterUtils.Humidity.DRY) // narrowed to just DRY
                .continentalness(ParameterUtils.Continentalness.INLAND)
                .erosion(ParameterUtils.Erosion.EROSION_1) // still 2 options
                .weirdness(ParameterUtils.Weirdness.span(
                        ParameterUtils.Weirdness.MID_SLICE_NORMAL_ASCENDING,
                        ParameterUtils.Weirdness.MID_SLICE_NORMAL_DESCENDING)) // slight variety
                .build().forEach(point -> builder.add(point, TEBiomes.SEEPING_REALITY));
    }
}
