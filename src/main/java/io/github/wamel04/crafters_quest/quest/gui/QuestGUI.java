package io.github.wamel04.crafters_quest.quest.gui;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.QuestData;
import io.github.wamel04.crafters_quest.quest.QuestDataContainer;
import io.github.wamel04.crafters_quest.quest.QuestState;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestConditionData;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerConditionType;
import io.github.wamel04.crafters_quest.util.Util;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestGUI {

    public static Map<Player, QuestGUI> openGuiMap = new HashMap<>();

    private static ItemStack leftArrow = Util.getSkull("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9", "§c<- 이전 페이지", null);
    private static ItemStack rightArrow = Util.getSkull("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf", "§a다음 페이지 ->", null);

    private String owner;
    private int currentPage;

    public QuestGUI(String owner) {
        this.owner = owner;
    }

    public void open(Player player, Integer page) {
        if (page == null)
            page = 1;

        currentPage = page;
        openGuiMap.put(player, this);

        Inventory inventory = Bukkit.createInventory(null, 54, "§0§l퀘스트 목록 (" + page + ")");

        inventory.setItem(45, leftArrow);
        inventory.setItem(53, rightArrow);

        QuestDataContainer.get(owner)
                .thenAcceptAsync(questDataContainer -> {
                    List<Quest> proceedingQuests = new ArrayList<>();

                    for (Quest quest : Quest.questMap.values()) {
                        if (questDataContainer.getQuestState(quest).equals(QuestState.PROCEEDING))
                            proceedingQuests.add(quest);
                    }

                    int slot = 0;
                    for (int j = 45 * (currentPage - 1); j<45 * currentPage; j++) {
                        if (proceedingQuests.size() <= j)
                            break;

                        Quest quest = proceedingQuests.get(j);
                        QuestData questData = questDataContainer.getQuestDataMap().get(quest);

                        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, 1);
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.setDisplayName("§6" + quest.getName() + "(" + quest.getId() + ")");

                        List<String> descriptions = new ArrayList<>();
                        descriptions.add("");
                        descriptions.add(ChatColor.of("#7B68EE") + "카테고리: " + quest.getCategory());
                        descriptions.add("");

                        descriptions.add(ChatColor.of("#A52A2A") + "cancellable: " + quest.isCancellable());
                        descriptions.add("");

                        for (QuestConditionData questConditionData : questData.getQuestConditionDataMap().values()) {
                            QuestCondition questCondition = questConditionData.getQuestCondition();
                            String conditionName = questConditionData.getQuestCondition().getName();

                            descriptions.add(ChatColor.of("#808000") + "- " + conditionName + ":");
                            descriptions.add(ChatColor.of("#BDB76B") + "   trigger_condition: " + questCondition.getTriggerConditionString());

                            if (questCondition.getTriggerCondition().getType().equals(TriggerConditionType.PROGRESSIVE))
                                descriptions.add(ChatColor.of("#98FB98") + "   진척도: §f" +
                                        questConditionData.getCurrentProgress() + "/" + questConditionData.getMaxProgress() + " §7(" + questConditionData.getProgressPercent() + "%)");
                            if (questConditionData.isCompleted())
                                descriptions.add(ChatColor.of("#F0E68C") + "   상태: §a(완료)");
                            else
                                descriptions.add(ChatColor.of("#F0E68C") + "   상태: §c(진행중)");

                            descriptions.add("");
                        }

                        descriptions.add(ChatColor.of("#008B8B") + "complete-operations:");

                        for (String operation : quest.getQuestCompleteOperationStrings()) {
                            descriptions.add(ChatColor.of("#7FFFD4") + "   " + operation);
                        }

                        descriptions.add("");
                        descriptions.add("§c쉬프트 + 좌클릭 시 해당 퀘스트를 포기합니다.");

                        itemMeta.setLore(descriptions);
                        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        item.setItemMeta(itemMeta);

                        inventory.setItem(slot, item);
                        slot++;
                    }

                    Bukkit.getScheduler().runTask(CraftersQuestPlugin.getInstance(),
                            () -> player.openInventory(inventory));
                }).exceptionally(ex -> {
                            ex.printStackTrace();
                            return null;
                        }
                );

    }

    public String getOwner() {
        return owner;
    }

    public int getCurrentPage() {
        return currentPage;
    }

}
