package io.github.wamel04.crafters_quest.quest.trigger_condition.list;

import io.github.wamel04.crafters_quest.quest.trigger_condition.ProgressTriggerCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerConditionType;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillCondition extends ProgressTriggerCondition {

    // KILL 'PIG' '10'
    // KILL '사그라지는 갑옷병' '10'
    public KillCondition(String symbol, TriggerConditionType type) {
        super("KILL", TriggerConditionType.PROGRESSIVE);
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null)
            return;

        Player player = event.getEntity().getKiller();
        EntityType type = event.getEntityType();
        String entityName = ChatColor.stripColor(event.getEntity().getName());

        match(player, this, condStr -> {
            String cEntityName = getFactorMap(condStr, "entityName", "amount").get("entityName");

            if (cEntityName.equalsIgnoreCase(type.name())) {
                return true;
            }
            if (cEntityName.equalsIgnoreCase(entityName)) {
                return true;
            }

            return false;
        });
    }

}

