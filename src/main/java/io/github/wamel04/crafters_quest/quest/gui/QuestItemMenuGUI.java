package io.github.wamel04.crafters_quest.quest.gui;

import io.github.wamel04.crafters_quest.item.QuestItem;
import io.github.wamel04.crafters_quest.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class QuestItemMenuGUI {

    public static Map<UUID, QuestItemMenuGUI> openGuiMap = new HashMap<>();

    private static ItemStack leftArrow = Util.getSkull("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9", "§c<- 이전 페이지", null);
    private static ItemStack rightArrow = Util.getSkull("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf", "§a다음 페이지 ->", null);

    private int page;

    public QuestItemMenuGUI(int page) {
        this.page = page;
    }

    public void open(Player player) {
        openGuiMap.put(player.getUniqueId(), this);

        Inventory inventory = Bukkit.createInventory(null, 54, "§0§0§l퀘스트 아이템 카테고리 (" + page + ")");

        inventory.setItem(45, leftArrow);
        inventory.setItem(53, rightArrow);

        int slot = 0;
        for (int j = 45 * (page - 1); j < 45 * page; j++) {
            if (QuestItem.questItemCategories.size() <= j)
                break;

            String category = QuestItem.questItemCategories.get(slot);

            ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, 1);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName("§6" + category);

            List<String> descriptions = new ArrayList<>();
            descriptions.add("");
            descriptions.add("§7클릭 시 해당 카테고리의 아이템 목록을 확인합니다.");
            descriptions.add("");

            itemMeta.setLore(descriptions);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
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
    
}
