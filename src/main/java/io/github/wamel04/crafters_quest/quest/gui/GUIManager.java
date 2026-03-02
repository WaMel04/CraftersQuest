package io.github.wamel04.crafters_quest.quest.gui;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager {

    private static final Map<UUID, GUIAction> actionMap = new HashMap<>();

    public static void setAction(Player player, GUIAction action) {
        actionMap.put(player.getUniqueId(), action);
    }

    public static GUIAction getAction(Player player) {
        return actionMap.getOrDefault(player.getUniqueId(), GUIAction.NONE);
    }

    public static void removeAction(Player player) {
        actionMap.remove(player.getUniqueId());
    }

}
