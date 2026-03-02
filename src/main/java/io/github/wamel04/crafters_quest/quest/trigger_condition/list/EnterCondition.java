package io.github.wamel04.crafters_quest.quest.trigger_condition.list;

import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerConditionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

public class EnterCondition extends TriggerCondition {

    // ENTER '-10' 'x' '-10' '10' 'x' '10' 'world'
    public EnterCondition(String symbol, TriggerConditionType type) {
        super("ENTER", TriggerConditionType.COMPLETED_ONCE);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null || (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()))
            return;

        match(player, this, condStr -> isLocationInRange(to, condStr));
    }

    private boolean isLocationInRange(Location location, String conditionString) {
        Map<String, String> factorMap = getFactorMap(conditionString, "xMin", "yMin", "zMin",
                "xMax", "yMax", "zMax", "world");

        Map<String, Double> locMap = new HashMap<>() {{
            put("x", location.getX());
            put("y", location.getY());
            put("z", location.getZ());
        }};

        for (Map.Entry<String, Double> entry : locMap.entrySet()) {
            String coordinateName = entry.getKey();
            Double value = entry.getValue();

            if (factorMap.get(coordinateName + "Min").equalsIgnoreCase("x")) {
                if (!factorMap.get(coordinateName + "Max").equalsIgnoreCase("x")) {
                    if (value > Double.parseDouble(factorMap.get(coordinateName + "Max"))) {
                        return false;
                    }
                }
            } else {
                if (!factorMap.get(coordinateName + "Max").equalsIgnoreCase("x")) {
                    if (value > Double.parseDouble(factorMap.get(coordinateName + "Max"))) {
                        return false;
                    }
                }
                if (value < Double.parseDouble(factorMap.get(coordinateName + "Min"))) {
                    return false;
                }
            }
        }

        return true;
    }

}

