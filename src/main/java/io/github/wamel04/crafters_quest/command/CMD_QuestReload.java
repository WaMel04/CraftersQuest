package io.github.wamel04.crafters_quest.command;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.config.ConfigManager$CraftersNPC;
import io.github.wamel04.crafters_quest.config.ConfigManager$Item;
import io.github.wamel04.crafters_quest.config.ConfigManager$Quest;
import io.github.wamel04.crafters_quest.config.ConfigManager$QuestDataContainer;
import io.github.wamel04.crafters_quest.quest.QuestDataContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class CMD_QuestReload implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(CraftersQuestPlugin.PREFIX + "§c권한이 부족합니다.");
            return false;
        }

        CraftersQuestPlugin.loadConfig();

        sender.sendMessage(CraftersQuestPlugin.PREFIX + "§fconfig를 리로드했습니다.");
        sender.sendMessage(CraftersQuestPlugin.PREFIX + "§f퀘스트, 아이템, npc 파일을 불러오고 있습니다...");

        long startTime = System.currentTimeMillis();

        CompletableFuture.runAsync(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ConfigManager$QuestDataContainer.save(player.getUniqueId().toString()).join();
            }

            ConfigManager$Quest.load().join();
            ConfigManager$Item.load().join();
            ConfigManager$CraftersNPC.load().join();

            for (Player player : Bukkit.getOnlinePlayers()) {
                ConfigManager$QuestDataContainer.load(player.getUniqueId().toString())
                        .thenAcceptAsync(questDataContainer ->
                                QuestDataContainer.questDataContainerMap.put(player.getUniqueId().toString(), questDataContainer))
                        .join();
            }

            sender.sendMessage(CraftersQuestPlugin.PREFIX + "§f성공적으로 파일들을 불러왔습니다! §7(경과 시간: " + (System.currentTimeMillis() - startTime) + "ms)");
        });

        return false;
    }

}
