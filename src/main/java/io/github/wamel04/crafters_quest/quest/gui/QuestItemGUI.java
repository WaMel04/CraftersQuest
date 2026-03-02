package io.github.wamel04.crafters_quest.quest.gui;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.item.QuestItem;
import io.github.wamel04.crafters_quest.util.Util;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

public class QuestItemGUI {

    public static Map<UUID, QuestItemGUI> openGuiMap = new HashMap<>();

    private static ItemStack leftArrow = Util.getSkull("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9", "§c<- 이전 페이지", null);
    private static ItemStack rightArrow = Util.getSkull("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf", "§a다음 페이지 ->", null);

    private List<QuestItem> questItems;
    private int page;

    public static final NamespacedKey ITEM_ID_KEY = new NamespacedKey(CraftersQuestPlugin.getInstance(),
            "quest_item_id");

    public QuestItemGUI(String category, int page) {
        this.page = page;
        this.questItems = QuestItem.questItemMap.values().stream()
                .filter(questItem -> questItem.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public void open(Player player) {
        openGuiMap.put(player.getUniqueId(), this);

        Inventory inventory = Bukkit.createInventory(null, 54, "§1§0§l퀘스트 아이템 (" + page + ")");

        inventory.setItem(45, leftArrow);
        inventory.setItem(53, rightArrow);

        int slot = 0;
        for (int j = 45 * (page - 1); j < 45 * page; j++) {
            if (questItems.size() <= j)
                break;

            QuestItem questItem = questItems.get(slot);
            ItemStack item = questItem.toItemStack().clone();
            ItemMeta itemMeta = item.getItemMeta();

            List<String> descriptions = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
            descriptions.add("");
            descriptions.add("§e아이템 ID: " + questItem.getId());
            descriptions.add("");
            descriptions.add(ChatColor.of("#7FFFD4") + "좌클릭시 해당 아이템을 획득합니다.");
            descriptions.add("§c쉬프트 + 좌클릭시 해당 아이템을 삭제합니다.");
            descriptions.add("");

            itemMeta.setLore(descriptions);

            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            pdc.set(ITEM_ID_KEY, PersistentDataType.STRING, questItem.getId());

            item.setItemMeta(itemMeta);

            inventory.setItem(slot, item);
            slot++;
        }

        player.openInventory(inventory);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<QuestItem> getQuestItems() {
        return questItems;
    }

    public void removeQuestItem(QuestItem questItem) {
        questItems.remove(questItem);
    }

}
