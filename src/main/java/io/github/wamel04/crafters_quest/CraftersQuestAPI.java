package io.github.wamel04.crafters_quest;

import io.github.wamel04.crafters_quest.config.ConfigManager$Quest;
import io.github.wamel04.crafters_quest.config.ConfigManager$QuestDataContainer;
import io.github.wamel04.crafters_quest.npc.QuestNPC;
import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.QuestDataContainer;
import io.github.wamel04.crafters_quest.quest.QuestState;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CraftersQuestAPI {

    /**
     * 퀘스트 상태를 가져옵니다.
     */
    public static CompletableFuture<QuestState> getQuestState(String uuid, String questId) {
        return CompletableFuture.supplyAsync(() -> {
            if (!Quest.questMap.containsKey(questId))
                return QuestState.NOT_REQUESTED;

            Quest quest = Quest.questMap.get(questId);

            QuestDataContainer questDataContainer = QuestDataContainer.get(uuid).join();

            return questDataContainer.getQuestState(quest);
        }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );
    }

    /**
     * 퀘스트 상태를 설정합니다.
     * <p>
     *     퀘스트 완료시, 해당 퀘스트의 첫번째 QuestCondition을 사용합니다.
     * </p>
     * @return
     */
    public static CompletableFuture<Void> setQuestState(String uuid, String questId, QuestState questState) {
        return CompletableFuture.runAsync(() -> {
            if (!Quest.questMap.containsKey(questId))
                return;

            Quest quest = Quest.questMap.get(questId);

            QuestDataContainer questDataContainer = QuestDataContainer.get(uuid).join();

            if (questState.equals(QuestState.COMPLETED)) {
                Player player = Bukkit.getPlayer(UUID.fromString(uuid));
                QuestCondition firstQuestCondition = new ArrayList<>(quest.getQuestConditionMap().values()).get(0);

                questDataContainer.completeQuest(player, quest, firstQuestCondition);
            } else {
                questDataContainer.revokeQuest(quest);
                questDataContainer.setQuestState(quest, questState);
            }

            ConfigManager$QuestDataContainer.save(uuid).join();
        }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );
    }

    /**
     * 현재 플레이어와 대화 중인 NPC를 반환합니다.
     */
    public static QuestNPC getTalkingNPC(Player player) {
        return QuestNPC.playerTalkingNPCMap.get(player);
    }

    /**
     * 플레이어의 대화 스크립트 실행 여부를 반환합니다.
     */
    public static boolean isTalking(Player player) {
        return QuestNPC.playerTalkingNPCMap.containsKey(player);
    }

    /**
     * 플레이어의 대화 스크립트를 종료합니다.
     */
    public static void unTalk(Player player) {
        if (isTalking(player)) {
            QuestNPC questNPC = getTalkingNPC(player);
            questNPC.unTalk(player);
        }
    }

    /**
     * 해당 ID의 퀘스트를 가져옵니다.
     */
    public static Quest getQuest(String questId) {
        return Quest.questMap.get(questId);
    }

    /**
     * 해당 카테고리의 퀘스트들을 반환합니다.
     */
    public static List<Quest> getQuests(String... categories) {
        List<Quest> quests = new ArrayList<>();

        for (Quest quest : Quest.questMap.values()) {
            for (String category : categories) {
                if (!quest.getCategory().equals(category))
                    continue;

                quests.add(quest);
            }
        }

        return quests;
    }

    /**
     * 해당 파일을 읽어와 퀘스트 목록에 등록합니다.
     */
    public static CompletableFuture<Void> registerQuest(File file, String category) {
        return ConfigManager$Quest.read(file, category);
    }

    /**
     * 해당 퀘스트를 퀘스트 목록에서 해제합니다.
     * @return 성공 여부
     */
    public static boolean unregisterQuest(String questId) {
        if (!Quest.questMap.containsKey(questId))
            return false;

        Quest.questMap.remove(questId);
        return true;
    }

}
