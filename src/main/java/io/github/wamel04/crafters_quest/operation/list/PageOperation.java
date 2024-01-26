package io.github.wamel04.crafters_quest.operation.list;

import io.github.wamel04.crafters_quest.npc.QuestNPC;
import io.github.wamel04.crafters_quest.operation.Operation;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import org.bukkit.entity.Player;

public class PageOperation extends Operation {

    // $page '페이지명'
    public PageOperation(String symbol) {
        super("$page");
    }

    @Override
    public void execute(Player player, String operationString, QuestCondition questCondition) {
        String page = getFactorMap(operationString, "page").get("page");

        getReplacedMessage(player, page, questCondition).thenAcceptAsync(p -> {
            QuestNPC questNPC = QuestNPC.playerTalkingNPCMap.get(player);

            if (questNPC != null && questNPC.isTalking(player))
                questNPC.setPlayerNextPage(player, p);
        }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );;
    }
}
