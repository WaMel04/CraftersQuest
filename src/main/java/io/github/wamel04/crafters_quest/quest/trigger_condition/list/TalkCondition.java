package io.github.wamel04.crafters_quest.quest.trigger_condition.list;

import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.QuestDataContainer;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerConditionType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class TalkCondition extends TriggerCondition {

    // TALK '예시_NPC'
    public TalkCondition(String symbol, TriggerConditionType type) {
        super("TALK", TriggerConditionType.COMPLETED_ONCE);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTalk(PlayerInteractAtEntityEvent event) {
        if (!event.getHand().equals(EquipmentSlot.HAND))
            return;

        Player player = event.getPlayer();

        Entity target = event.getRightClicked();

        if (!target.hasMetadata("NPC")) // CitizenNPC가 아닐 경우
            return;

        String npcName = ChatColor.stripColor(target.getName());

        for (Quest quest : Quest.questMap.values()) {
            for (QuestCondition questCondition : quest.getQuestConditionMap().values()) {
                if (!questCondition.getTriggerCondition().getSymbol().equalsIgnoreCase(symbol))
                    continue;

                String cNpcName = getFactorMap(questCondition.getTriggerConditionString(), "name").get("name");

                if (npcName.equals(cNpcName)) {
                    QuestDataContainer.get(player.getUniqueId().toString())
                            .thenAcceptAsync(questDataContainer -> questDataContainer.completeQuestCondition(player, questCondition))
                            .exceptionally(ex -> {
                                        ex.printStackTrace();
                                        return null;
                                    }
                            );
                }
            }
        }
    }


}

