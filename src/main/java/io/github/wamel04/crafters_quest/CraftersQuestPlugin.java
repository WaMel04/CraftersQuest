package io.github.wamel04.crafters_quest;

import com.comphenix.protocol.ProtocolLibrary;
import io.github.wamel04.crafters_quest.command.*;
import io.github.wamel04.crafters_quest.config.ConfigManager$CraftersNPC;
import io.github.wamel04.crafters_quest.config.ConfigManager$Item;
import io.github.wamel04.crafters_quest.config.ConfigManager$Quest;
import io.github.wamel04.crafters_quest.config.ConfigManager$QuestDataContainer;
import io.github.wamel04.crafters_quest.file_util.FileManager;
import io.github.wamel04.crafters_quest.item.QuestItem;
import io.github.wamel04.crafters_quest.listener.NPCListener;
import io.github.wamel04.crafters_quest.listener.PlayerListener;
import io.github.wamel04.crafters_quest.operation.OperationRegister;
import io.github.wamel04.crafters_quest.quest.gui.listener.PreviousQuestGUIListener;
import io.github.wamel04.crafters_quest.quest.gui.listener.QuestGUIListener;
import io.github.wamel04.crafters_quest.quest.gui.listener.QuestItemGUIListener;
import io.github.wamel04.crafters_quest.quest.gui.listener.QuestItemMenuGUIListener;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerConditionRegister;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public final class CraftersQuestPlugin extends JavaPlugin {

    // hooks
    private static boolean hasPlaceHolderAPI = false;

    public static boolean hasPlaceHolderAPI() {
        return hasPlaceHolderAPI;
    }

    private static CraftersQuestPlugin instance;

    private static int BACKUP_TIME = 60;
    private static BukkitTask backupTask;

    public static final String PREFIX = "§6[CraftersQuest] §f";

    @Override
    public void onEnable() {
        instance = this;

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"))
            hasPlaceHolderAPI = true;

        registerListeners();
        registerCommands();

        saveDefaultConfig();
        FileManager.init();

        loadConfig();

        TriggerConditionRegister.start();
        OperationRegister.start();

        ConfigManager$Quest.load();
        ConfigManager$Item.load();
        ConfigManager$CraftersNPC.load();
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ConfigManager$QuestDataContainer.save(player.getUniqueId().toString());
        }
        for (QuestItem questItem : QuestItem.questItemMap.values()) {
            ConfigManager$Item.save(questItem);
        }

        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
    }

    public static CraftersQuestPlugin getInstance() {
        return instance;
    }

    private static void registerListeners() {
        instance.getServer().getPluginManager().registerEvents(new PlayerListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new NPCListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new QuestGUIListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new PreviousQuestGUIListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new QuestItemMenuGUIListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new QuestItemGUIListener(), instance);
    }

    private static void registerCommands() {
        instance.getCommand("q").setExecutor(new CMD_Quest());
        instance.getCommand("quest").setExecutor(new CMD_Quest());
        instance.getCommand("퀘스트").setExecutor(new CMD_Quest());

        instance.getCommand("pq").setExecutor(new CMD_PreviousQuest());
        instance.getCommand("pquest").setExecutor(new CMD_PreviousQuest());
        instance.getCommand("이전퀘스트").setExecutor(new CMD_PreviousQuest());

        instance.getCommand("getqueststate").setExecutor(new CMD_GetQuestState());
        instance.getCommand("setqueststate").setExecutor(new CMD_SetQuestState());

        instance.getCommand("questitem").setExecutor(new CMD_QuestItem());
        instance.getCommand("qitem").setExecutor(new CMD_QuestItem());
        instance.getCommand("qi").setExecutor(new CMD_QuestItem());

        instance.getCommand("registerquestitem").setExecutor(new CMD_RegisterQuestItem());
        instance.getCommand("rqitem").setExecutor(new CMD_RegisterQuestItem());
        instance.getCommand("rqi").setExecutor(new CMD_RegisterQuestItem());

        instance.getCommand("qreload").setExecutor(new CMD_QuestReload());
        instance.getCommand("questreload").setExecutor(new CMD_QuestReload());
    }

    public static void loadConfig() {
        File configFile = new File(instance.getDataFolder(), "config.yml");
        BACKUP_TIME = Optional.of(YamlConfiguration.loadConfiguration(configFile).getInt("backup-time"))
                .filter(v -> v > 0)
                .orElse(60);

        if (backupTask != null)
            backupTask.cancel();

        backupTask = startBackupTask(BACKUP_TIME);
    }

    private static BukkitTask startBackupTask(int backupTime) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ConfigManager$QuestDataContainer.save(player.getUniqueId().toString());
            }

            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            File sourceDir = new File(instance.getDataFolder(), "quest_data");
            File backupDir = new File(instance.getDataFolder(), "backups" + File.separator + timeStamp);

            // 3. 폴더 복사 실행
            if (sourceDir.exists()) {
                try {
                    Files.walk(sourceDir.toPath()).forEach(path -> {
                        try {
                            Path dest = backupDir.toPath().resolve(sourceDir.toPath().relativize(path));
                            if (Files.isDirectory(path)) {
                                Files.createDirectories(dest);
                            } else {
                                Files.copy(path, dest, StandardCopyOption.REPLACE_EXISTING);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    Bukkit.getConsoleSender().sendMessage(PREFIX + "§6플레이어 데이터를 백업했습니다. (" + timeStamp + ")");
                } catch (IOException e) {
                    Bukkit.getConsoleSender().sendMessage(PREFIX + "§6백업 중 오류가 발생했습니다! (" + e.getMessage() + ")");
                    e.printStackTrace();
                }
            }

        }, 20 * 60 * backupTime, 20 * 60 * backupTime);
    }

}
