package net.lumynity.true_end.content.commands.calls;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.lumynity.true_end.config.TEConfig;
import net.lumynity.true_end.variables.PlayerData;
import net.lumynity.true_end.variables.TEVariables;
import net.lumynity.true_end.variables.WorldData;

public class PrintVars {

    public static void execute(Level world, ServerPlayer player, CommandSourceStack source) {
        if (player == null || source == null)
            return;

        WorldData getVariable = TEVariables.getLevelData(world);

        int btdSpawnX = (int) getVariable.getBtdSpawnX();
        int btdSpawnY = (int) getVariable.getBtdSpawnY();
        int btdSpawnZ = (int) getVariable.getBtdSpawnZ();

        // Player vars
        source.sendSystemMessage(Component.literal("----= Per-Player"));
        for (ServerPlayer otherPlayer : player.server.getPlayerList().getPlayers()) {
            PlayerData vars = TEVariables.getPlayerData(otherPlayer);
            source.sendSystemMessage(
                    Component.literal("beenBeyond (" + otherPlayer.getName().getString() + "): " + vars.getBeenBeyond())
            );
            source.sendSystemMessage(
                    Component.literal("seepingRealityTime (" + otherPlayer.getName().getString() + "): " + vars.getSeepingRealityTime())
            );
        }

        // World vars
        source.sendSystemMessage(Component.literal("\n----= World Variables"));
        source.sendSystemMessage(Component.literal("btdSpawnX/Y/Z: "+btdSpawnX+"/"+btdSpawnY +"/"+btdSpawnZ));

        source.sendSystemMessage(Component.literal("unknownInWorld: " + getVariable.isUnknownInWorld()));
        // Config vars
        source.sendSystemMessage(Component.literal("\n----= Config Variables"));
        source.sendSystemMessage(Component.literal("btdConversationDelay: " + TEConfig.btdConversationDelay));
        source.sendSystemMessage(Component.literal("randomEventChance: " + TEConfig.randomEventChance));
        source.sendSystemMessage(Component.literal("entitySpawnChance: " + TEConfig.entitySpawnChance));
        source.sendSystemMessage(Component.literal("creditsToggle: " + TEConfig.showCredits));
        source.sendSystemMessage(Component.literal("popupsToggle: " + TEConfig.popupsToggle));
        source.sendSystemMessage(Component.literal("fogToggle: " + TEConfig.showFog));
        source.sendSystemMessage(Component.literal("daytimeChangeToggle: " + TEConfig.doDaytimeChange));
        source.sendSystemMessage(Component.literal("clearDreamItems: " + TEConfig.doDaytimeChange));
        source.sendSystemMessage(Component.literal("flashingLights: " + TEConfig.doFlashingLights));
    }
}
