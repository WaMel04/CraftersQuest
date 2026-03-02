package io.github.wamel04.crafters_quest.quest.trigger_condition;

import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.QuestDataContainer;
import io.github.wamel04.crafters_quest.quest.quest_condition.QuestCondition;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TriggerCondition implements Listener {

    public static Map<String, TriggerCondition> triggerConditionMap = new HashMap<>();

    public static TriggerCondition parseTriggerCondition(String conditionString) {
        String firstWord = conditionString.substring(0, conditionString.indexOf(" "));

        for (TriggerCondition triggerCondition : triggerConditionMap.values()) {
            if (firstWord.equalsIgnoreCase(triggerCondition.getSymbol())) {
                return triggerCondition;
            }
        }

        return null;
    }

    protected String symbol;
    protected TriggerConditionType type;

    public TriggerCondition(String symbol, TriggerConditionType type) {
        this.symbol = symbol;
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public TriggerConditionType getType() {
        return type;
    }

    /**
     * 인자의 이름을 지정하여 Map 형태로 받아와 리턴합니다.
     * <p>
     *     getFactorMap("KILL '코카콜라 북극곰' '10', "name", "amount") 실행 시
     * </p>
     * <p>
     *     name: 코카콜라 북극곰
     * </p>
     * <p>
     *     amount: 10
     * </p>
     */
    protected HashMap<String, String> getFactorMap(String operationString, String... keys) {
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
     * 트리거 컨디션의 이벤트를 호출시킨 플레이어가 이를 충족시킬 수 있으면, 컨디션을 완료(진척)합니다.
     */
    protected void match(Player player, TriggerCondition condition, Predicate<String> matcher) {
        QuestDataContainer.get(player.getUniqueId().toString())
                .thenAcceptAsync(qdc -> {
                    for (Quest quest : qdc.getProceedingQuests()) {
                        for (QuestCondition questCondition : quest.getQuestConditionMap().values()) {
                            if (!questCondition.getTriggerCondition().getSymbol().equalsIgnoreCase(condition.getSymbol()))
                                continue;

                            String conditionStr = questCondition.getTriggerConditionString();

                            if (matcher.test(conditionStr)) {
                                if (condition instanceof ProgressTriggerCondition) {
                                    qdc.progressQuestCondition(player, questCondition);
                                } else {
                                    qdc.completeQuestCondition(player, questCondition);
                                }
                            }
                        }
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });

    }

}
