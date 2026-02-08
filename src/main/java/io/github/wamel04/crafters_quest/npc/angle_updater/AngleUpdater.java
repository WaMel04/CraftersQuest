package io.github.wamel04.crafters_quest.npc.angle_updater;

import io.github.wamel04.crafters_quest.CraftersQuestAPI;
import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import io.github.wamel04.crafters_quest.npc.QuestNPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class AngleUpdater {

    public static final double MAX_YAW_DIFFERENCE = 30;
    public static final double MAX_PITCH_DIFFERENCE = 15;
    public static final double MIN_DIFFERENCE = 4;
    public static Map<Player, AngleUpdater> playerMap = new HashMap<>();

    private Player player;
    private Entity target;

    private BukkitTask task;

    public AngleUpdater(Player player, Entity target) {
        this.player = player;
        this.target = target;
    }

    public void start() {
        task = new BukkitRunnable() {
            PersonalAngleUpdater pAngleUpdater = new PersonalAngleUpdater(player, target, player, 0.05, true);
            PersonalAngleUpdater eAngleUpdater = new PersonalAngleUpdater(target, player, player, 0.05, false);
            
            @Override
            public void run() {
                if (!player.getWorld().equals(target.getWorld())
                || player.getLocation().distance(target.getLocation()) > MIN_DIFFERENCE
                || !QuestNPC.playerTalkingNPCMap.containsKey(player)) {
                    stop();
                    return;
                }
                
                pAngleUpdater.updateAngle();
                eAngleUpdater.updateAngle();
            }
        }.runTaskTimerAsynchronously(CraftersQuestPlugin.getInstance(), 0, 1);
    }

    public void stop() {
        task.cancel();

        playerMap.remove(player);
        CraftersQuestAPI.unTalk(player);
    }

}
