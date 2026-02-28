package io.github.wamel04.crafters_quest.listener;

import io.github.wamel04.crafters_quest.quest.gui.PreviousQuestGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashSet;
import java.util.Set;

public class PreviousQuestGUIListener implements Listener {

    private static Set<Player> pagePlayers = new HashSet<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!PreviousQuestGUI.openGuiMap.containsKey(player))
            return;

        PreviousQuestGUI questGUI = PreviousQuestGUI.openGuiMap.get(player);

        String title = event.getView().getTitle();

        if (!title.startsWith("§0§l클리어한 퀘스트 목록"))
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
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (pagePlayers.contains(player))
            pagePlayers.remove(player);
        else
            PreviousQuestGUI.openGuiMap.remove((Player) event.getPlayer());
    }

}
