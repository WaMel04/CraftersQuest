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
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CollectItemCondition extends ProgressTriggerCondition {

    private static final Map<UUID, Long> lastCheckTimeMap = new ConcurrentHashMap<>();
    private static final long CHECK_COOLDOWN = 100L;

    // COLLECT_ITEM 'SLIME_BALL' '30'
    public CollectItemCondition(String symbol, TriggerConditionType type) {
        super("COLLECT_ITEM", TriggerConditionType.PROGRESSIVE);

        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(CraftersQuestPlugin.getInstance(), ListenerPriority.NORMAL,
                        PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        updateItemProgress(event.getPlayer());
                    }
                }
        );
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(CraftersQuestPlugin.getInstance(), ListenerPriority.LOWEST,
                        PacketType.Play.Client.SET_CREATIVE_SLOT) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        UUID uuid = event.getPlayer().getUniqueId();

                        long currentTime = System.currentTimeMillis();

                        lastCheckTimeMap.put(uuid, currentTime);
                    }
                }
        );
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.isCancelled())
            return;
        if (!(event.getEntity() instanceof Player))
            return;

        updateItemProgress((Player) event.getEntity());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.isCancelled())
            return;

        updateItemProgress(event.getPlayer());
    }


    private void updateItemProgress(Player player) {
        UUID uuid = player.getUniqueId();

        long currentTime = System.currentTimeMillis();
        long lastTime = lastCheckTimeMap.getOrDefault(uuid, 0L);

        if (currentTime - lastTime < CHECK_COOLDOWN)
            return;

        lastCheckTimeMap.put(uuid, currentTime);

        QuestDataContainer qdc = QuestDataContainer.questDataContainerMap.get(player.getUniqueId().toString());
        if (qdc == null)
            return;

        for (Quest quest : qdc.getProceedingQuests()) {
            for (QuestCondition questCondition : quest.getQuestConditionMap().values()) {
                if (!questCondition.getTriggerCondition().getSymbol().equalsIgnoreCase(getSymbol()))
                    continue;

                String condStr = questCondition.getTriggerConditionString();
                String cItemName = getFactorMap(condStr, "itemName", "amount").get("itemName").toLowerCase();

                Bukkit.getScheduler().runTask(CraftersQuestPlugin.getInstance(), () -> {
                    int currentAmount = 0;
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item != null && isItemMatched(item, cItemName)) {
                            currentAmount += item.getAmount();
                        }
                    }
                    ItemStack cursorItem = player.getItemOnCursor();

                    if (!cursorItem.getType().isAir()) {
                        if (isItemMatched(cursorItem, cItemName)) {
                            currentAmount += cursorItem.getAmount();
                        }
                    }

                    QuestData questData = qdc.getQuestDataMap().get(quest.getId());
                    QuestConditionData condData = questData.getQuestConditionDataMap().get(questCondition.getId());
                    int currentProgress = condData.getCurrentProgress();

                    if (currentAmount > currentProgress) {
                        qdc.progressQuestCondition(player, questCondition, currentAmount - currentProgress);
                    } else if (currentAmount < currentProgress) {
                        if (condData.isCompleted()) {
                            condData.reset();
                        }

                        condData.setCurrentProgress(currentAmount);

                        if (!questCondition.getProgressOperations().isEmpty()) {
                            questCondition.getProgressOperationStrings().forEach(ops -> {
                                Operation operation = Operation.parseOperation(ops);
                                if (operation != null)
                                    operation.execute(player, ops, questCondition);
                            });
                        }
                    }
                });
            }
        }
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

