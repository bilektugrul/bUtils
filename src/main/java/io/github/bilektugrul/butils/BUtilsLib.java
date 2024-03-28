package io.github.bilektugrul.butils;

import org.bukkit.plugin.java.JavaPlugin;

public class BUtilsLib {

    private static JavaPlugin INSTANCE;

    public static void setUsingPlugin(JavaPlugin plugin) {
        INSTANCE = plugin;
    }

    public static JavaPlugin getInstance() {
        return INSTANCE;
    }

}