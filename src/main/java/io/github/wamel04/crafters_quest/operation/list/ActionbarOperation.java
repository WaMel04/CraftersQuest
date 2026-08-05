package io.github.wamel04.crafters_quest.operation.list;

import de.themoep.minedown.MineDown;
import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.operation.Operation;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionbarOperation extends Operation {

    // $actionbar "액션바"
    public ActionbarOperation(String symbol) {
        super("$actionbar");
    }

    @Override
    public void execute(Player player, String operationString, QuestCondition questCondition) {
        String message = getFactorMap(operationString, "message").get("message");

        getReplacedMessage(player, message, questCondition).thenAcceptAsync(m -> {
            Bukkit.getScheduler().runTask(CraftersQuestPlugin.getInstance(), () -> {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new MineDown(m).toComponent());
            });
        }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );;
    }
}
