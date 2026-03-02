package io.github.wamel04.crafters_quest.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Util {

    public static ItemStack getSkull(String url, String name, String... lores) {
        String s = "http://textures.minecraft.net/texture/" + url;

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (name != null) {
            meta.setDisplayName(name);
            skull.setItemMeta(meta);
        }
        if (lores != null) {
            meta.setLore(Arrays.stream(lores).collect(Collectors.toList()));
            skull.setItemMeta(meta);
        }

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), "");
        PlayerTextures textures = profile.getTextures();

        try {
            textures.setSkin(new URL(s));
            meta.setOwnerProfile(profile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        skull.setItemMeta(meta);
        return skull;
    }

    public static String getColoredString(String s) {
        if (s == null)
            return null;

        s = s.replace("&", "§");

        String regex = "<color:(\\w+)>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);

        StringBuffer resultString = new StringBuffer();

        boolean isFound = false;
        while (matcher.find()) {
            String colorCode = matcher.group(1);  // match.group(1)은 괄호 안의 내용, 여기서는 색상코드
            matcher.appendReplacement(resultString, ChatColor.of("#" + colorCode).toString());

            isFound = true;
        }

        if (isFound) {
            matcher.appendTail(resultString);

            return resultString.toString();
        } else {
            return s;
        }
    }

}
