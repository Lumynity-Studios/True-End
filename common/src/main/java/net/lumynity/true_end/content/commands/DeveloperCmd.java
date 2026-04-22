package net.lumynity.true_end.content.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.content.commands.calls.BTDTest;
import net.lumynity.true_end.content.commands.calls.PrintVars;
import net.lumynity.true_end.content.commands.calls.screens.BlackOverlay;
import net.lumynity.true_end.content.commands.calls.screens.FunnyScreen;
import net.lumynity.true_end.registries.TEPackets;

public class DeveloperCmd {
    public static void register() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
            dispatcher.register(buildCommand());
        });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildCommand() {
        return Commands.literal("trueend").requires(s -> s.hasPermission(4))
                .then(Commands.literal("testScreen")
                        .then(Commands.literal("credits").executes(arguments -> {
                            ServerPlayer player = arguments.getSource().getPlayerOrException();

                            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                            NetworkManager.sendToPlayer(player, TEPackets.SHOW_CREDITS_PACKET, buf);

                            return 0;
                        }))
                        .then(Commands.literal("funny").executes(arguments -> {
                            ServerPlayer player = arguments.getSource().getPlayer();

                            FunnyScreen.call(player);
                            return 0;
                        }))
                        .then(Commands.literal("black").executes(arguments -> {
                            ServerPlayer player = arguments.getSource().getPlayer();
                            try {
                                BlackOverlay.call(player);
                            } catch (Exception e) {
                                TrueEnd.LOGGER.error("Failed to open black screen!", e);
                            }
                            return 0;
                        }))
                )
                .then(Commands.literal("printVars").executes(arguments -> {
                    Level world = arguments.getSource().getLevel();
                    Entity entity = arguments.getSource().getEntity();

                    if (entity instanceof ServerPlayer player) {
                        PrintVars.execute(world, player, arguments.getSource());
                    }
                    return 0;
                }))
                .then(Commands.literal("testBTD").executes(arguments -> {
                    Level world = arguments.getSource().getLevel();
                    Entity entity = arguments.getSource().getEntity();

                    BTDTest.execute(world, entity);
                    return 0;
                }));
    }
}
