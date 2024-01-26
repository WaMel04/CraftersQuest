package io.github.wamel04.crafters_quest.quest;

import io.github.wamel04.crafters_quest.operation.Operation;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Quest {

    public static Map<String, Quest> questMap = new HashMap<>();

    private String id;
    private String name;
    private String category;
    private boolean cancellable;
    private Map<String, QuestCondition> questConditionMap;
    private List<String> questCompleteOperationStrings;

    public Quest(String id, String name, String category, boolean cancellable, Map<String, QuestCondition> questConditionMap, List<String> questCompleteOperationStrings) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.cancellable = cancellable;
        this.questConditionMap = questConditionMap;
        this.questCompleteOperationStrings = questCompleteOperationStrings;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public boolean isCancellable() {
        return cancellable;
    }

    public Map<String, QuestCondition> getQuestConditionMap() {
        return questConditionMap;
    }

    public List<String> getQuestCompleteOperationStrings() {
        return questCompleteOperationStrings;
    }

    public List<Operation> getQuestCompleteOperations() {
        return questCompleteOperationStrings.stream()
                .map(Operation::parseOperation)
                .collect(Collectors.toList());
    }

}
