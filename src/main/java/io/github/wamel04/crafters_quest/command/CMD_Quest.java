package io.github.wamel04.crafters_quest.command;

import io.github.wamel04.crafters_quest.quest.gui.QuestGUI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class CMD_Quest implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return false;
        if (args.length == 0) {
            Player player = (Player) sender;

            QuestGUI questGUI = new QuestGUI(player.getUniqueId().toString());
            questGUI.open(player, 1);
        } else {
            if (!sender.isOp()) {
                sender.sendMessage("§c권한이 부족합니다.");
                return false;
            }

            Player player = (Player) sender;

            String nick = args[0];
            Player target = Bukkit.getPlayer(nick);

            CompletableFuture.runAsync(() -> {
                String nickname = nick;
                String uuid;

                if (target == null) {
                    OfflinePlayer newTarget = Bukkit.getOfflinePlayer(nickname);
                    uuid = newTarget.getUniqueId().toString();
                } else {
                    uuid = target.getUniqueId().toString();
                }

                QuestGUI questGUI = new QuestGUI(uuid);
                questGUI.open(player, 1);
            }).exceptionally(
                    ex -> {
                        ex.printStackTrace();
                        return null;
                    }
            );

        }
        return false;
    }

}
