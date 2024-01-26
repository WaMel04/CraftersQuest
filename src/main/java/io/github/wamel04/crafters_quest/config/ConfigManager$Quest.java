package io.github.wamel04.crafters_quest.config;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import io.github.wamel04.crafters_quest.util.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ConfigManager$Quest {

    private static final CraftersQuestPlugin plugin = CraftersQuestPlugin.getInstance();

    public static CompletableFuture<Void> load() {
        return CompletableFuture.runAsync(() -> {
            Quest.questMap.clear();

            File directory = new File(plugin.getDataFolder(), "quests");

            for (File child : directory.listFiles()) {
                if (child.isDirectory()) { // quests 안에 폴더가 있으면 카테고리를 지정할 수 있음.
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

    public static CompletableFuture<Void> read(File file, String category) {
        return CompletableFuture.runAsync(() -> {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

            String questId = file.getName().replace(".yml", "");
            String name = Util.getColoredString(yaml.getString("name"));

            boolean cancellable = yaml.getBoolean("cancellable");

            Map<String, QuestCondition> questConditionMap = new HashMap<>();
            ConfigurationSection conditionListSection = yaml.getConfigurationSection("conditions");

            for (String conditionId : conditionListSection.getKeys(false)) {
                ConfigurationSection conditionSection = conditionListSection.getConfigurationSection(conditionId);
                String conditionName = Util.getColoredString(conditionSection.getString("name"));
                String triggerCondition = conditionSection.getString("trigger-condition");

                List<String> progressOperationStrings = conditionSection.getStringList("progress-operations");
                List<String> completeOperationStrings = conditionSection.getStringList("complete-operations");

                QuestCondition questCondition = new QuestCondition(conditionId, conditionName, triggerCondition, progressOperationStrings, completeOperationStrings, questId);
                questConditionMap.put(conditionId, questCondition);
            }

            List<String> questCompleteOperationStrings = yaml.getStringList("quest-complete-operations");

            Quest.questMap.put(questId, new Quest(questId, name, category, cancellable, questConditionMap, questCompleteOperationStrings));
        }).exceptionally(
                ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );
    }



}
