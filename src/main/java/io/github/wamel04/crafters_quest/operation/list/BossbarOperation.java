package io.github.wamel04.crafters_quest.operation.list;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.operation.Operation;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class BossbarOperation extends Operation {

    // $bossbar 'RED' 'SOLID' '10' '&c%remaining_time%초 후 폭탄이 터집니다!'
    public BossbarOperation(String symbol) {
        super("$bossbar");
    }

    @Override
    public void execute(Player player, String operationString, QuestCondition questCondition) {
        HashMap<String, String> factorMap = getFactorMap(operationString, "color", "style", "duration", "title");

        int duration = Integer.parseInt(factorMap.get("duration"));

        getReplacedMessage(player, factorMap.get("title"), questCondition)
                .thenAcceptAsync(title -> {
                    BossBar bar = Bukkit.createBossBar(title.replace("%remaining_time%", String.valueOf(duration)), BarColor.valueOf(factorMap.get("color")), BarStyle.valueOf(factorMap.get("style")));
                    bar.addPlayer(player);

                    String finalTitle = title;
                    new BukkitRunnable() {
                        int second = duration;
                        @Override
                        public void run() {
                            if (second <= 0 || !player.isOnline()) {
                                bar.removeAll();
                                super.cancel();
                                return;
                            }

                            double progress = second*1D / duration*1D;
                            bar.setProgress(progress);
                            bar.setTitle(finalTitle.replace("%remaining_time%", String.valueOf(second)));

                            second = second - 1;
                        }
                    }.runTaskTimerAsynchronously(CraftersQuestPlugin.getInstance(), 0, 20);
                }).exceptionally(ex -> {
                            ex.printStackTrace();
                            return null;
                        }
                );;
    }

}
