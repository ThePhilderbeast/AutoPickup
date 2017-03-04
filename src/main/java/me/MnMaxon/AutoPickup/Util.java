package me.MnMaxon.AutoPickup;

import haveric.stackableItems.util.InventoryUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static me.MnMaxon.AutoPickup.AutoPickupPlugin.fullNotify;

public class Util
{

    public static void warn(Player p)
    {
        if (p != null && p.isValid() && fullNotify.contains(p.getName())) {
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, SoundCategory.BLOCKS, 1.0f, 1.0f);
            p.sendTitle(Message.ERROR0FULL_INVENTORY.toString(), ChatColor.GOLD + "/fullnotify to disable", 1, 15, 5);
        }
    }

    public static HashMap < Integer, ItemStack > giveItem(Player p, Inventory inv, ItemStack is)
    {
        if (is == null)
        {
            return new HashMap <> ();
        }

        if (!Config.usingStackableItems || p == null)
        {
            HashMap<Integer, ItemStack> remaining = inv.addItem(is);

            if (p != null && remaining.size() > 0 && fullNotify.contains(p.getName()))
            {
                warn(p);
            }

            return inv.addItem(is);
        }

        ItemStack toSend = is.clone();
        ItemStack remaining = null;
        int freeSpaces = InventoryUtil.getPlayerFreeSpaces(p, toSend);
        if (freeSpaces < toSend.getAmount())
        {
            remaining = toSend.clone();
            remaining.setAmount(toSend.getAmount() - freeSpaces);
            toSend.setAmount(freeSpaces);
        }

        if (toSend.getAmount() > 0)
        {
            InventoryUtil.addItemsToPlayer(p, toSend, "pickup");
        }
        HashMap < Integer, ItemStack > map = new HashMap <> ();
        if (remaining != null)
        {
            map.put(0, remaining);
        }
        return map;
    }

    public static HashMap < Integer, ItemStack > giveItem(Player p, ItemStack is)
    {
        return giveItem(p, p.getInventory(), is);
    }

    public static ItemStack easyItem(String name, Material material, int amount, int durability, String... lore)
    {
        ItemStack is = new ItemStack(material);
        if (durability > 0)
        {
            is.setDurability((short)durability);
        }

        if (amount > 1)
        {
            is.setAmount(amount);
        }

        if (is.getItemMeta() != null)
        {
            ItemMeta im = is.getItemMeta();
            if (name != null)
            {
                im.setDisplayName(name);
            }

            if (lore != null)
            {
                ArrayList < String > loreList = new ArrayList <> ();
                Collections.addAll(loreList, lore);
                im.setLore(loreList);
            }
            is.setItemMeta(im);
        }
        return is;
    }

}
