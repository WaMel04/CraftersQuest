package io.github.wamel04.crafters_quest.config;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.npc.QuestNPC;
import io.github.wamel04.crafters_quest.npc.page_condition.PageCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ConfigManager$CraftersNPC {

    private static final CraftersQuestPlugin plugin = CraftersQuestPlugin.getInstance();

    public static CompletableFuture<Void> load() {
        return CompletableFuture.runAsync(() -> {
            QuestNPC.questNPCMap.clear();

            File directory = new File(plugin.getDataFolder(), "npcs");

            for (File child : directory.listFiles()) {
                if (child.isDirectory()) { // npcs 안에 폴더가 있으면 카테고리를 지정할 수 있음.
                    for (File grandChild : child.listFiles()) {
                        read(grandChild, child.getName()).join(); // 파일을 다 읽을 때까지 대기
                    }
                } else {
                    read(child, "no_category").join();
                }
            }
        }).exceptionally(
                ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );
    }

    private static CompletableFuture<Void> read(File file, String category) {
        return CompletableFuture.runAsync(() -> {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

            String id = file.getName().replace(".yml", "");
            String name = yaml.getString("name");
            String permission = yaml.getString("permission");

            List<PageCondition> firstPageConditions = new ArrayList<>();
            ConfigurationSection firstPageConditionListSection = yaml.getConfigurationSection("first-page-conditions");

            for (String conditionString : firstPageConditionListSection.getKeys(false)) {
                String page = firstPageConditionListSection.getString(conditionString);

                firstPageConditions.add(new PageCondition(conditionString, page));
            }
            Map<String, List<String>> pageOperationStringMap = new HashMap<>();
            ConfigurationSection pageListSection = yaml.getConfigurationSection("pages");

            for (String page : pageListSection.getKeys(false)) {
                List<String> pageOperationStrings = pageListSection.getStringList(page);
                pageOperationStringMap.put(page, pageOperationStrings);
            }

            QuestNPC.questNPCMap.put(id, new QuestNPC(id, name, category, permission, firstPageConditions, pageOperationStringMap));
        }).exceptionally(
                ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );
    }



}
