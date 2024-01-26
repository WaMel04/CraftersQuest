package io.github.wamel04.crafters_quest.quest.trigger_condition.list;

import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.QuestDataContainer;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.ProgressTriggerCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerConditionType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakBlockCondition extends ProgressTriggerCondition {

    // BREAK_BLOCK 'GRASS_BLOCK' '10'
    public BreakBlockCondition(String symbol, TriggerConditionType type) {
        super("BREAK_BLOCK", TriggerConditionType.PROGRESSIVE);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        Material type = event.getBlock().getType();

        for (Quest quest : Quest.questMap.values()) {
            for (QuestCondition questCondition : quest.getQuestConditionMap().values()) {
                if (!questCondition.getTriggerCondition().getSymbol().equalsIgnoreCase(symbol))
                    continue;

                Material cType = Material.getMaterial(getFactorMap(questCondition.getTriggerConditionString(), "type", "amount").get("type"));

                if (type.equals(cType)) {
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
