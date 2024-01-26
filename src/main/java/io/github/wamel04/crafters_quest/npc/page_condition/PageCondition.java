package io.github.wamel04.crafters_quest.npc.page_condition;

import io.github.wamel04.crafters_quest.npc.QuestNPC;
import io.github.wamel04.crafters_quest.quest.Quest;
import io.github.wamel04.crafters_quest.quest.QuestDataContainer;
import io.github.wamel04.crafters_quest.quest.QuestState;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PageCondition {

    String conditionString;
    String page;

    public PageCondition(String conditionString, String page) {
        this.conditionString = conditionString;
        this.page = page;
    }

    public String getConditionString() {
        return conditionString;
    }

    public String getPage() {
        return page;
    }

    public static CompletableFuture<String> getPage(Player player, QuestNPC npc) {
        return CompletableFuture.supplyAsync(() -> {
            for (PageCondition pageCondition : npc.getFirstPageConditions()) {
                String conditionString = pageCondition.getConditionString().replace(" ", "");

                if (conditionString.equals("DEFAULT") || evaluateCondition(player, conditionString).join())
                    return pageCondition.getPage();
            }

            return null;
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
            }
        );
    }

    private static CompletableFuture<Boolean> evaluateCondition(Player player, String condition) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> splitConditions = getSplitConditions(condition, Separator.AND);

            if (splitConditions.size() == 1) {
                splitConditions = getSplitConditions(condition, Separator.OR);

                if (splitConditions.size() == 1) {
                    return evaluateSingleCondition(player, splitConditions.get(0)).join();
                } else {
                    for (String subCondition : splitConditions) {
                        if (subCondition.contains("(") && subCondition.contains(")")) {
                            if (evaluateCondition(player, subCondition.substring(1, subCondition.length()-1)).join())
                                return true;
                        } else {
                            if (evaluateCondition(player, subCondition).join())
                                return true;
                        }
                    }
                    return false;
                }
            } else {
                for (String subCondition : splitConditions) {
                    if (subCondition.contains("(") && subCondition.contains(")")) {
                        if (!evaluateCondition(player, subCondition.substring(1, subCondition.length()-1)).join())
                            return false;
                    } else {
                        if (!evaluateCondition(player, subCondition).join())
                            return false;
                    }
                }
                return true;
            }
        }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );
    }

    private static CompletableFuture<Boolean> evaluateSingleCondition(Player player, String conditionString) {
        return CompletableFuture.supplyAsync(() -> {
            QuestDataContainer questDataContainer = QuestDataContainer.get(player.getUniqueId().toString()).join();

            String cs = conditionString;
            cs = cs.replaceAll("\\(", "").replaceAll("\\)", "");

            if (cs.startsWith("NOT_REQUESTED")) {
                String questId = cs.replace("NOT_REQUESTED", "");

                if (!Quest.questMap.containsKey(questId))
                    return false;


                Quest quest = Quest.questMap.get(questId);

                if (questDataContainer.getQuestState(quest).equals(QuestState.NOT_REQUESTED))
                    return true;
                else
                    return false;
            } else if (cs.startsWith("PROCEEDING")) {
                String questId = cs.replace("PROCEEDING", "");

                if (!Quest.questMap.containsKey(questId))
                    return false;

                Quest quest = Quest.questMap.get(questId);

                if (questDataContainer.getQuestState(quest).equals(QuestState.PROCEEDING))
                    return true;
                else
                    return false;
            } else if (cs.startsWith("COMPLETED")) {
                String questId = cs.replace("COMPLETED", "");

                if (!Quest.questMap.containsKey(questId))
                    return false;

                Quest quest = Quest.questMap.get(questId);

                if (questDataContainer.getQuestState(quest).equals(QuestState.COMPLETED))
                    return true;
                else
                    return false;
            }

            return false;
        }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                }
        );
    }

    // ((A || B) && C) || D

    // A || B

    // (A || B) && C

    // D && ((A || B) && C)

    // (A || B) && (C || D)
    private static List<String> getSplitConditions(String condition, Separator separator) {
        List<String> conditions = new ArrayList<>();

        int openParentheses = 0;
        int start = 0;
        int findIndex = -1;
        for (int i = 0; i < condition.length(); i++) {
            char ch = condition.charAt(i);
            if (ch == '(') {
                openParentheses++;
            } else if (ch == ')') {
                openParentheses--;
            } else if (ch == separator.getSeparator().charAt(0) && openParentheses == 0) {
                if (condition.charAt(i+1) == separator.getSeparator().charAt(1)) {
                    findIndex = i;

                    conditions.add(condition.substring(start, i));
                    start = i + separator.getSeparator().length();
                }
            }
        }
        if (findIndex != -1) {
            conditions.add(condition.substring(findIndex + separator.getSeparator().length()));
        } else {
            conditions.add(condition);
        }

        return conditions;
    }

    private enum Separator {
        OR("||"), AND("&&");

        private HashMap<Separator, String> map = new HashMap<>();

        Separator(String s) {
            map.put(this, s);
        }

        private String getSeparator() {
            return map.get(this);
        }
    }

}
