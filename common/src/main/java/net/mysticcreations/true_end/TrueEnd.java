package net.mysticcreations.true_end;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.architectury.event.events.common.*;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.mysticcreations.true_end.commands.ConfigCmd;
import net.mysticcreations.true_end.commands.DeveloperCmd;
import net.mysticcreations.true_end.config.ConfigSync;
import net.mysticcreations.true_end.config.TEConfig;
import net.mysticcreations.true_end.init.*;
import net.mysticcreations.true_end.mechanics.DimSwapToBTD;
import net.mysticcreations.true_end.mechanics.DimSwapToNWAD;
import net.mysticcreations.true_end.mechanics.alphafeatures.AlphaFoodSystem;
import net.mysticcreations.true_end.mechanics.alphafeatures.NoCooldown;
import net.mysticcreations.true_end.mechanics.alphafeatures.NoSprint;
import net.mysticcreations.true_end.mechanics.alphafeatures.WoolDrop;
import net.mysticcreations.true_end.mechanics.compatibility.NostalgicTweaksCompatibiliy;
import net.mysticcreations.true_end.mechanics.events.BlackScreenSeepingReality;
import net.mysticcreations.true_end.mechanics.events.ChatReplies;
import net.mysticcreations.true_end.mechanics.events.NoBtdEscape;
import net.mysticcreations.true_end.mechanics.logic.FoodLvlReset;
import net.mysticcreations.true_end.mechanics.logic.PlayCredits;
import net.mysticcreations.true_end.mechanics.logic.PlayerInvManager;
import net.mysticcreations.true_end.mechanics.logic.adv.OnARail;
import net.mysticcreations.true_end.mechanics.logic.adv.WhenPigsFly;
import net.mysticcreations.true_end.mechanics.randomevents.MobStare;
import net.mysticcreations.true_end.mechanics.randomevents.NoVoidDamage;
import net.mysticcreations.true_end.mechanics.randomevents.SoundPlayer;
import net.mysticcreations.true_end.mechanics.randomevents.TimeChange;
import net.mysticcreations.true_end.mechanics.randomevents.entityspawning.SpawnShortAttack;
import net.mysticcreations.true_end.mechanics.randomevents.entityspawning.SpawnStalking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class TrueEnd {
    public static final Logger LOGGER = LoggerFactory.getLogger(TrueEnd.class);
    public static final String MOD_ID = "true_end";

    public static void init() {
        TickEvent.SERVER_POST.register(server -> processQueue());

        TEConfig.setup();

        TESounds.register();
        TEBlocks.register();
        TEItems.register();
        TECreativeTabs.register();
        TEParticles.registerParticleTypes();
        TEPoiTypes.register();
        TEEntities.register();
        TEScreens.register();
        TEPackets.registerServer();

        DeveloperCmd.register();
        ConfigCmd.register();

        wait(2, TEFireBlocks::register);

        Events.register();

    }

    private static final ConcurrentLinkedQueue<WorkItem> workQueue = new ConcurrentLinkedQueue<>();
    private static class WorkItem {
        final Runnable task;
        int ticksRemaining;

        WorkItem(Runnable task, int delay) {
            this.task = task;
            this.ticksRemaining = delay;
        }
    }
    public static void wait(int tickDelay, Runnable action) {
        workQueue.add(new WorkItem(action, tickDelay));
    }
    public static void processQueue() {
        for (Iterator<WorkItem> iterator = workQueue.iterator(); iterator.hasNext();) {
            WorkItem item = iterator.next();
            if (item.ticksRemaining <= 0) {
                item.task.run();
                iterator.remove(); // safe to remove in ConcurrentLinkedQueue
            }
        }
    }

    public static void messageWithCooldown(ServerPlayer player, String[] jsonLines, int cooldown) {
        for (int i = 0; i < jsonLines.length; i++) {
            final String rawJson = jsonLines[i];
            wait(1 + cooldown * i, () -> {
                JsonElement jsonElement = JsonParser.parseString(rawJson);
                Component component = Component.Serializer.fromJson(jsonElement);
                if (component != null) {
                    player.sendSystemMessage(component);
                }
            });
        }
    }

    public static boolean hasAdvancement(ServerPlayer player, String AdvancementID) {
        return player.getAdvancements().getOrStartProgress(
            player.server.getAdvancements().getAdvancement(asResource(AdvancementID))
        ).isDone();
    }
    public static void grantAdvancement(ServerPlayer player, String AdvancementID) {
        Advancement advancement = player.server.getAdvancements().getAdvancement(asResource(AdvancementID));
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) {
            for (String criteria : progress.getRemainingCriteria())
                player.getAdvancements().award(advancement, criteria);
        }
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
    public static String asStringResource(String path) {
        return MOD_ID+":"+path;
    }
    public static ResourceLocation asMod() {
        return new ResourceLocation(MOD_ID);
    }
    public static ResourceLocation asPath(String path) {
        return new ResourceLocation(path);
    }
}
