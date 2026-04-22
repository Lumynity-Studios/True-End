package net.lumynity.true_end.content.commands.calls.screentests;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.lumynity.true_end.client.CreditsScreen;
import net.lumynity.true_end.registries.TESounds;

public class TestCredits {
    private static final Minecraft mc = Minecraft.getInstance();
    public static void execute(double x, double y, double z) {
        mc.execute(() -> {
            mc.getSoundManager().stop();

            if (mc.level != null && mc.player != null) {
                mc.getSoundManager().stop();
                if (mc.level != null && mc.player != null) {
                    mc.level.playLocalSound(x, y, z, TESounds.MOD_CREDITS_MUSIC.get(), SoundSource.MASTER, 1, 1, false);
                }
                mc.execute(() -> mc.setScreen(new CreditsScreen()));
            }
        });
    }
}