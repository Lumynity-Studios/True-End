package net.mysticcreations.true_end.init;

import dev.architectury.event.events.common.*;
import net.mysticcreations.true_end.config.ConfigSync;
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

public class Events {
    public static void register() {
        TickEvent.SERVER_LEVEL_POST.register(MobStare::onWorldTick);
        TickEvent.PLAYER_POST.register(SpawnStalking::onPlayerTick);
        TickEvent.PLAYER_POST.register(SpawnShortAttack::onPlayerTick);
        TickEvent.PLAYER_POST.register(TimeChange::onPlayerTick);
        TickEvent.PLAYER_POST.register(SoundPlayer::onPlayerTick);
        TickEvent.PLAYER_POST.register(NoSprint::onPlayerTick);
        TickEvent.PLAYER_POST.register(OnARail::onPlayerTick);
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
}
