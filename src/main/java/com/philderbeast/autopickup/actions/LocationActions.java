package com.philderbeast.autopickup.actions;

import com.philderbeast.autopickup.AutoPickupPlugin;
import com.philderbeast.autopickup.Config;
import com.philderbeast.autopickup.util.Util;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

//TODO: this class must Die!
public class LocationActions
{
    public static final HashMap < Location, LocationActions> locations = new HashMap <> ();
    private final Player p;
    private final ItemStack itemStack;

    private LocationActions(Player p, ItemStack is)
    {
        this.p = p;
        this.itemStack = is;
    }

    public static void add(Location loc, Player p, ItemStack is)
    {
        final Location location = loc.getBlock().getLocation();

        if (locations.containsKey(location))
        {
            locations.remove(location);
        }

        final LocationActions sl = new LocationActions(p, is);
        locations.put(location, sl);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("AutoPickup"), () -> {
            if (locations.containsKey(location) && locations.get(location).equals(sl))
            {
                locations.remove(location);
            }
        }, 10L);
    }

    public static boolean doAutoActions(Item item, Location exactLoc)
    {
        Location loc = exactLoc.getBlock().getLocation();

        if (item == null ||  ! locations.containsKey(loc))
        {
            return false;
        }

        LocationActions sl = locations.get(loc);

        if (sl == null ||  ! sl.p.isValid())
        {
            return false;
        }

        ItemStack is = item.getItemStack();

        if (Config.usingPrisonGems && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Gem"))
        {
            return false;
        }

        item = Util.doFortune(sl.p, item, sl.itemStack);

        if (AutoPickupPlugin.autoPickup.contains(sl.p.getName()))
        {
            AutoPickup.pickup(sl.p, item.getItemStack());
            return true;
        }
        return false;
    }
}
