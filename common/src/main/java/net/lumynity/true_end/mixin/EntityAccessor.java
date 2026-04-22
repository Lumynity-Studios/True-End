package net.lumynity.true_end.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
interface EntityAccessor {

    @Invoker(value = "setSharedFlag")
    void invokeSetSharedFlag(int flag, boolean set);
}