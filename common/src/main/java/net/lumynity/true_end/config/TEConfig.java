package net.lumynity.true_end.config;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.lumynity.true_end.TrueEnd;
import net.lumynity.true_end.config.serializer.GsonSerializer;
import org.slf4j.event.Level;

import java.util.HashMap;
import java.util.Map;

public class TEConfig {

    public static final GsonSerializer serializer = new GsonSerializer("TrueEnd_COMMON");
    public static Map<String, Object> entries;

    public static double randomEventChance = 0.05;
    public static double entitySpawnChance = 0.05;
    public static int btdConversationDelay = 40;
    public static boolean doRandomEvents = true;
    public static boolean popupsToggle = true;
    public static boolean showFog = true;
    public static boolean showCredits = true;
    public static boolean doFlashingLights = true;
    public static boolean doDaytimeChange = true;
    public static boolean clearDreamItems = true;
    public static boolean doChatReplies = true;

    public static boolean showFogClient;

    public static void setup() {
        Map<String, Object> entries = TEConfig.serializer.deserialize();
        entries = entries == null ? new HashMap<>() : entries;

        entries.putIfAbsent("randomEventChance", 0.005d);
        entries.putIfAbsent("entitySpawnChance", 0.05d);
        entries.putIfAbsent("btdConversationDelay", 40d);
        entries.putIfAbsent("doRandomEvents", true);
        entries.putIfAbsent("popupsToggle", true);
        entries.putIfAbsent("showFog", true);
        entries.putIfAbsent("showCredits", true);
        entries.putIfAbsent("doFlashingLights", true);
        entries.putIfAbsent("doDaytimeChange", true);
        entries.putIfAbsent("clearDreamItems", true);
        entries.putIfAbsent("showFogClient", true);
        entries.putIfAbsent("doChatReplies", true);

        randomEventChance = (double) entries.get("randomEventChance");
        entitySpawnChance = (double) entries.get("entitySpawnChance");
        btdConversationDelay = Math.toIntExact(Math.round((double) entries.get("btdConversationDelay")));
        doRandomEvents = (boolean) entries.get("doRandomEvents");
        popupsToggle = (boolean) entries.get("popupsToggle");
        showFog = (boolean) entries.get("showFog");
        showCredits = (boolean) entries.get("showCredits");
        doFlashingLights = (boolean) entries.get("doFlashingLights");
        doDaytimeChange = (boolean) entries.get("doDaytimeChange");
        clearDreamItems = (boolean) entries.get("clearDreamItems");
        doChatReplies = (boolean) entries.get("doChatReplies");

        TEConfig.entries = entries;
        serializer.serialize(TEConfig.entries);
        TrueEnd.LOGGER.info("[TrueEnd] Successfully loaded TrueEnd Config!");
        TrueEnd.LOGGER.atLevel(Level.DEBUG).log(TEConfig.serializer.getMessage());
    }

    public static void updateConfig(String key, Object value) {
        entries.put(key, value);
        serializer.serialize(entries);

        switch (key) {
            case "randomEventChance" -> randomEventChance = (double)  value;
            case "entitySpawnChance" -> entitySpawnChance = (double)  value;
            case "btdConversationDelay" -> btdConversationDelay = (int) value;
            case "showCredits" -> showCredits = (boolean) value;
            case "showFog" -> showFog = (boolean) value;
            case "popupsToggle" -> popupsToggle = (boolean) value;
            case "doDaytimeChange" -> doDaytimeChange = (boolean) value;
            case "clearDreamItems" -> clearDreamItems = (boolean) value;
            case "doFlashingLights" -> doFlashingLights = (boolean) value;
            case "doChatReplies" -> doChatReplies = (boolean) value;
            default -> TrueEnd.LOGGER.warn("updateConfig: unhandled key '{}'", key);
        }
    }

    public static int handleBoolean(CommandSourceStack src, String key, boolean value) {
        try {
            updateConfig(key, value);
        } catch (Exception e) {
            src.sendFailure(Component.literal("Failed to update config '" + key + "': " + e.getMessage()));
            return 0;
        }
        src.sendSuccess(() -> Component.literal("Config '" + key + "' set to " + value), false);
        return 1;
    }
    public static int handleDouble(CommandSourceStack src, String key, double value) {
        try {
            updateConfig(key, value);
        } catch (Exception e) {
            src.sendFailure(Component.literal("Failed to update config '" + key + "': " + e.getMessage()));
            return 0;
        }
        src.sendSuccess(() -> Component.literal("Config '" + key + "' set to " + value), false);
        return 1;
    }
    public static int handleInt(CommandSourceStack src, String key, int value) {
        return handleDouble(src, key, value);
    }
}
