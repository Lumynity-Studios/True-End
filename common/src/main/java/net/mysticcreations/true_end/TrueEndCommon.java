package net.mysticcreations.true_end;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.architectury.event.events.common.*;
import net.mysticcreations.true_end.commands.ConfigCmd;
import net.mysticcreations.true_end.commands.DeveloperCmd;
import net.mysticcreations.true_end.config.ConfigSync;
import net.mysticcreations.true_end.config.TEConfig;
import net.mysticcreations.true_end.init.*;
import net.mysticcreations.true_end.procedures.DimSwapToBTD;
import net.mysticcreations.true_end.procedures.DimSwapToNWAD;
import net.mysticcreations.true_end.procedures.PlayerInvManager;
import net.mysticcreations.true_end.procedures.advancements.OnARailTracker;
import net.mysticcreations.true_end.procedures.advancements.WhenPigsFly;
import net.mysticcreations.true_end.procedures.alphafeatures.AlphaFoodSystem;
import net.mysticcreations.true_end.procedures.alphafeatures.NoCooldown;
import net.mysticcreations.true_end.procedures.alphafeatures.NoSprint;
import net.mysticcreations.true_end.procedures.alphafeatures.WoolDrop;
import net.mysticcreations.true_end.procedures.compatibility.NostalgicTweaksCompatibiliy;
import net.mysticcreations.true_end.procedures.events.*;
import net.mysticcreations.true_end.procedures.randomevents.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.mysticcreations.true_end.procedures.randomevents.entityspawning.SpawnShortAttack;
import net.mysticcreations.true_end.procedures.randomevents.entityspawning.SpawnStalking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class TrueEndCommon {
    public static final Logger LOGGER = LoggerFactory.getLogger(TrueEndCommon.class);
    public static final String MOD_ID = "true_end";

    public static void init() {
        TickEvent.SERVER_POST.register(server -> processQueue());

        TEConfig.setup();

        TESounds.register();
        TEBlocks.register();
        TEItems.register();
        TECreativeTabs.register();
        TEParticleTypes.register();
        TEPoiTypes.register();
        TEEntities.register();
        TEScreens.register();
        TEPackets.registerServer();

        DeveloperCmd.register();
        ConfigCmd.register();

        queueServerWork(2, TEFireBlocks::register);

        registerEvents();

    }

    public static void registerEvents() {
        TickEvent.SERVER_LEVEL_POST.register(MobStare::onWorldTick);
        TickEvent.PLAYER_POST.register(SpawnStalking::onPlayerTick);
        TickEvent.PLAYER_POST.register(SpawnShortAttack::onPlayerTick);
        TickEvent.PLAYER_POST.register(TimeChange::onPlayerTick);
        TickEvent.PLAYER_POST.register(SoundPlayer::onPlayerTick);
        TickEvent.PLAYER_POST.register(NoSprint::onPlayerTick);
        TickEvent.PLAYER_POST.register(OnARailTracker::onPlayerTick);
        TickEvent.PLAYER_POST.register(BlackScreenSeepingReality::onPlayerTick);
        TickEvent.PLAYER_POST.register(NostalgicTweaksCompatibiliy::onPlayerTick);

        ChatEvent.RECEIVED.register(ChatReplies::onChat);

        PlayerEvent.CHANGE_DIMENSION.register(FoodLvlReset::onChangeDimension);
        PlayerEvent.CHANGE_DIMENSION.register(NoCooldown::onPlayerChangedDimension);
        PlayerEvent.CHANGE_DIMENSION.register(PlayerInvManager::onDimensionChange);
        PlayerEvent.CHANGE_DIMENSION.register(PlayCredits::onDimensionChange);
        PlayerEvent.CHANGE_DIMENSION.register((player, to, from) -> ConfigSync.sendFogToggle(player));
        PlayerEvent.PLAYER_RESPAWN.register(NoBtdEscape::onPlayerRespawn);
        PlayerEvent.PLAYER_RESPAWN.register(NoCooldown::onPlayerRespawn);
        PlayerEvent.PLAYER_RESPAWN.register(DimSwapToNWAD::onPlayerRespawn);
        PlayerEvent.PLAYER_RESPAWN.register((player, bool) -> ConfigSync.sendFogToggle(player));
        PlayerEvent.PLAYER_ADVANCEMENT.register(DimSwapToBTD::onAdvancement);
        PlayerEvent.PLAYER_JOIN.register(ConfigSync::sendFogToggle);
        PlayerEvent.PLAYER_JOIN.register(NoVoidDamage::onPlayerJoin);
        PlayerEvent.PLAYER_JOIN.register(NoCooldown::onPlayerJoin);
        EntityEvent.LIVING_DEATH.register(NoBtdEscape::onPlayerDeath);
        EntityEvent.LIVING_DEATH.register(WhenPigsFly::onPigFallDeath);
        EntityEvent.LIVING_DEATH.register(DimSwapToNWAD::onPlayerDeath);
        EntityEvent.LIVING_HURT.register(NoVoidDamage::onEntityDamaged);
        EntityEvent.LIVING_HURT.register(WoolDrop::onEntityAttacked);
        EntityEvent.LIVING_HURT.register(DimSwapToNWAD::onEntityAttacked);

        InteractionEvent.RIGHT_CLICK_ITEM.register(AlphaFoodSystem::onRightClickItem);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(AlphaFoodSystem::onRightClickBlock);
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

    public static void queueServerWork(int tickDelay, Runnable action) {
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
            queueServerWork(1 + cooldown * i, () -> {
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
                player.server.getAdvancements().getAdvancement(new ResourceLocation(AdvancementID))
        ).isDone();
    }
}
