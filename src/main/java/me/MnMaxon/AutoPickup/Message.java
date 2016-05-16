package me.MnMaxon.AutoPickup;

import org.bukkit.ChatColor;

/**
 * Created by MnMaxon on 5/26/2015.
 */
public enum Message {
    ERROR0NO_PERM("&cYou do not have permission to do that!"), SUCCESS0RELOADED("&aPlugin reloaded!"),
    SUCCESS0TOGGLE0PICKUP_OFF("&aAutoPickup &cDISABLED&a!"), SUCCESS0TOGGLE0PICKUP_ON("&aAutoPickup ENABLED!"),
    SUCCESS0TOGGLE0BLOCK_OFF("&aAutoBlock &cDISABLED&a!"), SUCCESS0TOGGLE0BLOCK_ON("&aAutoBlock ENABLED!"),
    SUCCESS0TOGGLE0SMELT_OFF("&aAutoSmelt &cDISABLED&a!"), SUCCESS0TOGGLE0SMELT_ON("&aAutoSmelt ENABLED!"),
    SUCCESS0BLOCKED_INVENTORY("&aYour inventory has been auto blocked!"), ERROR0BLOCKED_INVENTORY("&cNothing in your inventory could be auto blocked!"),
    SUCCESS0SMELTED_INVENTORY("&aYour inventory has been auto smelted!"), ERROR0SMELTED_INVENTORY("&cNothing in your inventory could be auto smelted!"),
    ERROR0FULL_INVENTORY(ChatColor.RED + "Your inventory is full!"), ERROR0BLACKLISTED0WORLD("&cYou are not allowed to do that in this world!"),
    SUCCESS0TOGGLE0AUTOSELL_ON("&aAutoSell ENABLED!"), SUCCESS0TOGGLE0AUTOSELL_OFF("&aAutoSell &cDISABLED&a!"), ERROR0NO_QUICKSELL("&cSorry, this command requires the plugin QuickSell"),
	ERROR0FULL_INVENTORY_SUB(ChatColor.YELLOW + "Go sell something!");
    String defaultMessage = "";
    String message = defaultMessage;

    Message(String defaultMessage) {
        this.defaultMessage = colorize(defaultMessage);
        this.message = defaultMessage;
    }

    public static void setup() {
        boolean update = false;
        for (Message message : values()) if (message.reload()) update = true;
        if (update) AutoPickupPlugin.MessageConfig.save();
    }

    private boolean reload() {
        String path = name().replace("_", " ").replace("0", ".");
        message = colorize(AutoPickupPlugin.MessageConfig.getString(path));
        if (message != null) return false;
        message = defaultMessage;
        AutoPickupPlugin.MessageConfig.set(path, decolorize(defaultMessage));
        return true;
    }

    public static String decolorize(String msg) {
        if (msg == null) return null;
        return ChatColor.translateAlternateColorCodes('§', msg).replaceAll("Å", "");
    }

    public static String colorize(String msg) {
        if (msg == null) return null;
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public String toString() {
        return message;
    }
}
