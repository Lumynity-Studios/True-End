package net.lumynity.true_end.client;

import net.minecraft.client.Minecraft;
import net.lumynity.true_end.registries.TEDimKeys;

import java.util.Random;

public class VersionOverlay {
    private static final String BASE_TEXT = "Minecraft Alpha v1.1.2_10";
    private static final String[] VERSIONS = {
            "Minecraft Alpha v1.1.2",
            "Minecraft Alpha v1.1.2_01",
            "Minecraft v1.20.1",
            "Minecraft Snapshot -3w21a", //Reference to the "Snapshot Null" series on YT
            "Minecraft Beta v1.0.46", //Reference to andrewgames722 channel on YT
            "Minecraft Alpha v1.0.7", //Reference to the VoidExp series by mark101 on YT, specifically moonglitch.avi video
            "Minecraft Alpha v1.0.16_02" //Reference to the infamous herobrine screenshot
    };

    public static String currentText = BASE_TEXT;
    private static int flashTicks = 4;
    private static final Random random = new Random();

    public static void onClientTick(Minecraft mc) {
        if (mc.level == null || mc.level.dimension() != TEDimKeys.BTD) {
            currentText = BASE_TEXT;
            flashTicks = 0;
            return;
        }

        if (flashTicks > 0) {
            flashTicks--;
            if (flashTicks == 0) {
                currentText = BASE_TEXT;
            }
            // Dynamic String Change
            // 6000 - ticks between each random "glitch"
            // "//2-6 ticks" - "glitch" string show time
        } else if (random.nextInt(6000) == 0) {
            currentText = VERSIONS[random.nextInt(VERSIONS.length)];
            flashTicks = 2 + random.nextInt(5); // 2–4 ticks
        }
    }
}
