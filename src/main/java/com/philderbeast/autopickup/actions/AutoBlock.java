package com.philderbeast.autopickup.actions;

import com.philderbeast.autopickup.util.Message;
import com.philderbeast.autopickup.util.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by MnMaxon on 5/26/2015. */
@SuppressWarnings("ALL")
public class AutoBlock
{
    public static final HashMap < Material, Material > convertTo = new HashMap <> ();
    public static final HashMap < Material, Integer > convertNum = new HashMap <> ();
    private static final HashMap < Material, Short > convertDurability = new HashMap <> ();

    public static HashMap < Integer, ItemStack > addItem(Player p, ItemStack is)
    {

        if (is == null)
        {
            return new HashMap <> ();
        }

        HashMap < Integer, ItemStack > remaining = Util.giveItem(p, is);

        //its not an item to turn in to blocks
        if ( ! convertTo.containsKey(is.getType()))
        {
            //return items that cant be put in the players inventory
            return remaining;
        }

        //if there is only 1 item of the stack left return it?
        if (remaining.size() == 1 && remaining.values().toArray()[0].equals(is))
        {
            return remaining;
        }

        Inventory pInv = p.getInventory();
        ItemStack[] newCont = block(p, pInv.getStorageContents());

        //if we made any blocks
        if (newCont != null)
        {
            pInv.setStorageContents(newCont);
            p.updateInventory();
        }
        return remaining;
    }


    public static void block(Player p, boolean notify)
    {
        ItemStack[] newConts = block(p, p.getInventory().getStorageContents());
        if (newConts == null)
        {
            if (notify)
            {
                p.sendMessage(Message.ERROR0BLOCKED_INVENTORY + "");
            }
        }else
        {
            p.getInventory().setStorageContents(newConts);
            p.updateInventory();
            if (notify)
            {
                p.sendMessage(Message.SUCCESS0BLOCKED_INVENTORY + "");
            }
        }
    }

    public static void block(Player p)
    {
        block(p, true);
    }

    private static ItemStack[] block(Player p, ItemStack[] conts)
    {

        boolean totalChanged = false;

        for (ItemStack is:conts)
        {
            if (is != null && convertTo.containsKey(is.getType()))
            {
                Material type = is.getType();
                Material convertTo = AutoBlock.convertTo.get(type);
                int num = 0;
                int required = convertNum.get(type);
                int space = 0;

                //look through our inventory for items of this type and count how many we have
                for (ItemStack numIS:conts)
                {
                    if (numIS != null
                        && numIS.getType() == type
                        && ( ! numIS.hasItemMeta() ||  ! numIS.getItemMeta().hasDisplayName())
                        && ( ! convertDurability.containsKey(type) || numIS.getDurability() == convertDurability.get(type)))
                    {
                        num += numIS.getAmount();
                    }

                    if (numIS == null)
                    {
                        space += convertTo.getMaxStackSize();
                    }else if (numIS.getType().equals(convertTo))
                    {
                        space += convertTo.getMaxStackSize() - numIS.getAmount();
                    }

                }

                if (num < required || space == 0)
                {
                    //we can't make a block
                    continue;
                }

                totalChanged = true;

                int toMake = num / required;
                int tobeUsed = toMake * required;

                //leave one block worth of items in your inventory
                if (tobeUsed == num)
                {
                    //we have an exact number of blocks so make one less
                    toMake--;
                    tobeUsed -= required;
                }

                //make sure we dont make more then we can hold
                if(space < toMake)
                {
                    tobeUsed = space * required;
                    toMake = space;
                }

                //go backward through the inventory
                for (int i = conts.length - 1; i >= 0; i--)
                {
                    if (conts[i] != null
                        && conts[i].getType() == type
                        && (!conts[i].hasItemMeta() || !conts[i].getItemMeta().hasDisplayName())
                        && (!convertDurability.containsKey(type) || conts[i].getDurability() == convertDurability.get(type)))
                    {
                        //remove the items we are putting into blocks
                        if (conts[i].getAmount() > tobeUsed)
                        {
                            //this stack has enough
                            conts[i].setAmount(conts[i].getAmount() - tobeUsed);
                            break;
                        } else
                        {
                            //we need more then this stack has so take it all and remove it from the player
                            tobeUsed -= conts[i].getAmount();
                            conts[i] = null;
                        }
                    }
                }

                p.getInventory().setStorageContents(conts);
                //create an inventory to hold our items
                //set the number of items to the max stack size of the item

                //make a stack of the blocks
                ItemStack toAdd = new ItemStack(convertTo);
                toAdd.setAmount(type.getMaxStackSize());

                while (toMake > convertTo.getMaxStackSize())
                {
                    p.getInventory().addItem(toAdd);
                    toMake -= type.getMaxStackSize();
                }

                toAdd.setAmount(toMake);
                p.getInventory().addItem(toAdd);
                p.updateInventory();
                conts = p.getInventory().getStorageContents();
            }
        }

        if (totalChanged)
        {
            return conts;
        }
        return null;
    }

    //conversion data
    static {
        convertTo.put(Material.CLAY_BALL, Material.CLAY);
        convertNum.put(Material.CLAY_BALL, 4);

        convertTo.put(Material.IRON_INGOT, Material.IRON_BLOCK);
        convertNum.put(Material.IRON_INGOT, 9);

        convertTo.put(Material.REDSTONE, Material.REDSTONE_BLOCK);
        convertNum.put(Material.REDSTONE, 9);

        convertTo.put(Material.DIAMOND, Material.DIAMOND_BLOCK);
        convertNum.put(Material.DIAMOND, 9);

        convertTo.put(Material.INK_SAC, Material.LAPIS_BLOCK);
        convertNum.put(Material.INK_SAC, 9);
        convertDurability.put(Material.INK_SAC, (short) 4);

        convertTo.put(Material.COAL, Material.COAL_BLOCK);
        convertNum.put(Material.COAL, 9);
        convertDurability.put(Material.COAL, (short) 0);

        convertTo.put(Material.EMERALD, Material.EMERALD_BLOCK);
        convertNum.put(Material.EMERALD, 9);

        convertTo.put(Material.GOLD_INGOT, Material.GOLD_BLOCK);
        convertNum.put(Material.GOLD_INGOT, 9);
    }
}
