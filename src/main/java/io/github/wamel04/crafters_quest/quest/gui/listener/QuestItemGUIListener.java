package io.github.wamel04.crafters_quest.quest.gui.listener;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.item.QuestItem;
import io.github.wamel04.crafters_quest.quest.gui.GUIAction;
import io.github.wamel04.crafters_quest.quest.gui.GUIManager;
import io.github.wamel04.crafters_quest.quest.gui.QuestItemGUI;
import io.github.wamel04.crafters_quest.quest.gui.QuestItemMenuGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class QuestItemGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getOriginalTitle();

        if (!title.startsWith("§1§0§l퀘스트 아이템"))
            return;

        event.setCancelled(true);

        if (!QuestItemGUI.openGuiMap.containsKey(player.getUniqueId()))
            return;

        QuestItemGUI gui = QuestItemGUI.openGuiMap.get(player.getUniqueId());

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
            if (event.isLeftClick()) {
                if (!event.getCurrentItem().hasItemMeta())
                    return;

                ItemMeta itemMeta = event.getCurrentItem().getItemMeta();
                PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

                if (!pdc.has(QuestItemGUI.ITEM_ID_KEY, PersistentDataType.STRING))
                    return;

                String itemId = pdc.get(QuestItemGUI.ITEM_ID_KEY, PersistentDataType.STRING);
                QuestItem questItem = QuestItem.questItemMap.get(itemId);

                if (event.isShiftClick()) {
                    QuestItem.questItemMap.remove(itemId);
                    gui.removeQuestItem(questItem);

                    if (gui.getQuestItems().isEmpty()) {
                        QuestItem.questItemCategories.remove(questItem.getCategory());
                        QuestItemGUI.openGuiMap.remove(player.getUniqueId());

                        if (QuestItemMenuGUI.openGuiMap.containsKey(player.getUniqueId())) {
                            GUIManager.setAction(player, GUIAction.BACK_PREVIOUS_GUI);
                            QuestItemMenuGUI.openGuiMap.get(player.getUniqueId()).open(player);
                        } else {
                            player.closeInventory();
                        }
                    } else {
                        GUIManager.setAction(player, GUIAction.MOVE_PAGE);
                        gui.open(player);
                    }

                    player.sendMessage(CraftersQuestPlugin.PREFIX + "§e" + itemId + " §f아이템을 제거했습니다.");
                } else {
                    player.getInventory().addItem(questItem.toItemStack());
                    player.sendMessage(CraftersQuestPlugin.PREFIX + "§e" + itemId + " §f아이템을 획득했습니다.");
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String title = event.getView().getOriginalTitle();

        if (!title.startsWith("§1§0§l퀘스트 아이템")) {
            return;
        }

        if (GUIManager.getAction(player).equals(GUIAction.MOVE_PAGE)) {
            GUIManager.removeAction(player);
            return;
        }
        if (GUIManager.getAction(player).equals(GUIAction.BACK_PREVIOUS_GUI)) {
            GUIManager.removeAction(player);
            QuestItemGUI.openGuiMap.remove(player.getUniqueId());
            return;
        }

        QuestItemGUI.openGuiMap.remove(player.getUniqueId());

        if (QuestItemMenuGUI.openGuiMap.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().runTaskLater(CraftersQuestPlugin.getInstance(), () -> {
                if (QuestItemMenuGUI.openGuiMap.containsKey(player.getUniqueId())) {
                    GUIManager.setAction(player, GUIAction.BACK_PREVIOUS_GUI);
                    QuestItemMenuGUI.openGuiMap.get(player.getUniqueId()).open(player);
                }
            }, 1);
        }
    }

}
