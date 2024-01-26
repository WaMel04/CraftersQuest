package io.github.wamel04.crafters_quest.quest.quest_condition;

import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerConditionType;

import java.text.DecimalFormat;

public class QuestConditionData {

    private QuestCondition questCondition;
    private boolean completeState;
    private int currentProgress;
    private int maxProgress;

    public QuestConditionData(QuestCondition questCondition, boolean completeState, int currentProgress, int maxProgress) {
        this.questCondition = questCondition;
        this.completeState = completeState;
        this.currentProgress = currentProgress;
        this.maxProgress = maxProgress;
    }

    public QuestCondition getQuestCondition() {
        return questCondition;
    }

    public TriggerConditionType getConditionType() {
        return TriggerCondition.parseTriggerCondition(questCondition.triggerConditionString).getType();
    }

    public boolean isCompleted() {
        return completeState;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public double getProgressPercent() {
        if (currentProgress == -1)
            return -1;

        double percent = (currentProgress * 1D / maxProgress) * 100;

        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        return Double.parseDouble(decimalFormat.format(percent));
    }

    public void progress() {
        currentProgress++;
    }

    public void complete() {
        completeState = true;
        currentProgress = maxProgress;
    }

    public void reset() {
        completeState = false;

        if (getConditionType().equals(TriggerConditionType.PROGRESSIVE))
            currentProgress = 0;
        else
            currentProgress = -1;
    }


}
