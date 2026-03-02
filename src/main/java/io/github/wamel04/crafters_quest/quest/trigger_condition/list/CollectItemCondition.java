package io.github.wamel04.crafters_quest.quest.trigger_condition.list;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.item.QuestItem;
import io.github.wamel04.crafters_quest.operation.Operation;
import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.QuestData;
import io.github.wamel04.crafters_quest.quest.QuestDataContainer;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestConditionData;
import io.github.wamel04.crafters_quest.quest.trigger_condition.ProgressTriggerCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerConditionType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CollectItemCondition extends ProgressTriggerCondition {

    // COLLECT_ITEM 'SLIME_BALL' '30'
    public CollectItemCondition(String symbol, TriggerConditionType type) {
        super("COLLECT_ITEM", TriggerConditionType.PROGRESSIVE);

        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(CraftersQuestPlugin.getInstance(), ListenerPriority.NORMAL,
                        PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        Player player = event.getPlayer();

                        Bukkit.getScheduler().runTask(CraftersQuestPlugin.getInstance(), () -> {
                            updateItemProgress(player);
                        });
                    }
                }
        );
    }

//    @EventHandler
//    public void onPickup(EntityPickupItemEvent event) {
//        if (event.isCancelled())
//            return;
//        if (!(event.getEntity() instanceof Player))
//            return;
//
//        updateItemProgress((Player) event.getEntity());
//    }
//
//    @EventHandler
//    public void onDrop(PlayerDropItemEvent event) {
//        if (event.isCancelled())
//            return;
//
//        updateItemProgress(event.getPlayer());
//    }
//
//    @EventHandler
//    public void onInventoryClick(InventoryClickEvent event) {
//        if (event.isCancelled())
//            return;
//        if (!(event.getWhoClicked() instanceof Player))
//            return;
//
//        updateItemProgress((Player) event.getWhoClicked());
//    }

    private void updateItemProgress(Player player) {
        Bukkit.getScheduler().runTaskLater(CraftersQuestPlugin.getInstance(), () -> {
            if (player.isOnline()) {
                QuestDataContainer.get(player.getUniqueId().toString())
                        .thenAcceptAsync(qdc -> {
                            for (Quest quest : qdc.getProceedingQuests()) {
                                for (QuestCondition questCondition : quest.getQuestConditionMap().values()) {
                                    if (!questCondition.getTriggerCondition().getSymbol().equalsIgnoreCase(getSymbol()))
                                        continue;

                                    String condStr = questCondition.getTriggerConditionString();
                                    String cItemName = getFactorMap(condStr, "itemName", "amount").get("itemName").toLowerCase();

                                    int currentAmount = 0;
                                    for (ItemStack item : player.getInventory().getContents()) {
                                        if (item == null || item.getType().isAir())
                                            continue;

                                        if (isItemMatched(item, cItemName))
                                            currentAmount += item.getAmount();
                                    }

                                    QuestData questData = qdc.getQuestDataMap().get(quest.getId());
                                    QuestConditionData condData = questData.getQuestConditionDataMap().get(questCondition.getId());
                                    int currentProgress = condData.getCurrentProgress();

                                    if (currentAmount > currentProgress) {
                                        qdc.progressQuestCondition(player, questCondition, currentAmount - currentProgress);
                                    }
                                    if (currentAmount < currentProgress) {
                                        if (condData.isCompleted()) {
                                            condData.reset();
                                        }

                                        condData.setCurrentProgress(currentAmount);

                                        if (!questCondition.getProgressOperations().isEmpty()) {
                                            questCondition.getProgressOperationStrings().stream().forEach(ops -> {
                                                Operation operation = Operation.parseOperation(ops);

                                                if (operation != null)
                                                    operation.execute(player, ops, questCondition);
                                            });
                                        }
                                    }
                                }
                            }
                        });
            }
        }, 1);
    }

    private boolean isItemMatched(ItemStack currentItem, String cItemName) {
        if (QuestItem.questItemMap.containsKey(cItemName)) {
            QuestItem questItem = QuestItem.questItemMap.get(cItemName);

            return questItem != null && questItem.toItemStack().isSimilar(currentItem);
        }
        if (cItemName.equalsIgnoreCase(currentItem.getType().name())) {
            return true;
        }
        if (currentItem.hasItemMeta() && currentItem.getItemMeta().hasDisplayName()) {
            return cItemName.equals(ChatColor.stripColor(currentItem.getItemMeta().getDisplayName()));
        }

        return false;
    }

}

