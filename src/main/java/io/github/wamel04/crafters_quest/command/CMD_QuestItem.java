package io.github.wamel04.crafters_quest.command;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.quest.gui.QuestItemMenuGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CMD_QuestItem implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return false;
        if (!sender.isOp()) {
            sender.sendMessage(CraftersQuestPlugin.PREFIX + "§c권한이 부족합니다.");
            return false;
        }

        Player player = (Player) sender;
        QuestItemMenuGUI menuGUI = new QuestItemMenuGUI(1);
        menuGUI.open(player);
        return false;
    }

}
