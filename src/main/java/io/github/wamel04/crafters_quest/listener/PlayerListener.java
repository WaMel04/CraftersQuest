package io.github.wamel04.crafters_quest.listener;

import io.github.wamel04.crafters_quest.config.ConfigManager$QuestDataContainer;
import io.github.wamel04.crafters_quest.quest.QuestDataContainer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();

        ConfigManager$QuestDataContainer.load(uuid).thenAcceptAsync(questDataContainer -> QuestDataContainer.questDataContainerMap.put(uuid, questDataContainer))
                .exceptionally(
                        ex -> {
                            ex.printStackTrace();
                            return null;
                        }
                );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ConfigManager$QuestDataContainer.save(event.getPlayer().getUniqueId().toString())
                .exceptionally(
                        ex -> {
                            ex.printStackTrace();
                            return null;
                        }
                );
    }

}
