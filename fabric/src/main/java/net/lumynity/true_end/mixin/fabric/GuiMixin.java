package net.lumynity.true_end.mixin.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.lumynity.true_end.registries.TEDimKeys;
import net.lumynity.true_end.client.VersionOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Stack;


@Mixin(Gui.class) // replace with the real class
public abstract class GuiMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow private int screenWidth;

    @WrapOperation(
            method = "renderPlayerHealth(Lnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Gui;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I"
            )
    )
    private int wrapVehicleHearts(Gui instance, LivingEntity vehicle, Operation<Integer> original) {
        if (this.minecraft.player.level().dimension() != TEDimKeys.BTD) {return this.getVehicleMaxHearts(vehicle);}
        return -1;
    }

    private Stack<String> currentProfiler = new Stack<>();

    @Redirect(
            method = "renderPlayerHealth(Lnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V"
            )
    )
    private void logProfilePushes(ProfilerFiller instance, String name) {
        currentProfiler.push(name);
        instance.push(name);
    }

    @Redirect(
            method = "renderPlayerHealth(Lnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"
            )
    )
    private void logProfilePopPushes(ProfilerFiller instance, String name) {
        currentProfiler.pop();
        currentProfiler.push(name);
        instance.pop();
        instance.push(name);
    }

    @Redirect(
            method = "renderPlayerHealth(Lnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"
            )
    )
    private void logProfilePops(ProfilerFiller instance) {
        currentProfiler.pop();
        instance.pop();
    }

    /**
     * Redirects every blit call inside renderFood so we can shift x and y.
     */
    @Redirect(
            method = "renderPlayerHealth(Lnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
            )
    )
    private void redirectBlit(
            GuiGraphics instance, ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight
    ) {
        if (this.minecraft.player.level().dimension() != TEDimKeys.BTD) {
            instance.blit(atlasLocation, x,y,uOffset,vOffset, uWidth, vHeight);
            return;
        }

        if (this.currentProfiler.peek().equals("armor")) {
            instance.blit(atlasLocation, x+100,y-6,uOffset,vOffset, uWidth, vHeight);
        } else if (this.currentProfiler.peek().equals("air")) {
            instance.blit(atlasLocation, x,y-12,uOffset,vOffset, uWidth, vHeight);
        } else {
            instance.blit(atlasLocation, x, y, uOffset, vOffset, uWidth, vHeight);
        }
    }

    @Inject(at = @At("HEAD"), method = "renderExperienceBar", cancellable = true)
    private void renderExperienceBar(CallbackInfo ci) {
        if (minecraft.player.level().dimension() == TEDimKeys.BTD) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "renderHearts", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private int moveHeartsDown(int y) {
        if (this.minecraft.player.level().dimension() != TEDimKeys.BTD) {return y;}
        if (Platform.isModLoaded("nostalgic_tweaks")) {
            return y +7 ;
        } else {
            return y -17;
        }
    }

    // "ordinal" = which int local to target, since there are multiple ints in the method
    @ModifyVariable(
            method = "renderPlayerHealth",
            at = @At("STORE"), // right after it's stored
            ordinal = 4     // <-- need to match the correct int local
    )
    private int modifyBubblesX(int original) {
        if (this.minecraft.player.level().dimension() != TEDimKeys.BTD) {return original;}
        return original - 100; // shift it down 20 px
    }

    // "ordinal" = which int local to target, since there are multiple ints in the method
    @ModifyVariable(
            method = "renderPlayerHealth",
            at = @At("STORE"), // right after it's stored
            ordinal = 5     // <-- need to match the correct int local
    )
    private int modifyBubblesY(int original) {
        if (this.minecraft.player.level().dimension() != TEDimKeys.BTD) {return original;}
        if (Platform.isModLoaded("nostalgic_tweaks")) {
            return original;
        } else {
            return original + 24;  // shift it down 20 px
        }
    }

    @Shadow
    protected abstract int getVehicleMaxHearts(LivingEntity vehicle);

    /*
    Version Overlay, Fabric renderer
     */
    @Shadow() public abstract Font getFont();
    @Inject(at = @At("TAIL"), method = "render")
    public void render(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        if (this.minecraft.player == null) return;
        if (this.minecraft.player.level().dimension() != TEDimKeys.BTD) return;
        this.minecraft.getProfiler().push("demo");
        Component component = Component.literal(VersionOverlay.currentText);

        final int fontSize = 32;
        float guiScaleFactor = (float) this.minecraft.getWindow().getScreenWidth() / (float) this.minecraft.getWindow().getGuiScaledWidth();
        float baseFontHeight = (float) this.minecraft.font.lineHeight;
        float userScale = fontSize / baseFontHeight;

        int i = this.getFont().width(component);
        int x = 6;
        int y = 6;
        int textColor = 0xFFFFFF;
        int textShadowColor = 0xFF3F3F3F;
        int drawX = Math.round(x / userScale);
        int drawY = Math.round(y / userScale);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(1f / guiScaleFactor, 1f / guiScaleFactor, 1f);
        guiGraphics.pose().scale(userScale, userScale, 1f);

        guiGraphics.drawString(minecraft.font, component, drawX + 1, drawY + 1, textShadowColor, false);
        guiGraphics.drawString(minecraft.font, component, drawX, drawY, textColor, false);

        guiGraphics.pose().popPose();
    }

}
