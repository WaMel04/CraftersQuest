package io.github.wamel04.crafters_quest.quest.gui.listener;

import io.github.wamel04.crafters_quest.quest.gui.GUIAction;
import io.github.wamel04.crafters_quest.quest.gui.GUIManager;
import io.github.wamel04.crafters_quest.quest.gui.PreviousQuestGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class PreviousQuestGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (!title.startsWith("§0§l클리어한 퀘스트 목록"))
            return;

        event.setCancelled(true);

        if (!PreviousQuestGUI.openGuiMap.containsKey(player.getUniqueId()))
            return;

        PreviousQuestGUI gui = PreviousQuestGUI.openGuiMap.get(player.getUniqueId());

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
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String title = event.getView().getOriginalTitle();

        if (!title.startsWith("§0§l클리어한 퀘스트 목록"))
            return;
        if (GUIManager.getAction(player).equals(GUIAction.MOVE_PAGE))
            GUIManager.removeAction(player);
        else
            PreviousQuestGUI.openGuiMap.remove(player.getUniqueId());
    }

}
