package org.prag.mc.plugins.serverDatabaseController.Utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;
import java.util.logging.Level;

public class ItemStackSerializer {

    public static String serialize(ItemStack item) {
        if (item == null) return null;
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.set("item", item);
            String yaml = config.saveToString();
            return Base64.getEncoder().encodeToString(yaml.getBytes());
        } catch (Exception e) {

            Bukkit.getLogger().log(Level.SEVERE, AnsiConstants.ANSI_RED + "[ItemStackSerializer] Failed to serialize ItemStack: " + e.getMessage() + AnsiConstants.ANSI_RESET);
            return null;
        }
    }

    public static ItemStack deserialize(String data) {
        if (data == null || data.isEmpty()) return null;
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            String yaml = new String(bytes);
            YamlConfiguration config = new YamlConfiguration();
            config.loadFromString(yaml);
            return config.getItemStack("item");
        } catch (Exception e) {

            Bukkit.getLogger().log(Level.SEVERE, AnsiConstants.ANSI_RED + "[ItemStackSerializer] Failed to deserialize ItemStack data: " + e.getMessage() + AnsiConstants.ANSI_RESET);
            return null;
        }
    }
}