package io.github.wamel04.crafters_quest.operation.list;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.operation.Operation;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class TitleOperation extends Operation {

    // $title 'title' 'subtitle' '3' '10' '10'
    public TitleOperation(String symbol) {
        super("$title");
    }

    @Override
    public void execute(Player player, String operationString, QuestCondition questCondition) {
        HashMap<String, String> factorMap = getFactorMap(operationString, "title", "subtitle", "duration", "fadein", "fadeout");

        CompletableFuture.runAsync(() -> {
            String title = getReplacedMessage(player, factorMap.get("title"), questCondition).join();
            String subtitle = getReplacedMessage(player, factorMap.get("subtitle"), questCondition).join();

            int duration = Integer.parseInt(factorMap.get("duration"));
            int fadein = Integer.parseInt(factorMap.get("fadein"));
            int fadeout = Integer.parseInt(factorMap.get("fadeout"));

            Bukkit.getScheduler().runTask(CraftersQuestPlugin.getInstance(), () -> {
                player.sendTitle(title, subtitle, duration * 20, fadein, fadeout);
            });
        }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );;
    }

}
