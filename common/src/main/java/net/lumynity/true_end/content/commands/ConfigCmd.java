package net.lumynity.true_end.content.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.lumynity.true_end.config.ConfigSync;
import net.lumynity.true_end.config.TEConfig;

import static net.lumynity.true_end.config.TEConfig.*;

public class ConfigCmd {
    public static void register() {
    CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
        dispatcher.register(buildCommand());
    });
}

    private static LiteralArgumentBuilder<CommandSourceStack> buildCommand() {
        return Commands.literal("te-config").requires(s -> s.hasPermission(4))
                        .then(Commands.literal("randomEventChance")
                                .executes(ctx -> getConfig(ctx.getSource(), "randomEventChance", TEConfig.randomEventChance))
                                .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 0.5))
                                        .executes(ctx -> handleDouble(ctx.getSource(), "randomEventChance", DoubleArgumentType.getDouble(ctx, "value")))))

                        .then(Commands.literal("entitySpawnChance")
                                .executes(ctx -> getConfig(ctx.getSource(), "entitySpawnChance", TEConfig.entitySpawnChance))
                                .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1))
                                        .executes(ctx -> handleDouble(ctx.getSource(), "entitySpawnChance", DoubleArgumentType.getDouble(ctx, "value")))))

                        .then(Commands.literal("btdConversationDelay")
                                .executes(ctx -> getConfig(ctx.getSource(), "btdConversationDelay", TEConfig.btdConversationDelay))
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 60))
                                        .executes(ctx -> handleInt(ctx.getSource(), "btdConversationDelay", IntegerArgumentType.getInteger(ctx, "value")))))

                        .then(Commands.literal("creditsToggle")
                                .executes(ctx -> getConfig(ctx.getSource(), "creditsToggle", TEConfig.showCredits))
                                .then(Commands.literal("true")
                                        .executes(ctx -> handleBoolean(ctx.getSource(), "creditsToggle", true)))
                                .then(Commands.literal("false")
                                        .executes(ctx -> handleBoolean(ctx.getSource(), "creditsToggle", false))))

                        .then(Commands.literal("fogToggle")
                                .executes(ctx -> getConfig(ctx.getSource(), "fogToggle", TEConfig.showFog))
                                .then(Commands.literal("true")
                                        .executes(ctx -> {
                                            handleBoolean(ctx.getSource(), "fogToggle", true);
                                            ConfigSync.sendFogToggleAll(ctx.getSource().getServer());
                                            return 0;
                                        }))
                                .then(Commands.literal("false")
                                        .executes(ctx -> {handleBoolean(ctx.getSource(), "fogToggle", false);
                                            ConfigSync.sendFogToggleAll(ctx.getSource().getServer());
                                            return 0;
                                        })))

                        .then(Commands.literal("popupsToggle")
                                .executes(ctx -> getConfig(ctx.getSource(), "popupsToggle", TEConfig.popupsToggle))
                                .then(Commands.literal("true")
                                        .executes(ctx -> handleBoolean(ctx.getSource(), "popupsToggle", true)))
                                .then(Commands.literal("false")
                                        .executes(ctx -> handleBoolean(ctx.getSource(), "popupsToggle", false))))

                        .then(Commands.literal("flashingLights")
                                .executes(ctx -> getConfig(ctx.getSource(), "flashingLights", TEConfig.doFlashingLights))
                                .then(Commands.literal("true")
                                        .executes(ctx -> handleBoolean(ctx.getSource(), "flashingLights", true)))
                                .then(Commands.literal("false")
                                        .executes(ctx -> handleBoolean(ctx.getSource(), "flashingLights", false))))

                        .then(Commands.literal("daytimeChangeToggle")
                                .executes(ctx -> getConfig(ctx.getSource(), "daytimeChangeToggle", TEConfig.doDaytimeChange))
                                .then(Commands.literal("true")
                                        .executes(ctx -> handleBoolean(ctx.getSource(), "daytimeChangeToggle", true)))
                                .then(Commands.literal("false")
                                        .executes(ctx -> handleBoolean(ctx.getSource(), "daytimeChangeToggle", false))))

                        .then(Commands.literal("clearDreamItems")
                                .executes(ctx -> getConfig(ctx.getSource(), "clearDreamItems", TEConfig.clearDreamItems))
                                .then(Commands.literal("true")
                                        .executes(ctx -> handleBoolean(ctx.getSource(), "clearDreamItems", true)))
                                .then(Commands.literal("false")
                                        .executes(ctx -> handleBoolean(ctx.getSource(), "clearDreamItems", false))));
    }

    private static int getConfig(CommandSourceStack src, String key, Object value) {
        src.sendSuccess(() -> Component.literal("Config '" + key + "' is currently set to " + value), false);
        return 1;
    }
}
