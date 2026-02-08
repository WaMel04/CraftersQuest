package io.github.wamel04.crafters_quest.command;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.config.ConfigManager$CraftersNPC;
import io.github.wamel04.crafters_quest.config.ConfigManager$Item;
import io.github.wamel04.crafters_quest.config.ConfigManager$Quest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class CMD_QuestReload implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        CraftersQuestPlugin.loadConfig();

        sender.sendMessage("§6config를 리로드했습니다.");
        sender.sendMessage("§6퀘스트, 아이템, npc 파일을 불러오고 있습니다...");

        long startTime = System.currentTimeMillis();

        CompletableFuture.runAsync(() -> {
            ConfigManager$Quest.load().join();
            ConfigManager$Item.load().join();
            ConfigManager$CraftersNPC.load().join();

            sender.sendMessage("§6성공적으로 파일들을 불러왔습니다! §7(경과 시간: " + (System.currentTimeMillis() - startTime) + "ms)");
        });

        return false;
    }

}
