package io.github.wamel04.crafters_quest.quest;

import io.github.wamel04.crafters_quest.quest.quest_condition.QuestConditionData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class QuestData {

    private Quest quest;
    private QuestState questState;
    private String completedDate;

    // key : QuestCondition's id
    private Map<String, QuestConditionData> questConditionDataMap;

    public QuestData(Quest quest, QuestState questState, String completedDate, Map<String, QuestConditionData> questConditionDataMap) {
        this.quest = quest;
        this.questState = questState;
        this.completedDate = completedDate;
        this.questConditionDataMap = questConditionDataMap;
    }

    public Quest getQuest() {
        return quest;
    }

    public QuestState getQuestState() {
        return questState;
    }

    public String getCompletedDate() {
        return completedDate;
    }

    public void setQuestState(QuestState questState) {
        this.questState = questState;

        Map<String, QuestConditionData> newQcdMap = new HashMap<>();

        for (Map.Entry<String, QuestConditionData> entry : questConditionDataMap.entrySet()) {
            String conditionId = entry.getKey();
            QuestConditionData questConditionData = entry.getValue();

            if (questState.equals(QuestState.COMPLETED)) {
                questConditionData.complete();
                newQcdMap.put(conditionId, questConditionData);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a hh시 mm분 ss초");
                completedDate = LocalDateTime.now().format(formatter);
            } else {
                questConditionData.reset();
                newQcdMap.put(conditionId, questConditionData);
            }
        }

        this.questConditionDataMap = newQcdMap;
    }

    public Map<String, QuestConditionData> getQuestConditionDataMap() {
        return questConditionDataMap;
    }

}
