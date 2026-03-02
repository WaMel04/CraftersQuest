package io.github.wamel04.crafters_quest.command;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.config.ConfigManager$Item;
import io.github.wamel04.crafters_quest.item.QuestItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CMD_RegisterQuestItem implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return false;
        if (!sender.isOp()) {
            sender.sendMessage(CraftersQuestPlugin.PREFIX + "§c권한이 부족합니다.");
            return false;
        }
        if (args.length != 2) {
            sender.sendMessage(CraftersQuestPlugin.PREFIX + "§f/registerquestitem [카테고리] [아이템 ID] §7- 들고 있는 아이템을 퀘스트 아이템 목록에 등록합니다.");
            return false;
        } else {
            Player player = (Player) sender;

            if (QuestItem.questItemMap.containsKey(args[1].toLowerCase())) {
                player.sendMessage("§e" + args[1].toLowerCase() + " §6은 이미 존재하는 아이템입니다.");
                return false;
            }
            if (player.getInventory().getItemInMainHand().getType().isAir()) {
                sender.sendMessage(CraftersQuestPlugin.PREFIX + "§c아이템을 손에 들어주세요");
                return false;
            }

            ConfigManager$Item.register(args[1].toLowerCase(), args[0].toLowerCase(), player.getInventory().getItemInMainHand())
                    .thenAcceptAsync(v -> player.sendMessage("§e" + args[1].toLowerCase() + " (카테고리: " + args[0].toLowerCase() + ")§6을 등록했습니다."));
        }

        return false;
    }

}
