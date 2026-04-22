package net.lumynity.true_end.registries;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.content.commands.calls.screens.BlackOverlay;
import net.lumynity.true_end.content.commands.calls.screens.FunnyScreen;

public class TEScreens {
    public static DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(TrueEnd.MOD_ID, Registries.MENU);
    public static final RegistrySupplier<MenuType<BlackOverlay>> BLACK_SCREEN = REGISTRY.register("black_screen", ()->MenuRegistry.ofExtended(BlackOverlay::new));
    public static final RegistrySupplier<MenuType<FunnyScreen>> FUNNY_SCREEN = REGISTRY.register("funny_screen", ()->MenuRegistry.ofExtended(FunnyScreen::new));

    public static void register() {
        REGISTRY.register();
    }
}
