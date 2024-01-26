package io.github.wamel04.crafters_quest.config;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.item.QuestItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConfigManager$Item {

    private static final CraftersQuestPlugin plugin = CraftersQuestPlugin.getInstance();

    public static CompletableFuture<Void> load() {
        return CompletableFuture.runAsync(() -> {
            QuestItem.questItemMap.clear();

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
                }
            }
        }).exceptionally(
                ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );
    }

}
