package io.github.wamel04.crafters_quest.operation.list;

import io.github.wamel04.crafters_quest.CraftersQuestAPI;
import io.github.wamel04.crafters_quest.operation.Operation;
import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.QuestState;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SetQuestStateOperation extends Operation {

    // $set_quest_state 'questId' 'NOT_REQUESTED'
    // $set_quest_state 'questId' 'PROCEEDING'
    // $set_quest_state 'questId' 'COMPLETED'
    public SetQuestStateOperation(String symbol) {
        super("$set_quest_state");
    }

    @Override
    public void execute(Player player, String operationString, QuestCondition questCondition) {
        HashMap<String, String> factorMap = getFactorMap(operationString, "questId", "questState");

        String questId = factorMap.get("questId");

        if (!Quest.questMap.containsKey(questId))
            return;

        QuestState questState = QuestState.valueOf(factorMap.get("questState"));

        CraftersQuestAPI.setQuestState(player.getUniqueId().toString(), questId, questState);
    }

}
