package io.github.wamel04.crafters_quest;

import io.github.wamel04.crafters_quest.command.CMD_GetQuestState;
import io.github.wamel04.crafters_quest.command.CMD_PreviousQuest;
import io.github.wamel04.crafters_quest.command.CMD_Quest;
import io.github.wamel04.crafters_quest.command.CMD_SetQuestState;
import io.github.wamel04.crafters_quest.config.ConfigManager$CraftersNPC;
import io.github.wamel04.crafters_quest.config.ConfigManager$Item;
import io.github.wamel04.crafters_quest.config.ConfigManager$Quest;
import io.github.wamel04.crafters_quest.config.ConfigManager$QuestDataContainer;
import io.github.wamel04.crafters_quest.file_util.FileManager;
import io.github.wamel04.crafters_quest.listener.NPCListener;
import io.github.wamel04.crafters_quest.listener.PlayerListener;
import io.github.wamel04.crafters_quest.listener.PreviousQuestGUIListener;
import io.github.wamel04.crafters_quest.listener.QuestGUIListener;
import io.github.wamel04.crafters_quest.operation.OperationRegister;
import io.github.wamel04.crafters_quest.quest.trigger_condition.TriggerConditionRegister;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class CraftersQuestPlugin extends JavaPlugin {

    // hooks
    private static boolean hasPlaceHolderAPI = false;

    public static boolean hasPlaceHolderAPI() {
        return hasPlaceHolderAPI;
    }

    private static CraftersQuestPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"))
            hasPlaceHolderAPI = true;

        registerListeners();
        registerCommands();

        FileManager.init();

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
    }

    public static CraftersQuestPlugin getInstance() {
        return instance;
    }

    private static void registerListeners() {
        instance.getServer().getPluginManager().registerEvents(new PlayerListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new NPCListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new QuestGUIListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new PreviousQuestGUIListener(), instance);
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
    }

}
