package io.github.wamel04.crafters_quest.config;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.item.QuestItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ConfigManager$Item {

    private static final CraftersQuestPlugin plugin = CraftersQuestPlugin.getInstance();

    public static CompletableFuture<Void> load() {
        return CompletableFuture.runAsync(() -> {
            QuestItem.questItemMap.clear();
            QuestItem.questItemCategories.clear();

            File directory = new File(plugin.getDataFolder(), "items");

            for (File file : directory.listFiles()) {
                String category = file.getName().replace(".yml", ""); // 파일의 이름이 아이템의 카테고리

                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

                for (String itemId : yaml.getKeys(false)) {
                    ConfigurationSection itemSection = yaml.getConfigurationSection(itemId);

                    Material type = Material.getMaterial(itemSection.getString("type"));
                    String name = itemSection.getString("name");
                    List<String> lore = itemSection.getStringList("lore");
                    int customModelData = itemSection.getInt("custom-model-data");
                    List<String> enchantments = itemSection.getStringList("enchantments");

                    QuestItem.questItemMap.put(itemId, new QuestItem(itemId, category, type, name, lore, customModelData, enchantments));

                    if (!QuestItem.questItemCategories.contains(category))
                        QuestItem.questItemCategories.add(category);
                }
            }
        }).exceptionally(
                ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );
    }

    public static CompletableFuture<Void> register(String itemId, String category, ItemStack item) {
        Material type = item.getType();
        String name = (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : null;
        List<String> lore = (item.hasItemMeta() && item.getItemMeta().hasLore()) ? item.getItemMeta().getLore() : null;
        Integer customModelData = (item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) ? item.getItemMeta().getCustomModelData() : null;
        List<String> enchantments = new ArrayList<>();

        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                String enchantmentString = entry.getKey().getKey().getKey() + " " + entry.getValue();
                enchantments.add(enchantmentString);
            }
        }

        return CompletableFuture.runAsync(() -> {
            QuestItem questItem = new QuestItem(itemId, category, type, name, lore, customModelData, enchantments);
            QuestItem.questItemMap.put(itemId, questItem);

            if (!QuestItem.questItemCategories.contains(category))
                QuestItem.questItemCategories.add(category);

            save(questItem);
        }).exceptionally(ex -> {
           ex.printStackTrace();
           return null;
        });
    }

    public static void save(QuestItem questItem) {
        String itemId = questItem.getId();
        String category = questItem.getCategory();
        Material type = questItem.getType();
        String name = questItem.getName();
        List<String> lore = questItem.getLore();
        Integer customModelData = questItem.getCustomModelData();
        List<String> enchantments = questItem.getEnchantments();

        File file = new File(plugin.getDataFolder() + "/items", category + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection itemSection = yaml.contains("itemId")
                ? yaml.getConfigurationSection(itemId)
                : yaml.createSection(itemId);
        itemSection.set("type", type.toString());
        itemSection.set("name", name);
        itemSection.set("lore", lore);
        itemSection.set("custom-model-data", customModelData);
        itemSection.set("enchantments", enchantments);

        try {
            yaml.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
