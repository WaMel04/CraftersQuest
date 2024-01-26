package io.github.wamel04.crafters_quest.operation;

import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class OperationRegister {

    private static CraftersQuestPlugin plugin = CraftersQuestPlugin.getInstance();

    public static void start() {
        Set<Class<?>> classes = getClasses("io.github.wamel04.crafters_quest.operation.list");

        for (Class clazz : classes) {
            register(clazz);
        }
    }

    private static Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new HashSet<>();

        try {
            JavaPlugin pluginObject = (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin(plugin.getName());
            Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);
            File file = (File) getFileMethod.invoke(pluginObject);

            try (JarFile jarFile = new JarFile(file)) {
                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName().replace("/", ".");

                    if (entryName.endsWith(".class") && entryName.startsWith(packageName + ".") && !entryName.contains("$")) {
                        String className = entryName.substring(0, entryName.length() - 6); // ".class" 제거
                        Class<?> clazz = Class.forName(className);
                        classes.add(clazz);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classes;
    }
    private static void register(Class<?> clazz) {
        try {
            Constructor constructor = clazz.getConstructors()[0];
            Object[] args = new Object[constructor.getParameterCount()];
            Object instance = constructor.newInstance(args);

            Operation operation = (Operation) instance;
            Operation.operationMap.put(operation.getSymbol(), operation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
