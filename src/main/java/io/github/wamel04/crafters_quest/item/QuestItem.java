package io.github.wamel04.crafters_quest.item;

import io.github.wamel04.crafters_quest.util.Util;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuestItem {

    public static Map<String, QuestItem> questItemMap = new HashMap<>();

    private String id;
    private String category;

    private Material type;
    private String name;
    private List<String> lore;
    private Integer customModelData;
    private List<String> enchantments;

    private ItemStack item;

    public QuestItem(String id, String category, Material type, String name, List<String> lore, Integer customModelData, List<String> enchantments) {
        this.id = id;
        this.category = category;
        this.type = type;
        this.name = name;
        this.lore = lore;
        this.customModelData = customModelData;
        this.enchantments = enchantments;

        ItemStack item = new ItemStack(type, 1);

        ItemMeta meta = item.getItemMeta();

        if (name != null)
            meta.setDisplayName(Util.getColoredString(name));
        if (lore != null)
            meta.setLore(lore.stream()
                    .map(s -> Util.getColoredString(s))
                    .collect(Collectors.toList()));
        if (customModelData != null)
            meta.setCustomModelData(customModelData);

        for (String enchantmentString : enchantments) {
            String[] parts = enchantmentString.split("\\s+");
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(parts[0].toLowerCase()));

            if (enchantment == null)
                continue;

            int level = Integer.parseInt(parts[1]);
            meta.addEnchant(enchantment, level, true);
        }

        item.setItemMeta(meta);

        this.item = item;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public Material getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public List<String> getEnchantments() {
        return enchantments;
    }

    public ItemStack toItemStack() {
        return item;
    }

}
