package io.github.wamel04.crafters_quest.operation.list;

import io.github.wamel04.crafters_quest.item.QuestItem;
import io.github.wamel04.crafters_quest.operation.Operation;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ChanceGiveOperation extends Operation {

    // $chance_give '50' 'crystal' '2' 'ruby' '1'
    public ChanceGiveOperation(String symbol) {
        super("$chance_give");
    }

    @Override
    public void execute(Player player, String operationString, QuestCondition questCondition) {
        HashMap<String, String> factorMap = getFactorMap(operationString, "chance", "item_1", "amount_1", "item_2", "amount_2");

        double chance = Double.parseDouble(factorMap.get("chance"));

        ItemStack item1 = getItem(factorMap.get("item_1"), Integer.parseInt(factorMap.get("amount_1")));
        ItemStack item2 = getItem(factorMap.get("item_2"), Integer.parseInt(factorMap.get("amount_2")));

        ItemStack giveItem;

        if (Math.random() <= chance / 100) {
            giveItem = item1;
        } else {
            giveItem = item2;
        }

        HashMap<Integer, ItemStack> map = player.getInventory().addItem(giveItem);

        for (ItemStack stack : map.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), stack);
        }
    }

    private static ItemStack getItem(String itemString, Integer amount) {
        if (itemString.equalsIgnoreCase("<none>")) {
            return new ItemStack(Material.AIR);
        } else {
            ItemStack item;

            if (QuestItem.questItemMap.containsKey(itemString)) {
                item = QuestItem.questItemMap.get(itemString).toItemStack();
                item.setAmount(amount);
            } else {
                if (Material.getMaterial(itemString) == null) {
                    return new ItemStack(Material.AIR);
                } else {
                    item = new ItemStack(Material.valueOf(itemString), amount);
                }
            }

            return item;
        }
    }

}
