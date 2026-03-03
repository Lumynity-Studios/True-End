package net.mysticcreations.true_end.mechanics.events;

import dev.architectury.event.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.mysticcreations.true_end.TrueEnd;
import net.mysticcreations.true_end.config.TEConfig;
import net.mysticcreations.true_end.init.TESounds;

import java.util.Locale;
import java.util.Random;

import static net.mysticcreations.true_end.init.TEDimKeys.BTD;
import static net.mysticcreations.true_end.mechanics.randomevents.TimeChange.DAY;
import static net.mysticcreations.true_end.mechanics.randomevents.TimeChange.NIGHT;

public class ChatReplies {
    //Detection & Util
    public static EventResult onChat(Player player, Component component) {
        if (!TEConfig.doChatReplies) return EventResult.pass();
        if (!(player instanceof  ServerPlayer serverPlayer)) return EventResult.pass();
        LevelAccessor world = serverPlayer.serverLevel();
        MinecraftServer server = player.getServer();

        if (server != null) server.execute(() -> {
            String msg = component.getString().toLowerCase(Locale.ROOT).trim().replaceAll("[!?.-]+$", "");
            hardcodedReplies(world, msg, serverPlayer);
        });
        return EventResult.pass();
    }
    private static void sendChatReply(LevelAccessor world, String msg, Integer delay) {
        if (!world.isClientSide() && world.getServer() != null) {
            TrueEnd.wait(delay, () -> {
                MinecraftServer server = world.getServer();
                server.getPlayerList().broadcastSystemMessage(Component.literal(msg), false);
            });
        }
    }

    //Main components
    public static void hardcodedReplies(LevelAccessor world, String msg, ServerPlayer player) {
        int delay = (int)((Math.random()*50)+15);
        switch (msg) {
            case "sleep" -> sendChatReply(world, "<§kUnknown§r> No more.", delay);
            case "awake" -> sendChatReply(world, "<§kUnknown§r> You.", delay);
            case "where from" -> sendChatReply(world, "<§kUnknown§r> The fog.", delay);
            case "why is it night" -> nightReply(player);
            case "is anyone there", "anyone there" -> sendChatReply(world, "<§kUnknown§r> Yes.", delay);
            case "who are you", "what is your name", "what's your name", "whats your name"
                    -> sendChatReply(world, "<§kUnknown§r> Unknown. Forgotten.", delay);
            case "nightmare" -> sendChatReply(world, "<§kUnknown§r> Within.", delay);
            case "fuck you" -> punish(player);
            case "where am i" -> sendChatReply(world, "<§kUnknown§r> "+(int)player.getX()+"/"+(int)player.getY()+"/"+(int)player.getZ()+".", delay);
            case "where are you" -> sendChatReply(world,"<§kUnknown§r> U29tZXdoZXJlIGNsb3NlLg==", delay);
            case "hello", "hi", "sup" -> sendChatReply(world, "<§kUnknown§r> Hi.", delay);
            case "go away", "please go away", "leave me alone", "can you leave me alone", "can you go away", "please leave me alone"
                    -> sendChatReply(world, "<§kUnknown§r> I can't.", delay);
            case "song", "sing" -> entitySings(world, delay, player);
            case "i love you" -> sendChatReply(world, "<§kUnknown§r>      ?      ", delay);
            case "voices" -> sendChatReply(world, "<§kUnknown§r> Gods.", delay);

            //Easter eggs
            case "null" -> sendChatReply(world, "<§kUnknown§r> I'm not Null", delay);
            case "the broken script" -> sendChatReply(world, "<§kUnknown§r> Inspiration.", delay);
            case "zarsai", "zarsaivt", "shinhoa", "shinhoaz", "fireydude", "imfireydude", "olt", "one last time", "dario" -> sendChatReply(world, "<SillyMili> <3", 30);
            default -> randomReplies(world, player);
        }
    }
    public static void randomReplies(LevelAccessor world, ServerPlayer player) {
        if (player.level().dimension() == BTD) return;
        if (!(Math.random() < (TEConfig.randomEventChance)/48)) return;
        int delay = 15+(int)(Math.random()*46);
        String name = player.getName().getString().trim();
        String[] messages = {
                "<§kUnknown§r> Nothing is real.",
                "<§kUnknown§r> Don't forget me.",
                "<§kUnknown§r> Remember me " + name + ". Don't forget.",
                "<§kUnknown§r> I see you. They see you. The universe sees you.",
                "<§kUnknown§r> Do you remember me, " + player + "?",
                "<§kUnknown§r> Do you see me?",
                "<§kUnknown§r> Can you see me?"
        };
        String chat = messages[new Random().nextInt(messages.length)];
        sendChatReply(world, chat, delay);
    }

    //More behavior
    private static void nightReply(ServerPlayer player) {
        ServerLevel world = (ServerLevel) player.level();
        int delay = (int)((Math.random()*50)+15);
        long time = world.getDayTime() % 24000;
        boolean isDay = time >= DAY && time < NIGHT;
        if (!isDay) {
            sendChatReply(world, "<§kUnknown§r> Sleep.", delay);
        } else {
            sendChatReply(world, "<§kUnknown§r> It is not.", delay);
        }
    }
    private static void punish(ServerPlayer player) {
        int delay = (int) ((Math.random()*50)+20);
        MinecraftServer server = player.server;
        String playerName = player.getDisplayName().getString();
        String textA = "§7§o["+playerName+"'s game mode has been changed to Adventure Mode by §kUnknown§r§7§o]";
        String textS = "§7§o["+playerName+"'s game mode has been changed to Survival Mode by §kUnknown§r§7§o]";

        TrueEnd.wait(delay, () -> {
            player.setGameMode(GameType.ADVENTURE);
            server.getPlayerList().broadcastSystemMessage(Component.literal(textA), false);
            TrueEnd.wait(6000, () -> {
                player.setGameMode(GameType.SURVIVAL);
                server.getPlayerList().broadcastSystemMessage(Component.literal(textS), false);
            });
        });
    }
    private static void entitySings(LevelAccessor world, Integer delay, ServerPlayer player) {
        sendChatReply(world, "<§kUnknown§r> 4D79206661766F72697465", delay);
        Level level = player.level();

        TrueEnd.wait(delay*2, () -> {
            level.playSound(
                    null,
                    BlockPos.containing(player.getX()+8, player.getY(), player.getZ()-8),
                    TESounds.DAISY_BELL.get(),
                    SoundSource.NEUTRAL, 1, 1);
        });
    }
}