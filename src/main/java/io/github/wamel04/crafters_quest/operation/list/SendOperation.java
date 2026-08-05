package io.github.wamel04.crafters_quest.operation.list;

import de.themoep.minedown.MineDown;
import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.operation.Operation;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SendOperation extends Operation {

    // $send '메세지'
    public SendOperation(String symbol) {
        super("$send");
    }

    @Override
    public void execute(Player player, String operationString, QuestCondition questCondition) {
        String message = getFactorMap(operationString, "message").get("message");

        getReplacedMessage(player, message, questCondition).thenAcceptAsync(m -> {
            Bukkit.getScheduler().runTask(CraftersQuestPlugin.getInstance(), () -> {
                player.spigot().sendMessage(new MineDown(m).toComponent());
            });
        }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );;
    }
}
