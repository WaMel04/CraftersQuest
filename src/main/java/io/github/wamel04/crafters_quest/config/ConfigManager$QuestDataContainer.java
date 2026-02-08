package io.github.wamel04.crafters_quest.config;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.QuestData;
import io.github.wamel04.crafters_quest.quest.QuestDataContainer;
import io.github.wamel04.crafters_quest.quest.QuestState;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestConditionData;
import io.github.wamel04.crafters_quest.quest.trigger_condition.ProgressTriggerCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerConditionType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ConfigManager$QuestDataContainer {

    private static final CraftersQuestPlugin plugin = CraftersQuestPlugin.getInstance();

    public static CompletableFuture<QuestDataContainer> load(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(plugin.getDataFolder() + "/quest_data", uuid + ".yml");

            Map<String, QuestData> questDataMap = new HashMap<>();

            if (!file.exists()) {
                for (Quest quest : Quest.questMap.values()) {
                    Map<String, QuestConditionData> questConditionDataMap = new HashMap<>();

                    for (String questConditionId : quest.getQuestConditionMap().keySet()) {
                        QuestCondition questCondition = quest.getQuestConditionMap().get(questConditionId);
                        TriggerCondition triggerCondition = questCondition.getTriggerCondition();

                        if (triggerCondition == null)
                            continue;

                        QuestConditionData questConditionData;

                        if (triggerCondition.getType().equals(TriggerConditionType.PROGRESSIVE))
                            questConditionData = new QuestConditionData(questCondition, false, 0, ((ProgressTriggerCondition) triggerCondition).getMaxProgress(questCondition));
                        else
                            questConditionData = new QuestConditionData(questCondition, false, -1, -1);

                        questConditionDataMap.put(questConditionId, questConditionData);
                    }

                    QuestData questData = new QuestData(quest, QuestState.NOT_REQUESTED, null, questConditionDataMap);
                    questDataMap.put(quest.getId(), questData);
                }
            } else {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection questListSection = yaml.getConfigurationSection("quests");

                for (Quest quest : Quest.questMap.values()) {
                    ConfigurationSection questSection = questListSection.getConfigurationSection(quest.getId());

                    if (questSection == null) {
                        Map<String, QuestConditionData> questConditionDataMap = new HashMap<>();

                        for (QuestCondition questCondition : quest.getQuestConditionMap().values()) {
                            if (questCondition.getTriggerCondition().getType().equals(TriggerConditionType.PROGRESSIVE))
                                questConditionDataMap.put(questCondition.getId(), new QuestConditionData(questCondition, false, 0,
                                        ((ProgressTriggerCondition) questCondition.getTriggerCondition()).getMaxProgress(questCondition)));
                            else
                                questConditionDataMap.put(questCondition.getId(), new QuestConditionData(questCondition, false, -1, -1));
                        }

                        QuestData questData = new QuestData(quest, QuestState.NOT_REQUESTED, null, questConditionDataMap);
                        questDataMap.put(quest.getId(), questData);

                        continue;
                    }

                    QuestState questState = QuestState.valueOf(questSection.getString("quest-state"));
                    String completedDate = questSection.getString("completed-date");

                    ConfigurationSection questConditionListSection = questSection.getConfigurationSection("conditions");

                    Map<String, QuestConditionData> questConditionDataMap = new HashMap<>();

                    for (String questConditionId : quest.getQuestConditionMap().keySet()) {
                        QuestCondition questCondition = quest.getQuestConditionMap().get(questConditionId);
                        ConfigurationSection questConditionSection = questConditionListSection.getConfigurationSection(questConditionId);

                        if (questConditionSection == null) {
                            if (questCondition.getTriggerCondition().getType().equals(TriggerConditionType.PROGRESSIVE))
                                questConditionDataMap.put(questConditionId, new QuestConditionData(questCondition, false, 0,
                                        ((ProgressTriggerCondition) questCondition.getTriggerCondition()).getMaxProgress(questCondition)));
                            else
                                questConditionDataMap.put(questConditionId, new QuestConditionData(questCondition, false, -1, -1));
                        } else {
                            boolean completeState = questConditionSection.getBoolean("complete-state");
                            int currentProgress = questConditionSection.getInt("current-progress", -1);

                            if (currentProgress != -1) {
                                TriggerCondition triggerCondition = questCondition.getTriggerCondition();

                                if (triggerCondition == null)
                                    continue;

                                if (triggerCondition.getType().equals(TriggerConditionType.PROGRESSIVE)) {
                                    ProgressTriggerCondition progressTriggerCondition = (ProgressTriggerCondition) triggerCondition;
                                    questConditionDataMap.put(questConditionId, new QuestConditionData(questCondition, completeState, currentProgress, progressTriggerCondition.getMaxProgress(questCondition)));
                                }
                            } else {
                                questConditionDataMap.put(questConditionId, new QuestConditionData(questCondition, completeState, -1, -1));
                            }
                        }
                    }

                    QuestData questData = new QuestData(quest, questState, completedDate, questConditionDataMap);
                    questDataMap.put(quest.getId(), questData);
                }
            }

            return new QuestDataContainer(uuid, questDataMap);
        }).exceptionally(
                ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );
    }

    public static CompletableFuture<Void> save(String uuid) {
        return CompletableFuture.runAsync(() -> {
            if (!QuestDataContainer.questDataContainerMap.containsKey(uuid))
                return;

            File file = new File(plugin.getDataFolder() + "/quest_data", uuid + ".yml");

            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection questListSection = yaml.createSection("quests");

            QuestDataContainer questDataContainer = QuestDataContainer.questDataContainerMap.get(uuid);

            for (String questId : questDataContainer.getQuestDataMap().keySet()) {
                QuestData questData = questDataContainer.getQuestDataMap().get(questId);

                ConfigurationSection questSection = questListSection.createSection(questId);
                questSection.set("quest-state", questData.getQuestState().name());

                if (questData.getCompletedDate() != null)
                    questSection.set("completed-date", questData.getCompletedDate());

                ConfigurationSection questConditionListSection = questSection.createSection("conditions");

                for (String questConditionId : questData.getQuestConditionDataMap().keySet()) {
                    ConfigurationSection questConditionSection = questConditionListSection.createSection(questConditionId);

                    QuestConditionData questConditionData = questData.getQuestConditionDataMap().get(questConditionId);
                    QuestCondition questCondition = questConditionData.getQuestCondition();

                    questConditionSection.set("complete-state", questConditionData.isCompleted());

                    if (questCondition.getTriggerCondition().getType().equals(TriggerConditionType.PROGRESSIVE)) {
                        questConditionSection.set("current-progress", questConditionData.getCurrentProgress());
                    }
                }
            }

            try {
                yaml.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(
                ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );
    }

}
