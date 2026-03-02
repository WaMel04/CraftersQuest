package io.github.wamel04.crafters_quest.quest.trigger_condition.list;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.item.QuestItem;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerConditionType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EquipCondition extends TriggerCondition {

    // EQUIP 'PRISM_CHESTPLATE' (QuestItem으로 구분)
    // EQUIP 'DIAMOND_CHESTPLATE' (타입으로 구분)
    // EQUIP '프리즈마린 갑옷' (이름으로 구분)
    public EquipCondition(String symbol, TriggerConditionType type) {
        super("EQUIP", TriggerConditionType.COMPLETED_ONCE);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Player player = event.getPlayer();

            if (event.getItem() == null)
                return;

            ItemStack item = event.getItem().clone();

            check(player, item);
        } else if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand().clone();

            if (!player.getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
                if (item.getType().equals(Material.AIR)) {
                    check(player, player.getInventory().getItemInOffHand().clone());
                } else if (item.getType().name().contains("HELMET") || item.getType().name().contains("CHESTPLATE")
                        || item.getType().name().contains("LEGGINGS") || item.getType().name().contains("BOOTS") || item.getType().name().contains("ELYTRA")) {
                    check(player, item);
                }
            } else {
                check(player, item);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !event.getClickedInventory().getType().equals(InventoryType.PLAYER))
            return;
        if (event.getClick().equals(ClickType.SHIFT_LEFT) || event.getClick().equals(ClickType.SHIFT_RIGHT)) {
            Player player = (Player) event.getWhoClicked();

            if (event.getCurrentItem() != null) {
                ItemStack item = event.getCurrentItem().clone();

                check(player, item);
            }
        } else if (event.getClick().equals(ClickType.NUMBER_KEY)) {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());

            if (item != null)
                check(player, item);
        } else if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
            if (event.getCurrentItem() != null && event.getCurrentItem().equals(event.getCursor())) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCursor().clone();

            check(player, item);
        }
    }

    private void check(Player player, ItemStack currentItem) {
        ItemStack compareItem;
        String type = currentItem.getType().name();
        String slot;

        if (type.contains("HELMET")) {
            slot = "HELMET";
            compareItem = player.getInventory().getHelmet();
        }
        else if (type.contains("CHESTPLATE")) {
            slot = "CHESTPLATE";
            compareItem = player.getInventory().getChestplate();
        }
        else if (type.contains("LEGGINGS")) {
            slot = "LEGGINGS";
            compareItem = player.getInventory().getLeggings();
        }
        else if (type.contains("BOOTS")) {
            slot = "BOOTS";
            compareItem = player.getInventory().getBoots();
        }
        else if (type.contains("ELYTRA")) {
            slot = "CHESTPLATE";
            compareItem = player.getInventory().getChestplate();
        } else {
            return;
        }
        if (currentItem.equals(compareItem))
            return;

        Bukkit.getScheduler().runTaskLater(CraftersQuestPlugin.getInstance(), () -> {
            if (player.isOnline()) {
                ItemStack changeItem;

                if (slot.equals("HELMET")) {
                    changeItem = player.getInventory().getHelmet();
                } else if (slot.equals("CHESTPLATE")) {
                    changeItem = player.getInventory().getChestplate();
                } else if (slot.equals("LEGGINGS")) {
                    changeItem = player.getInventory().getLeggings();
                } else {
                    changeItem = player.getInventory().getBoots();
                }
                if (currentItem.equals(changeItem)) {
                    match(player, this, condStr -> {
                        String cItemName = getFactorMap(condStr, "itemName").get("itemName").toLowerCase();

                        if (QuestItem.questItemMap.containsKey(cItemName)) {
                            QuestItem questItem = QuestItem.questItemMap.get(cItemName);

                            return questItem != null && questItem.toItemStack().isSimilar(currentItem);
                        }
                        if (cItemName.equalsIgnoreCase(currentItem.getType().name())) {
                            return true;
                        }
                        if (currentItem.hasItemMeta() && currentItem.getItemMeta().hasDisplayName()) {
                            return cItemName.equals(ChatColor.stripColor(currentItem.getItemMeta().getDisplayName()));
                        }

                        return false;
                    });
                }
            }
        }, 1);
    }

}

