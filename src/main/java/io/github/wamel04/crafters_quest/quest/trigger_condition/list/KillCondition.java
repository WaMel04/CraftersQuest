package io.github.wamel04.crafters_quest.quest.trigger_condition.list;

import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.QuestDataContainer;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.ProgressTriggerCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerConditionType;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillCondition extends ProgressTriggerCondition {

    // KILL 'PIG' '10'
    // KILL '사그라지는 갑옷병' '10'
    public KillCondition(String symbol, TriggerConditionType type) {
        super("KILL", TriggerConditionType.PROGRESSIVE);
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.isCancelled())
            return;
        if (event.getEntity().getKiller() == null)
            return;

        Player player = event.getEntity().getKiller();
        EntityType type = event.getEntityType();
        String entityName = ChatColor.stripColor(event.getEntity().getName());

        for (Quest quest : Quest.questMap.values()) {
            for (QuestCondition questCondition : quest.getQuestConditionMap().values()) {
                if (!questCondition.getTriggerCondition().getSymbol().equalsIgnoreCase(symbol))
                    continue;

                String cEntityName = getFactorMap(questCondition.getTriggerConditionString(), "entityName", "amount").get("entityName");

                if (cEntityName.equalsIgnoreCase(type.name()) || cEntityName.equals(entityName)) {
                    QuestDataContainer.get(player.getUniqueId().toString())
                            .thenAcceptAsync(questDataContainer -> questDataContainer.progressQuestCondition(player, questCondition))
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

