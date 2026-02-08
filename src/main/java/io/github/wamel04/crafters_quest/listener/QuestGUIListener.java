package io.github.wamel04.crafters_quest.listener;

import io.github.wamel04.crafters_quest.CraftersQuestAPI;
import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.QuestState;
import io.github.wamel04.crafters_quest.quest.gui.QuestGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestGUIListener implements Listener {

    private static Set<Player> pagePlayers = new HashSet<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!QuestGUI.openGuiMap.containsKey(player))
            return;

        QuestGUI questGUI = QuestGUI.openGuiMap.get(player);

        String title = event.getView().getOriginalTitle();

        if (!title.startsWith("§0§l퀘스트 목록"))
            return;

        event.setCancelled(true);

        if (event.getRawSlot() > 53)
            return;
        if (event.getCurrentItem() == null)
            return;
        if (event.getRawSlot() == 45) {
            if (questGUI.getCurrentPage() == 1) {
                player.sendMessage("§6[CraftersQuest] §f이전 페이지가 존재하지 않습니다.");
            } else {
                pagePlayers.add(player);
                questGUI.open(player, questGUI.getCurrentPage() - 1);
            }
        } else if (event.getRawSlot() == 53) {
            if (questGUI.getCurrentPage() == 500) {
                player.sendMessage("§6[CraftersQuest] §f페이지의 끝입니다.");
            } else {
                pagePlayers.add(player);
                questGUI.open(player, questGUI.getCurrentPage() + 1);
            }
        } else {
            if (event.isLeftClick() && event.isShiftClick()) {
                String itemName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

                String regex = "\\(([^)]+)\\)";
                Pattern pattern = Pattern.compile(regex);

                Matcher matcher = pattern.matcher(itemName);

                if (!matcher.find())
                    return;

                String questId = matcher.group(1);

                if (Quest.questMap.containsKey(questId)) {
                    Quest quest = Quest.questMap.get(questId);

                    if (!quest.isCancellable()) {
                        player.sendMessage("§6[CraftersQuest] §e" + quest.getName() + " 퀘스트§f는 포기가 불가능합니다.");
                        return;
                    }

                    CraftersQuestAPI.setQuestState(questGUI.getOwner(), questId, QuestState.NOT_REQUESTED)
                            .thenRunAsync(() -> {
                                pagePlayers.add(player);
                                questGUI.open(player, questGUI.getCurrentPage());
                                player.sendMessage("§6[CraftersQuest] §e" + quest.getName() + " 퀘스트§f를 포기했습니다.");
                            }).exceptionally(
                                    ex -> {
                                        ex.printStackTrace();
                                        return null;
                                    }
                            );
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (pagePlayers.contains(player))
            pagePlayers.remove(player);
        else
            QuestGUI.openGuiMap.remove((Player) event.getPlayer());
    }

}
