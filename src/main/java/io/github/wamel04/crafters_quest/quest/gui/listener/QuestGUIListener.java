package io.github.wamel04.crafters_quest.quest.gui.listener;

import io.github.wamel04.crafters_quest.CraftersQuestAPI;
import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.QuestState;
import io.github.wamel04.crafters_quest.quest.gui.GUIAction;
import io.github.wamel04.crafters_quest.quest.gui.GUIManager;
import io.github.wamel04.crafters_quest.quest.gui.QuestGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getOriginalTitle();

        if (!title.startsWith("§0§l퀘스트 목록"))
            return;

        event.setCancelled(true);

        if (!QuestGUI.openGuiMap.containsKey(player.getUniqueId()))
            return;

        QuestGUI gui = QuestGUI.openGuiMap.get(player.getUniqueId());

        if (event.getRawSlot() > 53)
            return;
        if (event.getCurrentItem() == null)
            return;
        if (event.getRawSlot() == 45) {
            if (gui.getPage() == 1) {
                player.sendMessage("§6[CraftersQuest] §f이전 페이지가 존재하지 않습니다.");
            } else {
                GUIManager.setAction(player, GUIAction.MOVE_PAGE);
                gui.setPage(gui.getPage() - 1);
                gui.open(player);
            }
        } else if (event.getRawSlot() == 53) {
            if (gui.getPage() == 500) {
                player.sendMessage("§6[CraftersQuest] §f페이지의 끝입니다.");
            } else {
                GUIManager.setAction(player, GUIAction.MOVE_PAGE);
                gui.setPage(gui.getPage() + 1);
                gui.open(player);
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

                    CraftersQuestAPI.setQuestState(gui.getOwner(), questId, QuestState.NOT_REQUESTED)
                            .thenRunAsync(() -> {
                                GUIManager.setAction(player, GUIAction.MOVE_PAGE);
                                gui.open(player);

                                if (gui.getOwner().equals(player.getUniqueId().toString()))
                                    player.sendMessage("§6[CraftersQuest] §e" + quest.getName() + " 퀘스트§f를 포기했습니다.");
                                else
                                    player.sendMessage("§6[CraftersQuest] §e" + quest.getName() + " 퀘스트§f를 포기시켰습니다.");
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
        String title = event.getView().getOriginalTitle();

        if (!title.startsWith("§0§l퀘스트 목록"))
            return;
        if (GUIManager.getAction(player).equals(GUIAction.MOVE_PAGE))
            GUIManager.removeAction(player);
        else
            QuestGUI.openGuiMap.remove(player.getUniqueId());
    }

}
