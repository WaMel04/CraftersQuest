package io.github.wamel04.crafters_quest.file_util;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;

import java.io.File;

public class FileManager {

    private static CraftersQuestPlugin plugin = CraftersQuestPlugin.getInstance();

    public static void init() {
        // npc example initialize
        try {
            File file = new File(plugin.getDataFolder(), "npcs");

            if (!file.exists())
                JarUtil.copyFolderFromJar("npcs", plugin.getDataFolder(), JarUtil.CopyOption.COPY_IF_NOT_EXIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // quest example initialize
        try {
            File file = new File(plugin.getDataFolder(), "quests");

            if (!file.exists())
                JarUtil.copyFolderFromJar("quests", plugin.getDataFolder(), JarUtil.CopyOption.COPY_IF_NOT_EXIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // item example initialize
        try {
            File file = new File(plugin.getDataFolder(), "items");

            if (!file.exists())
                JarUtil.copyFolderFromJar("items", plugin.getDataFolder(), JarUtil.CopyOption.COPY_IF_NOT_EXIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // docs example initialize
        try {
            File file = new File(plugin.getDataFolder(), "docs");

            if (!file.exists())
                JarUtil.copyFolderFromJar("docs", plugin.getDataFolder(), JarUtil.CopyOption.COPY_IF_NOT_EXIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
