package io.github.wamel04.crafters_quest.command;

import io.github.wamel04.crafters_quest.CraftersQuestAPI;
import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.QuestState;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CMD_SetQuestState implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§c권한이 부족합니다.");
            return false;
        }
        if (args.length < 3) {
            sender.sendMessage("§6/setqueststate [닉네임] [퀘스트] [상태] §7- 플레이어의 퀘스트 상태를 설정합니다.");
            return false;
        }

        String nick = args[0];
        String questName = args[1];
        String state = args[2];

        Player target = Bukkit.getPlayer(nick);

        if (!Quest.questMap.containsKey(questName)) {
            sender.sendMessage("§e" + questName + " §6퀘스트는 존재하지 않습니다.");
            return false;
        }

        QuestState questState;

        try {
            questState = QuestState.valueOf(state);
        } catch (Exception e) {
            sender.sendMessage("§e" + state + " §6는 올바른 퀘스트 상태가 아닙니다.");
            return false;
        }
        CompletableFuture.runAsync(() -> {
            String nickname = nick;
            String uuid;

            if (target == null) {
                OfflinePlayer newTarget = Bukkit.getOfflinePlayer(nickname);
                nickname = newTarget.getName();
                uuid = newTarget.getUniqueId().toString();
            } else {
                uuid = target.getUniqueId().toString();
            }

            CraftersQuestAPI.setQuestState(uuid, questName, questState);

            if (sender instanceof Player)
                sender.sendMessage("§e" + nickname + " §6님의 §e" + questName + " §6퀘스트 상태를 §e" + questState.name() + "§6(으)로 설정했습니다.");
        }).exceptionally(
                ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> result = new ArrayList<>();

            if (args[0].isEmpty()) {
                result.addAll(Bukkit.getOnlinePlayers().stream()
                        .map(HumanEntity::getName)
                        .collect(Collectors.toList()));
            } else {
                String searchInput = args[0].toLowerCase();

                result.addAll(Bukkit.getOnlinePlayers().stream()
                        .map(HumanEntity::getName)
                        .filter(name -> name.toLowerCase().startsWith(searchInput))
                        .collect(Collectors.toList()));
            }

            return result;
        } else if (args.length == 2){
            List<String> result = new ArrayList<>();

            if (args[1].isEmpty()) {
                result.addAll(Quest.questMap.values().stream()
                        .map(Quest::getId)
                        .collect(Collectors.toList()));
            } else {
                String searchInput = args[1].toLowerCase();

                result.addAll(Quest.questMap.values().stream()
                        .filter(quest -> quest.getId().startsWith(searchInput))
                        .map(Quest::getId)
                        .collect(Collectors.toList()));
            }

            return result;
        } else if (args.length == 3) {
            List<String> result = new ArrayList<>();

            if (args[2].isEmpty()) {
                result.addAll(Arrays.stream(QuestState.values())
                        .map(QuestState::name)
                        .collect(Collectors.toList()));
            } else {
                String searchInput = args[2].toLowerCase();

                result.addAll(Arrays.stream(QuestState.values())
                        .filter(questState -> questState.name().toLowerCase().startsWith(searchInput))
                        .map(QuestState::name)
                        .collect(Collectors.toList()));
            }

            return result;
        }

        return null;
    }

}
