package io.github.wamel04.crafters_quest.quest.quest_condition;

import io.github.wamel04.crafters_quest.operation.Operation;
import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerCondition;

import java.util.List;
import java.util.stream.Collectors;

public class QuestCondition {

    String id;
    String name;
    String triggerConditionString;
    List<String> progressOperationStrings;
    List<String> completeOperationStrings;
    String questId;

    public QuestCondition(String id, String name, String triggerConditionString, List<String> progressOperationStrings, List<String> completeOperationStrings, String questId) {
        this.id = id;
        this.name = name;
        this.triggerConditionString = triggerConditionString;
        this.progressOperationStrings = progressOperationStrings;
        this.completeOperationStrings = completeOperationStrings;
        this.questId = questId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTriggerConditionString() {
        return triggerConditionString;
    }

    public TriggerCondition getTriggerCondition() {
        return TriggerCondition.parseTriggerCondition(triggerConditionString);
    }

    public List<String> getProgressOperationStrings() {
        return progressOperationStrings;
    }

    public List<Operation> getProgressOperations() {
        return progressOperationStrings.stream()
                .map(Operation::parseOperation)
                .collect(Collectors.toList());
    }

    public List<String> getCompleteOperationStrings() {
        return completeOperationStrings;
    }

    public List<Operation> getCompleteOperations() {
        return completeOperationStrings.stream()
                .map(Operation::parseOperation)
                .collect(Collectors.toList());
    }

    public String getQuestId() {
        return questId;
    }

    public Quest getQuest() {
        return Quest.questMap.get(questId);
    }

}
