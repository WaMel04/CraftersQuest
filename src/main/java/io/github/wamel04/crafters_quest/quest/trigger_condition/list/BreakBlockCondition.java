package io.github.wamel04.crafters_quest.quest.trigger_condition.list;

import io.github.wamel04.crafters_quest.quest.trigger_condition.ProgressTriggerCondition;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerConditionType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakBlockCondition extends ProgressTriggerCondition {

    // BREAK_BLOCK 'GRASS_BLOCK' '10'
    public BreakBlockCondition(String symbol, TriggerConditionType type) {
        super("BREAK_BLOCK", TriggerConditionType.PROGRESSIVE);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        Material type = event.getBlock().getType();

        match(player, this, condStr -> {
            Material cType = Material.getMaterial(getFactorMap(condStr, "type", "amount").get("type"));

            return type.equals(cType);
        });
    }

}
