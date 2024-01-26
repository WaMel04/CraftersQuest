package io.github.wamel04.crafters_quest.quest.trigger_condition;

import io.github.wamel04.crafters_quest.quest.QuestDataContainer;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;

import java.text.DecimalFormat;
import java.util.concurrent.CompletableFuture;

public class ProgressTriggerCondition extends TriggerCondition {

    public ProgressTriggerCondition(String symbol, TriggerConditionType type) {
        super(symbol, type);
    }

    public CompletableFuture<Integer> getCurrentProgress(String uuid, QuestCondition questCondition) {
        return QuestDataContainer.get(uuid)
                .thenApplyAsync(questDataContainer -> questDataContainer.getQuestDataMap().get(questCondition.getQuest())
                        .getQuestConditionDataMap().get(questCondition.getId())
                        .getCurrentProgress())
                .exceptionally(ex -> {
                            ex.printStackTrace();
                            return null;
                        }
                );
    }

    public int getMaxProgress(QuestCondition questCondition) {
        String conditionString = questCondition.getTriggerConditionString();
        String[] parts = conditionString.split("\\s+");

        if (parts.length == 1)
            return -1;

        String maxString = parts[parts.length - 1].replace("'", "");

        try {
            return Integer.parseInt(maxString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public CompletableFuture<Double> getProgressPercent(String uuid, QuestCondition questCondition) {
        return getCurrentProgress(uuid, questCondition)
                .thenApplyAsync(currentProgress -> {
                    int maxProgress = getMaxProgress(questCondition);
                    double percent = (currentProgress * 1D / maxProgress) * 100;

                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                    return Double.parseDouble(decimalFormat.format(percent));
                })
                .exceptionally(ex -> {
                            ex.printStackTrace();
                            return null;
                        }
                );
    }

}
