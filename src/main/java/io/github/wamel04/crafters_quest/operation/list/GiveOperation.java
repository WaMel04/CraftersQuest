package io.github.wamel04.crafters_quest.operation.list;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.item.QuestItem;
import io.github.wamel04.crafters_quest.operation.Operation;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GiveOperation extends Operation {

    // $give 'DIRT' '10'
    // $give 'ruby' '3'
    public GiveOperation(String symbol) {
        super("$give");
    }

    @Override
    public void execute(Player player, String operationString, QuestCondition questCondition) {
        Map<String, String> factorMap = getFactorMap(operationString, "item", "amount");

        String itemString = factorMap.get("item");
        int amount = Integer.parseInt(factorMap.get("amount"));

        ItemStack item;

        if (QuestItem.questItemMap.containsKey(itemString)) {
            item = QuestItem.questItemMap.get(itemString).toItemStack();
            item.setAmount(amount);
        } else {
            Material type = Material.getMaterial(itemString);

            if (type == null) {
                CraftersQuestPlugin.getInstance().getLogger().warning(itemString + " 아이템 타입은 존재하지 않습니다.");
                return;
            }

            item = new ItemStack(type, amount);
        }

        HashMap<Integer, ItemStack> map = player.getInventory().addItem(item);

        for (ItemStack stack : map.values()) {
            Bukkit.getScheduler().runTask(CraftersQuestPlugin.getInstance(), () -> player.getWorld().dropItemNaturally(player.getLocation(), stack));
        }
    }

}
