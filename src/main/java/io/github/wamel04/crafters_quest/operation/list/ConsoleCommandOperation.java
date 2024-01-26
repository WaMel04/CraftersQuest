package io.github.wamel04.crafters_quest.operation.list;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.operation.Operation;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ConsoleCommandOperation extends Operation {

    // $console_command 'spawn'
    public ConsoleCommandOperation(String symbol) {
        super("$console_command");
    }

    @Override
    public void execute(Player player, String operationString, QuestCondition questCondition) {
        HashMap<String, String> factorMap = getFactorMap(operationString, "command");

        getReplacedMessage(player, factorMap.get("command"), questCondition)
                .thenAcceptAsync(command -> Bukkit.getScheduler().runTask(CraftersQuestPlugin.getInstance(), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                })).exceptionally(ex -> {
                            ex.printStackTrace();
                            return null;
                        }
                );;
    }

}
