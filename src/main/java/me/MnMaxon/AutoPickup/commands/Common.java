package me.MnMaxon.AutoPickup.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.MnMaxon.AutoPickup.AutoPickupPlugin;
import me.MnMaxon.AutoPickup.Config;
import me.MnMaxon.AutoPickup.util.Util;

@SuppressWarnings("ALL")
public class Common
{

    public static void openGui(Player p)
    {
        if (Config.allowBlockGui && p.hasPermission("AutoPickup.BlockGui"))
        {
            p.sendMessage(ChatColor.RED + "You do not have permission to open the gui");
            return;
        }

        int size = 18;
        Inventory newInv = Bukkit.createInventory(null, size, ChatColor.BLUE + "AutoPickup Settings");

        //GUI layout
        // AP|AB|AS|FN|A$|  |  |AS|AB
        // TO|TO|TO|TO|TO|  |  |HE|SU

        ItemStack[] conts = newInv.getContents();
        conts[0] = Util.easyItem(ChatColor.GREEN + "AutoPickup",
                                 Material.HOPPER,
                                 1,
                                 0,
                                 ChatColor.GRAY + "Sends mined blocks",
                                 ChatColor.GRAY + "straight to your inventory");

        conts[1] = Util.easyItem(ChatColor.GREEN + "AutoBlock",
                                 Material.IRON_BLOCK,
                                 1,
                                 0,
                                 ChatColor.GRAY + "Turns ingots into blocks");

        conts[2] = Util.easyItem(ChatColor.GREEN + "AutoSmelt",
                                 Material.FURNACE,
                                 1,
                                 0,
                                 ChatColor.GRAY + "Smelts all mined ores");

        conts[3] = Util.easyItem(ChatColor.GREEN + "FullNotify",
                                 Material.CHEST,
                                 1,
                                 0,
                                 ChatColor.GRAY + "Notifys you when your inventory is full");

        if (Config.usingQuickSell)
        {
            conts[4] = Util.easyItem(ChatColor.GREEN + "AutoSell",
                                     Material.GOLD_INGOT,
                                     1,
                                     0,
                                     ChatColor.GRAY + "Sells any possible",
                                     ChatColor.GRAY + "mined blocks");
        }

        String autoPickupName;
        if (AutoPickupPlugin.autoPickup.contains(p.getName()))
        {
            autoPickupName = ChatColor.GREEN + "AutoPickup ENABLED";
        } else
        {
            autoPickupName =  ChatColor.RED + "AutoPickup DISABLED";
        }

        String autoBlockName;
        if (AutoPickupPlugin.autoBlock.contains(p.getName()))
        {
            autoBlockName = ChatColor.GREEN + "AutoBlock ENABLED";
        } else
        {
            autoBlockName = ChatColor.RED + "AutoBlock DISABLED";
        }

        String autoSmeltName;
        if (AutoPickupPlugin.autoSmelt.contains(p.getName()))
        {
            autoSmeltName = ChatColor.GREEN + "AutoSmelt ENABLED";
        } else
        {
            autoSmeltName = ChatColor.RED + "AutoSmelt DISABLED";
        }

        String fullNotifyName;
        if (AutoPickupPlugin.fullNotify.contains(p.getName()))
        {
            fullNotifyName = ChatColor.GREEN + "FullNotify ENABLED";
        } else
        {
            fullNotifyName = ChatColor.RED + "FullNotify DISABLED";
        }

        //set the durability of the INK_SAC, 10 = lime, 8 = gray
        int apDur = (AutoPickupPlugin.autoPickup.contains(p.getName()))  ? 10 : 8;
        int abDur = (AutoPickupPlugin.autoBlock.contains(p.getName()))   ? 10 : 8;
        int asDur = (AutoPickupPlugin.autoSmelt.contains(p.getName()))   ? 10 : 8;
        int fullNotifyDur = (AutoPickupPlugin.fullNotify.contains(p.getName())) ? 10 : 8;

        if (p.hasPermission("AutoPickup.Toggle"))
        {
            conts[9] = Util.easyItem(autoPickupName, Material.INK_SAC, 1, apDur, ChatColor.GRAY + "Click to Toggle");
        } else
        {
            conts[9] = Util.easyItem(autoPickupName, Material.INK_SAC, 1, apDur);
        }

        if (p.hasPermission("AutoBlock.Toggle"))
        {
            conts[10] = Util.easyItem(autoBlockName, Material.INK_SAC, 1, abDur, ChatColor.GRAY + "Click to Toggle");
        } else
        {
            conts[10] = Util.easyItem(autoBlockName, Material.INK_SAC, 1, abDur);
        }

        if (p.hasPermission("AutoSmelt.Toggle"))
        {
            conts[11] = Util.easyItem(autoSmeltName, Material.INK_SAC, 1, asDur, ChatColor.GRAY + "Click to Toggle");
        } else
        {
            conts[11] = Util.easyItem(autoSmeltName, Material.INK_SAC, 1, asDur);
        }

        if (p.hasPermission("Fullnotify.Toggle"))
        {
            conts[12] = Util.easyItem(fullNotifyName, Material.INK_SAC, 1, fullNotifyDur, ChatColor.GRAY + "Click to Toggle");
        } else
        {
            Util.easyItem(fullNotifyName, Material.INK_SAC, 1, fullNotifyDur);
        }

        ItemStack locked = Util.easyItem(ChatColor.RED + "LOCKED", Material.GLASS_PANE, 1, 14);
        ItemStack empty = Util.easyItem(null, Material.GLASS_PANE, 1, 7);

        //TODO: make these real if statements
        if (p.hasPermission("AutoBlock.Command"))
        {
            conts[7] = Util.easyItem(ChatColor.GREEN + "Block Your Items", Material.IRON_INGOT, 1, 0);
        } else
        {
            conts[7] = locked;
        }


        if (p.hasPermission("AutoSmelt.Command"))
        {
            conts[8] = Util.easyItem(ChatColor.GREEN + "Smelt Your Items", Material.COAL, 1, 0);
        } else
        {
            conts[8] = locked;
        }

        if (Config.extraInfo)
        {
            //might need to change this
            conts[16] = Util.easyItem(ChatColor.RED + "Suggestions/Found Bugs",
                                      Material.LAVA_BUCKET,
                                      1,
                                      0,
                                      ChatColor.GRAY + "Contact " + ChatColor.GOLD + "MnMaxon" + ChatColor.GRAY + " on ", ChatColor.GRAY + "Spigot or Bukkit", ChatColor.GRAY + "Or, by email:",
                                      ChatColor.GRAY + "masontg777@aol.com");
        }

        conts[17] = Util.easyItem(ChatColor.RED + "Close", Material.ARROW, 1, 0);

        for (int i = 0; i < conts.length; i++)
        {
            if (conts[i] == null)conts[i] = empty;
        }

        //TODO: wtf?
        if (p.getInventory() != null
            && p.getOpenInventory().getTitle() != null
            && p.getOpenInventory().getTitle().equals(ChatColor.BLUE + "AutoPickup"))
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