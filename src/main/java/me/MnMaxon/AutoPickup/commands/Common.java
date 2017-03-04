package me.MnMaxon.AutoPickup.commands;

import me.MnMaxon.AutoPickup.AutoPickupPlugin;
import me.MnMaxon.AutoPickup.Config;
import me.MnMaxon.AutoPickup.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class Common
{

    public Common()
    {}

    public static void displayHelp(CommandSender s)
    {
        ChatColor c1 = null;
        ChatColor c2 = null;
        Random random = new Random();
        while (c1 == null || c1 == ChatColor.MAGIC || c1 == ChatColor.ITALIC || c1 == ChatColor.BLACK || c1 == ChatColor.UNDERLINE || c1 == ChatColor.BOLD || c1 == ChatColor.RESET || c1 == ChatColor.STRIKETHROUGH)
        {
            c1 = ChatColor.values()[random.nextInt(ChatColor.values().length - 1)];
        }

        while (c2 == null || c2 == c1 || c2 == ChatColor.MAGIC || c2 == ChatColor.ITALIC || c2 == ChatColor.BLACK || c2 == ChatColor.UNDERLINE || c2 == ChatColor.BOLD || c2 == ChatColor.RESET || c2 == ChatColor.STRIKETHROUGH)
        {
            c2 = ChatColor.values()[random.nextInt(ChatColor.values().length - 1)];
        }

        ArrayList < String > messages = new ArrayList<String>();

        messages.add("AutoPickup-Displays this screen");

        if (Config.usingQuickSell)
        {
            messages.add("AutoSell toggle-Toggles auto sell");
        }

        messages.add("AutoPickup toggle-Toggles auto pickup");
        messages.add("AutoBlock toggle-Toggles auto block");
        messages.add("AutoBlock-Turns anything that can be into a block");
        messages.add("AutoSmelt-Smelts anything that can be smelted in your inventory");
        messages.add("AutoSmelt toggle-Toggles auto smelt");
        messages.add("AutoSmelt reload-Reloads the plugin");
        s.sendMessage(c1 + "==== " + c2 + AutoPickupPlugin.getPlugin(AutoPickupPlugin.class).getName() + c1 + " ====");
        for (String message:messages)
        {
            s.sendMessage(c2 + "/" + message.replace("-", c1 + " - "));
        }
        s.sendMessage(c1 + "For more help: " + c2 + "http://goo.gl/WdfLpK");
        s.sendMessage(c1 + "Shortcuts: " + c2 + "/ap = /AutoPickup, /ab = /AutoBlock, /as = /AutoSmelt");
    }

    public static void openGui(Player p)
    {
        if (Config.allowBlockGui && p.hasPermission("AutoPickup.BlockGui"))
        {
            p.sendMessage(ChatColor.RED + "You do not have permission to open the gui");
            return;
        }

        int size = 18;
        Inventory newInv = Bukkit.createInventory(null, size, ChatColor.BLUE + "AutoPickup");
        // AP|AB|AS|A$|FN|  |  |AS|AB
        // TO|TO|TO|TO|TO|  |  |HE|SU

        ItemStack[] conts = newInv.getContents();
        conts[0] = Util.easyItem(ChatColor.GREEN + "AutoPickup", Material.HOPPER, 1, 0, ChatColor.GRAY + "Sends mined blocks", ChatColor.GRAY + "straight to your inventory");
        conts[1] = Util.easyItem(ChatColor.GREEN + "AutoBlock", Material.IRON_BLOCK, 1, 0, ChatColor.GRAY + "Turns ingots into blocks");
        conts[2] = Util.easyItem(ChatColor.GREEN + "AutoSmelt", Material.FURNACE, 1, 0, ChatColor.GRAY + "Smelts all mined ores");

        if (Config.usingQuickSell) {
            conts[3] = Util.easyItem(ChatColor.GREEN + "AutoSell", Material.GOLD_INGOT, 1, 0, ChatColor.GRAY + "Sells any possible", ChatColor.GRAY + "mined blocks");
            conts[4] = Util.easyItem(ChatColor.GREEN + "FullNotify", Material.NOTE_BLOCK, 1, 0, ChatColor.GRAY + "Notifies you on full inventory");
        } else
        {
            conts[3] = Util.easyItem(ChatColor.GREEN + "FullNotify", Material.NOTE_BLOCK, 1, 0, ChatColor.GRAY + "Notifies you on full inventory");
        }

        String autoPickupName = (AutoPickupPlugin.autoPickup.contains(p.getName())) ? ChatColor.GREEN + "AutoPickup ENABLED" : ChatColor.RED + "AutoPickup DISABLED";
        String autoBlockName = (AutoPickupPlugin.autoBlock.contains(p.getName())) ? ChatColor.GREEN + "AutoBlock ENABLED" : ChatColor.RED + "AutoBlock DISABLED";
        String autoSmeltName = (AutoPickupPlugin.autoSmelt.contains(p.getName())) ? ChatColor.GREEN + "AutoSmelt ENABLED" : ChatColor.RED + "AutoSmelt DISABLED";
        String autoSellName = (AutoPickupPlugin.autoSell.contains(p.getName())) ? ChatColor.GREEN + "AutoSell ENABLED" : ChatColor.RED + "AutoSell DISABLED";
        String fullNotifyName = (AutoPickupPlugin.fullNotify.contains(p.getName())) ? ChatColor.GREEN + "FullNotify ENABLED" : ChatColor.RED + "FullNotify DISABLED";

        //TODO: make these real if statements
        int apDur = (AutoPickupPlugin.autoPickup.contains(p.getName())) ? 10: 8;
        int abDur = (AutoPickupPlugin.autoBlock.contains(p.getName())) ? 10 : 8;
        int asDur = (AutoPickupPlugin.autoSmelt.contains(p.getName())) ? 10 : 8;
        int aSellDur = (AutoPickupPlugin.autoSell.contains(p.getName())) ? 10 : 8;
        int fullNotifyDur = (AutoPickupPlugin.fullNotify.contains(p.getName())) ? 10 : 8;

        //TODO: make these real if statements
        conts[9] = (p.hasPermission("AutoPickup.Toggle")) ? Util.easyItem(autoPickupName, Material.INK_SACK, 1, apDur, ChatColor.GRAY + "Click to Toggle") : Util.easyItem(autoPickupName, Material.INK_SACK, 1, apDur);
        conts[10] = (p.hasPermission("AutoBlock.Toggle")) ? Util.easyItem(autoBlockName, Material.INK_SACK, 1, abDur, ChatColor.GRAY + "Click to Toggle") : Util.easyItem(autoBlockName, Material.INK_SACK, 1, abDur);
        conts[11] = (p.hasPermission("AutoSmelt.Toggle")) ? Util.easyItem(autoSmeltName, Material.INK_SACK, 1, asDur, ChatColor.GRAY + "Click to Toggle") : Util.easyItem(autoSmeltName, Material.INK_SACK, 1, asDur);

        if (Config.usingQuickSell) {
            conts[12] = (p.hasPermission("AutoSell.Toggle")) ? Util.easyItem(autoSellName, Material.INK_SACK, 1, aSellDur, ChatColor.GRAY + "Click to Toggle") : Util.easyItem(autoSellName, Material.INK_SACK, 1, aSellDur);
            conts[13] = (p.hasPermission("FullNotify.Toggle")) ? Util.easyItem(fullNotifyName, Material.INK_SACK, 1, fullNotifyDur, ChatColor.GRAY + "Click to Toggle") : Util.easyItem(fullNotifyName, Material.INK_SACK, 1, fullNotifyDur);
        } else
        {
            conts[12] = (p.hasPermission("FullNotify.Toggle")) ? Util.easyItem(fullNotifyName, Material.INK_SACK, 1, fullNotifyDur, ChatColor.GRAY + "Click to Toggle") : Util.easyItem(fullNotifyName, Material.INK_SACK, 1, fullNotifyDur);
        }

        ItemStack locked = Util.easyItem(ChatColor.RED + "LOCKED", Material.STAINED_GLASS_PANE, 1, 14);
        ItemStack empty = Util.easyItem(null, Material.STAINED_GLASS_PANE, 1, 7);

        //TODO: make these real if statements
        conts[7] = (p.hasPermission("AutoBlock.Command"))?Util.easyItem(ChatColor.GREEN + "Block Your Items", Material.IRON_INGOT, 1, 0):locked;
        conts[8] = (p.hasPermission("AutoSmelt.Command"))?Util.easyItem(ChatColor.GREEN + "Smelt Your Items", Material.COAL, 1, 0):locked;
        if (Config.extraInfo)
        {
            conts[16] =Util.easyItem(ChatColor.RED + "Suggestions/Found Bugs", Material.LAVA_BUCKET, 1, 0, ChatColor.GRAY + "Contact " + ChatColor.GOLD + "MnMaxon" + ChatColor.GRAY + " on ", ChatColor.GRAY + "Spigot or Bukkit", ChatColor.GRAY + "Or, by email:", ChatColor.GRAY + "masontg777@aol.com");
        }

        conts[17] = Util.easyItem(ChatColor.RED + "Close", Material.ARROW, 1, 0);

        for (int i = 0; i < conts.length; i++)
        {
            if (conts[i] == null)conts[i] = empty;
        }

        //TODO: wtf?
        if (p.getInventory() != null
            && p.getInventory().getName() != null
            && p.getInventory().getName().equals(ChatColor.BLUE + "AutoPickup"))
        {
            p.getInventory().setContents(conts);
            p.updateInventory();
        }else
        {
            newInv.setContents(conts);
            p.openInventory(newInv);
        }
    }
}
