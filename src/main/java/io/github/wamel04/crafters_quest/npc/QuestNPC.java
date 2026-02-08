package io.github.wamel04.crafters_quest.npc;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.npc.page_condition.PageCondition;
import io.github.wamel04.crafters_quest.operation.Operation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestNPC {

    public static Map<String , QuestNPC> questNPCMap = new HashMap<>();

    public static Map<Player, QuestNPC> playerTalkingNPCMap = new HashMap<>();

    String id;
    String name;
    String category;
    String permission;
    List<PageCondition> firstPageConditions;
    Map<String, List<String>> pageOperationStringMap;

    Map<Player, String> playerNextPageMap = new HashMap<>();

    public QuestNPC(String id, String name, String category, String permission, List<PageCondition> firstPageConditions, Map<String, List<String>> pageOperationStringMap) {
        this.id = id;
        this.name = name;
        this.category = category;

        if (permission.equalsIgnoreCase("disabled"))
            this.permission = null;
        else
            this.permission = permission;

        this.firstPageConditions = firstPageConditions;
        this.pageOperationStringMap = pageOperationStringMap;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getPermission() {
        return permission;
    }

    public List<PageCondition> getFirstPageConditions() {
        return firstPageConditions;
    }

    public Map<String, List<String>> getPageOperationStringMap() {
        return pageOperationStringMap;
    }

    public boolean isTalking(Player player) {
        return this.equals(playerTalkingNPCMap.get(player));
    }

    public void openPage(Player player, String page) {
        List<String> pageOperationStrings = pageOperationStringMap.get(page);
        playerNextPageMap.remove(player);

        for (String ops : pageOperationStrings) {
            Operation operation = Operation.parseOperation(ops);

            if (operation != null)
                operation.execute(player, ops, null);
        }
    }

    public void talk(Player player) {
        playerTalkingNPCMap.put(player, this);

        PageCondition.getPage(player, this)
                .thenAcceptAsync(page -> {
                    openPage(player, page);
                }).exceptionally(ex -> {
                            ex.printStackTrace();
                            return null;
                        }
                );;
    }

    public void unTalk(Player player) {
        playerTalkingNPCMap.remove(player);
        playerNextPageMap.remove(player);
        Bukkit.getScheduler().runTask(CraftersQuestPlugin.getInstance(), () -> player.removePotionEffect(PotionEffectType.SLOWNESS));
    }

    public String getPlayerNextPage(Player player) {
        return playerNextPageMap.get(player);
    }

    public void setPlayerNextPage(Player player, String page) {
        playerNextPageMap.put(player, page);
    }

}
