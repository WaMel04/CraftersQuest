package io.github.wamel04.crafters_quest.quest.trigger_condition.list;

import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.QuestDataContainer;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerConditionType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandCondition extends TriggerCondition {

    // COMMAND 'me'
    public CommandCondition(String symbol, TriggerConditionType type) {
        super("COMMAND", TriggerConditionType.COMPLETED_ONCE);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        String command;

        if (event.getMessage().contains(" ")) {
            String[] parts = event.getMessage().split("\\s+");
            command = parts[0].substring(1); // '/' 문자 제거
        } else {
            command = event.getMessage().substring(1);
        }

        for (Quest quest : Quest.questMap.values()) {
            for (QuestCondition questCondition : quest.getQuestConditionMap().values()) {
                if (!questCondition.getTriggerCondition().getSymbol().equalsIgnoreCase(symbol))
                    continue;

                String cCommand = getFactorMap(questCondition.getTriggerConditionString(), "command").get("command");

                if (command.equalsIgnoreCase(cCommand)) {
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

