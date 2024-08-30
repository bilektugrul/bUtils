package io.github.bilektugrul.butils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###.#");

    public static DateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

    public static void noPermission(CommandSender sender) {
        sender.sendMessage(getMessage("no-permission", sender));
    }

    public static FileConfiguration getConfig() {
        return BUtilsLib.getInstance().getConfig();
    }

    public static int getInt(String path) {
        return BUtilsLib.getInstance().getConfig().getInt(path);
    }

    public static long getLong(String path) {
        return BUtilsLib.getInstance().getConfig().getLong(path);
    }

    public static String getString(String string) {
        return BUtilsLib.getInstance().getConfig().getString(string);
    }

    public static String getColoredString(String string) {
        return colored(getString(string));
    }

    public static boolean getBoolean(String string) {
        return BUtilsLib.getInstance().getConfig().getBoolean(string);
    }

    public static List<String> getStringList(String string) {
        return BUtilsLib.getInstance().getConfig().getStringList(string);
    }

    public static Location getLocation(YamlConfiguration yaml, String key) {
        return (Location) yaml.get(key);
    }

    public static String getMessage(String msg, CommandSender sender) {
        String message = listToString(colored(getStringList("messages." + msg)));
        if (sender instanceof Player player) {
            message = message.replace("%player%", player.getName());
        }

        return message
                .replace("%prefix%", getColoredString("prefix"))
                .replace("%prefix-2%", getColoredString("prefix-2"));
    }

    public static int getInt(FileConfiguration config, String path) {
        return config.getInt(path);
    }

    public static long getLong(FileConfiguration config, String path) {
        return config.getLong(path);
    }

    public static String getString(FileConfiguration config, String string) {
        return config.getString(string);
    }

    public static String getColoredString(FileConfiguration config, String string) {
        return colored(getString(config, string));
    }

    public static boolean getBoolean(FileConfiguration config, String string) {
        return config.getBoolean(string);
    }

    public static List<String> getStringList(FileConfiguration config, String string) {
        return config.getStringList(string);
    }

    public static String getMessage(FileConfiguration config, String msg, CommandSender sender) {
        String message = listToString(colored(getStringList(config, "messages." + msg)));
        if (sender instanceof Player player) {
            message = message.replace("%player%", player.getName());
        }

        return message
                .replace("%prefix%", getColoredString("prefix"))
                .replace("%prefix-2%", getColoredString("prefix-2"));
    }

    public static String colored(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> colored(List<String> strings) {
        List<String> list = new ArrayList<>();
        for (String str : strings) {
            list.add(colored(str));
        }
        return list;
    }

    public static String arrayToString(String[] array) {
        return String.join(" ", array);
    }

    public static String listToString(List<String> list) {
        return String.join("\n", list);
    }

    public static String listToStringLore(List<String> list) {
        return String.join("||", list);
    }

    public static String listToStringNoNl(List<String> list) {
        return String.join(" ", list);
    }

    public static String listToStringComma(List<String> list) {
        return String.join(", ", list);
    }

    public static boolean matchMode(String mode) {
        mode = mode.toLowerCase(Locale.ROOT);
        if (mode.contains("on") || mode.contains("true") || mode.contains("aç") || mode.contains("aktif")) {
            return true;
        } else if (mode.contains("off") || mode.contains("false") || mode.contains("kapat") || mode.contains("de-aktif") || mode.contains("deaktif")) {
            return false;
        }
        return false;
    }

    public static String fileToString(File file) throws IOException {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<String> content = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null)
            content.add(line);

        return listToString(content);
    }

    public static String millisToString(long millis) {
        Date date = new Date(millis);
        return dateFormat.format(date);
    }

    public static String moneyWithCommas(long l) {
        return decimalFormat.format(l);
    }

    public static void sendMessage(String msg, CommandSender sendTo) {
        String message = getMessage(msg, sendTo);
        sendTo.sendMessage(message);
    }

    // made by hakan-krgn
    public static int getMaximum(Player player, String perm, int def) {
        TreeSet<Integer> permMax = new TreeSet<>();
        for (PermissionAttachmentInfo permissionAttachmentInfo : player.getEffectivePermissions()) {
            String permission = permissionAttachmentInfo.getPermission();
            if (permission.contains(perm)) {
                permMax.add(Integer.parseInt(permission.replace(perm, "")));
            }
        }
        return !permMax.isEmpty() ? permMax.last() : def;
    }

    public static boolean isSameLoc(Location loc1, Location loc2) {
        return (loc1.getBlockX() == loc2.getBlockX()) && (loc1.getBlockY() == loc2.getBlockY()) && (loc1.getBlockZ() == loc2.getBlockZ());
    }

    public static void sendActionBar(Player player, String message) {
        if (player == null || message == null) return;
        String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);

        //1.10 and up
        if (!nmsVersion.startsWith("v1_9_R") && !nmsVersion.startsWith("v1_8_R")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            return;
        }

        //1.8.x and 1.9.x
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);

            Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
            Class<?> packet = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
            Object packetPlayOutChat;
            Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
            Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");

            Method method = null;
            if (nmsVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);

            Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
            packetPlayOutChat = ppoc.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);

            Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
            Object iCraftPlayer = handle.invoke(craftPlayer);
            Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(iCraftPlayer);
            Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packet);
            sendPacket.invoke(playerConnection, packetPlayOutChat);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}