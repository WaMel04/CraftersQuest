package io.github.wamel04.crafters_quest.operation;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.QuestDataContainer;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerConditionType;
import io.github.wamel04.crafters_quest.util.Util;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Operation implements Listener {

    public static Map<String, Operation> operationMap = new HashMap<>();

    public static Operation parseOperation(String conditionString) {
        String firstWord;

        if (conditionString.contains(" "))
            firstWord = conditionString.substring(0, conditionString.indexOf(" "));
        else
            firstWord = conditionString;

        for (Operation operation : operationMap.values()) {
            if (firstWord.equalsIgnoreCase(operation.getSymbol())) {
                return operation;
            }
        }

        return null;
    }

    protected String symbol;

    public Operation(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public abstract void execute(Player player, String operationString, QuestCondition questCondition);

    /**
     * 인자의 이름을 지정하여 Map 형태로 받아와 리턴합니다.
     * <p>
     *     getFactorMap("$bossbar 'RED' 'SOLID' '10' '보스바 내용'", "color", "style", "duration", "message") 실행 시
     * </p>
     * <p>
     *     color: RED
     * </p>
     * <p>
     *     style: SOLID
     * </p>
     * <p>
     *     duration: 10
     * </p>
     * <p>
     *     message: 보스바 내용
     * </p>
     */
    protected HashMap<String, String> getFactorMap(String operationString, String... keys) {
        operationString = Util.getColoredString(operationString);

        HashMap<String, String> factorMap = new HashMap<>();

        String pattern = "'([^']+)'";

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(operationString);

        // 매칭된 문자열 수와 키 배열의 길이를 비교하여 짝이 맞는지 확인
        int matchCount = 0;
        while (matcher.find() && matchCount < keys.length) {
            String value = matcher.group(1); // 매칭된 문자열 추출
            String key = keys[matchCount];   // 현재 키

            factorMap.put(key, value);

            matchCount++;
        }

        return factorMap;
    }

    /**
     * Placeholder를 처리한 메세지를 반환합니다.
     * @param questCondition null로 설정 시 퀘스트 이외의 호출을 의미합니다.
     */
    protected CompletableFuture<String> getReplacedMessage(Player player, String message, QuestCondition questCondition) {
        return QuestDataContainer.get(player.getUniqueId().toString())
                .thenApply(questDataContainer -> {
                    String result = Util.getColoredString(message);

                    if (CraftersQuestPlugin.hasPlaceHolderAPI())
                        result = PlaceholderAPI.setPlaceholders(player, result);
                    if (questCondition == null) {
                        result = result.replace("%player_name%", player.getName());
                    } else {
                        Quest quest = questCondition.getQuest();

                        result = result.replace("%quest_name%", questCondition.getQuest().getName());
                        result = result.replace("%player_name%", player.getName());
                        result = result.replace("%condition_name%", questCondition.getName());

                        if (result.contains("%condition_name:")) {
                            for (QuestCondition qCondition : quest.getQuestConditionMap().values()) {
                                result = result.replace("%condition_name:" + qCondition.getId() + "%", qCondition.getName());
                            }
                        }
                        if (questCondition.getTriggerCondition().getType().equals(TriggerConditionType.PROGRESSIVE)) {
                            result = result.replace("%current_progress%", String.valueOf(questDataContainer.getQuestConditionCurrentProgress(questCondition)));

                            if (result.contains("%current_progress:")) {
                                for (QuestCondition qCondition : quest.getQuestConditionMap().values()) {
                                    result = result.replace("%current_progress:" + qCondition.getId() + "%",
                                            String.valueOf(questDataContainer.getQuestConditionCurrentProgress(qCondition)));
                                }
                            }

                            result = result.replace("%max_progress%", String.valueOf(questDataContainer.getQuestConditionMaxProgress(questCondition)));

                            if (result.contains("%max_progress:")) {
                                for (QuestCondition qCondition : quest.getQuestConditionMap().values()) {
                                    result = result.replace("%max_progress:" + qCondition.getId() + "%",
                                            String.valueOf(questDataContainer.getQuestConditionMaxProgress(qCondition)));
                                }
                            }

                            result = result.replace("%progress_percent%", String.valueOf(questDataContainer.getQuestConditionProgressPercent(questCondition)));

                            if (result.contains("%progress_percent:")) {
                                for (QuestCondition qCondition : quest.getQuestConditionMap().values()) {
                                    result = result.replace("%progress_percent:" + qCondition.getId() + "%",
                                            String.valueOf(questDataContainer.getQuestConditionProgressPercent(qCondition)));
                                }
                            }
                        }
                    }

                    return result;
                }).exceptionally(ex -> {
                            ex.printStackTrace();
                            return null;
                        }
                );
    }

}
