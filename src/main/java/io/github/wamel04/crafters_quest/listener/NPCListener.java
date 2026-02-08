package io.github.wamel04.crafters_quest.listener;

import io.github.wamel04.crafters_quest.CraftersQuestAPI;
import io.github.wamel04.crafters_quest.npc.QuestNPC;
import io.github.wamel04.crafters_quest.npc.angle_updater.AngleUpdater;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;

public class NPCListener implements Listener {

    @EventHandler
    public void onNPCRightClick(PlayerInteractAtEntityEvent event) {
        if (!event.getHand().equals(EquipmentSlot.HAND))
            return;

        Entity target = event.getRightClicked();

        if (!target.hasMetadata("NPC")) // CitizenNPC가 아닐 경우
            return;

        Player player = event.getPlayer();
        String npcName = ChatColor.stripColor(target.getName());

        for (QuestNPC questNPC : QuestNPC.questNPCMap.values()) {
            if (questNPC.getName().equals(npcName)) {
                if (questNPC.getPermission() != null && !player.hasPermission(questNPC.getPermission()))
                    return;
                if (questNPC.isTalking(player)) { // 이미 대화 중일 경우, 페이지를 넘김
                    if (questNPC.getPlayerNextPage(player) == null) // 다음 페이지가 없는 경우
                        questNPC.unTalk(player);
                    else
                        questNPC.openPage(player, questNPC.getPlayerNextPage(player));

                } else { // 대화 중이지 않을 경우
                    questNPC.talk(player);

                    AngleUpdater angleUpdater = new AngleUpdater(player, target);
                    angleUpdater.start();
                    AngleUpdater.playerMap.put(player, angleUpdater);
                }

                return;
            }
        }
    }

    @EventHandler
    public void onSneaking(PlayerToggleSneakEvent event) {
        CraftersQuestAPI.unTalk(event.getPlayer());
    }

    @EventHandler
    public void onJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getFrom().getBlockY() < event.getTo().getBlockY() && !player.isSwimming() && !player.isFlying())
            CraftersQuestAPI.unTalk(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        CraftersQuestAPI.unTalk(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        CraftersQuestAPI.unTalk(event.getPlayer());
    }

}
