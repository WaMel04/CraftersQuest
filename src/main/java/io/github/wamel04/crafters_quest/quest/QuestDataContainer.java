package io.github.wamel04.crafters_quest.quest;

import io.github.wamel04.crafters_quest.config.ConfigManager$QuestDataContainer;
import io.github.wamel04.crafters_quest.operation.Operation;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestConditionData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class QuestDataContainer {

    public static Map<String, QuestDataContainer> questDataContainerMap = new HashMap<>();

    public static CompletableFuture<QuestDataContainer> get(String uuid) {
        if (questDataContainerMap.containsKey(uuid))
            return CompletableFuture.completedFuture(questDataContainerMap.get(uuid));

        return ConfigManager$QuestDataContainer.load(uuid).thenApplyAsync(questDataContainer -> {
                questDataContainerMap.put(uuid, questDataContainer);
            return questDataContainer;
        }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );
    }

    private String owner;
    private Map<String, QuestData> questDataMap;

    public QuestDataContainer(String owner, Map<String, QuestData> questDataMap) {
        this.owner = owner;
        this.questDataMap = questDataMap;
    }

    public String getOwner() {
        return owner;
    }

    public Map<String, QuestData> getQuestDataMap() {
        return questDataMap;
    }

    /**
     * QuestState를 반환합니다.
     */
    public QuestState getQuestState(Quest quest) {
        QuestData questData = questDataMap.get(quest.getId());

        return questData.getQuestState();
    }

    /**
     * QuestState를 설정합니다.
     */
    public void setQuestState(Quest quest, QuestState questState) {
        QuestData questData = questDataMap.get(quest.getId());
        questData.setQuestState(questState);

        questDataMap.put(quest.getId(), questData);
    }

    /**
     * QuestCondition의 현재 진척도를 반환합니다.
     */
    public int getQuestConditionCurrentProgress(QuestCondition questCondition) {
        QuestConditionData questConditionData = questDataMap.get(questCondition.getQuest().getId()).getQuestConditionDataMap().get(questCondition.getId());
        return questConditionData.getCurrentProgress();
    }

    /**
     * QuestCondition의 최대 진척도를 반환합니다.
     */
    public int getQuestConditionMaxProgress(QuestCondition questCondition) {
        QuestConditionData questConditionData = questDataMap.get(questCondition.getQuest().getId()).getQuestConditionDataMap().get(questCondition.getId());
        return questConditionData.getMaxProgress();
    }

    /**
     * QuestCondition의 현재 진척률을 반환합니다.
     */
    public double getQuestConditionProgressPercent(QuestCondition questCondition) {
        QuestConditionData questConditionData = questDataMap.get(questCondition.getQuest().getId()).getQuestConditionDataMap().get(questCondition.getId());
        return questConditionData.getProgressPercent();
    }

    /**
     * QuestCondition을 진척시킵니다.
     * @param player null일 시 QuestCondition#ProgressOperation이 실행되지 않습니다.
     * @return 실패 여부
     */
    public boolean progressQuestCondition(Player player, QuestCondition questCondition) {
        Quest quest = questCondition.getQuest();

        if (!questDataMap.get(quest.getId()).getQuestConditionDataMap().containsKey(questCondition.getId()))
            return false;
        if (!getQuestState(quest).equals(QuestState.PROCEEDING))
            return false;

        QuestConditionData questConditionData = questDataMap.get(quest.getId()).getQuestConditionDataMap().get(questCondition.getId());

        if (questConditionData.isCompleted())
            return false;

        questConditionData.progress();

        if (questConditionData.getMaxProgress() != -1) {
            if (player != null && !questCondition.getProgressOperations().isEmpty()) {
                for (String ops : questCondition.getProgressOperationStrings()) {
                    Operation operation = Operation.parseOperation(ops);

                    if (operation != null) {
                        operation.execute(player, ops, questCondition);
                    }
                }
            }
            if (questConditionData.getCurrentProgress() >= questConditionData.getMaxProgress())
                completeQuestCondition(player, questCondition);
        }
        return true;
    }

    /**
     * QuestCondition을 완료시킵니다.
     * @param player null일 시 QuestCondition#CompleteOperation이 실행되지 않습니다.
     * @return 실패 여부
     */
    public boolean completeQuestCondition(Player player, QuestCondition questCondition) {
        Quest quest = questCondition.getQuest();

        if (!questDataMap.get(quest.getId()).getQuestConditionDataMap().containsKey(questCondition.getId()))
            return false;
        if (!getQuestState(quest).equals(QuestState.PROCEEDING))
            return false;

        QuestConditionData questConditionData = questDataMap.get(quest.getId()).getQuestConditionDataMap().get(questCondition.getId());

        if (questConditionData.isCompleted())
            return false;

        questConditionData.complete();

        if (player != null && !questCondition.getCompleteOperations().isEmpty()) {
            for (String ops : questCondition.getCompleteOperationStrings()) {
                Operation operation = Operation.parseOperation(ops);

                if (operation != null) {
                    operation.execute(player, ops, questCondition);
                }
            }
        }

        for (QuestConditionData qcd : questDataMap.get(quest.getId()).getQuestConditionDataMap().values()) {
            if (qcd.isCompleted() == false)
                return true;
        }

        // 퀘스트 완료
        completeQuest(player, quest, questCondition);
        return true;
    }

    /**
     * Quest를 완료합니다.
     * @param player null일 시 Quest#CompleteOperation이 실행되지 않습니다.
     * @return 실패 여부
     */
    public boolean completeQuest(Player player, Quest quest, QuestCondition questCondition) {
        if (!questDataMap.containsKey(quest.getId()))
            return false;

        QuestData questData = questDataMap.get(quest.getId());
        questData.setQuestState(QuestState.COMPLETED);

        if (player != null && !quest.getQuestCompleteOperations().isEmpty()) {
            for (String ops : quest.getQuestCompleteOperationStrings()) {
                Operation operation = Operation.parseOperation(ops);

                if (operation != null) {
                    operation.execute(player, ops, questCondition);
                }
            }
        }

        return true;
    }

    /**
     * Quest를 취소합니다.
     * @return 실패 여부
     */
    public boolean revokeQuest(Quest quest) {
        if (!questDataMap.containsKey(quest.getId()))
            return false;

        QuestData questData = questDataMap.get(quest.getId());
        questData.setQuestState(QuestState.NOT_REQUESTED);

        return true;
    }

}
