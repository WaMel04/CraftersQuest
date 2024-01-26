package io.github.wamel04.crafters_quest.operation.list;

import io.github.wamel04.crafters_quest.CraftersQuestAPI;
import io.github.wamel04.crafters_quest.operation.Operation;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import org.bukkit.entity.Player;

public class ExitOperation extends Operation {

    // $exit
    public ExitOperation(String symbol) {
        super("$exit");
    }

    @Override
    public void execute(Player player, String operationString, QuestCondition questCondition) {
        CraftersQuestAPI.unTalk(player);
    }
}
