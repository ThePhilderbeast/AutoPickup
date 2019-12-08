package com.philderbeast.autopickup.listners;

import com.philderbeast.autopickup.actions.AutoBlock;
import com.philderbeast.autopickup.AutoPickupPlugin;
import com.philderbeast.autopickup.actions.AutoSmelt;
import com.philderbeast.autopickup.Config;
import com.philderbeast.autopickup.actions.LocationActions;
import com.philderbeast.autopickup.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.Damageable;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.philderbeast.autopickup.commands.Common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("ALL")
public class MainListener implements Listener
{

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e)
    {
        if (Config.fortuneData != null && Config.fortuneList.contains(e.getBlock().getType()))
        {
            String worldId = e.getBlock().getWorld().getUID().toString();
            List < String > list = Config.fortuneData.getStringList(worldId);
            String vecString = e.getBlock().getLocation().toVector().toString();
            if ( ! list.contains(vecString))
            {
                list.add(vecString);
                Config.fortuneData.set(worldId, list);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void giveBreakXP(BlockBreakEvent e)
    {
        if (Config.autoBlockXp &&  ! Config.getBlockedWorlds().contains(e.getBlock().getWorld()))
        {
            int exp = e.getExpToDrop();

            Player p = e.getPlayer();

            //do mending
            applyMending(p.getInventory().getItemInMainHand(), exp);
            applyMending(p.getInventory().getItemInOffHand(), exp);

            final ItemStack armor[] = p.getInventory().getArmorContents();
            for (ItemStack i : armor)
            {
                applyMending(i, exp);
            }
            e.getPlayer().giveExp(exp);
            e.setExpToDrop(0);
        }
    }

    private int applyMending(ItemStack i, int exp)
    {
        if (i.getEnchantments().containsKey(Enchantment.MENDING))
        {
            ItemMeta im = i.getItemMeta();
            if (im instanceof Damageable) {
                Damageable d = (Damageable)im;
                int min = Math.min(exp, d.getDamage());
                d.setDamage(d.getDamage() - min);
                exp -= min;
            }
        }
        return exp;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(PlayerInteractEvent e)
    {
        try
        {
            if (Config.usingAutoSell
                && e.getAction().name().toLowerCase().contains("right")
                && e.getPlayer().isSneaking()
                && e.getItem().getType().name().toLowerCase().contains("pickaxe"))
            {
                Bukkit.dispatchCommand(e.getPlayer(), "sellall");
            }
        } catch (Exception ignored)
        {
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e)
    {
        try
        {
            e.getCurrentItem().getData().getItemType();
            if (e.getView().getTitle().equals(ChatColor.BLUE + "AutoPickup Settings"))
            {
                e.setCancelled(true);
                Player p = (Player)e.getWhoClicked();
                String name = e.getCurrentItem().getItemMeta().getDisplayName();
                if (name.contains("AutoPickup"))
                {
                    if (p.hasPermission("AutoPickup.Toggle"))
                    {
                        if (AutoPickupPlugin.autoPickup.contains(p.getName()))
                        {
                            AutoPickupPlugin.autoPickup.remove(p.getName());
                        } else
                        {
                            AutoPickupPlugin.autoPickup.add(p.getName());
                        }
                        Common.openGui(p);
                    }
                } else if (name.contains("AutoSmelt"))
                {
                    if (p.hasPermission("AutoSmelt.Toggle"))
                    {
                        if (AutoPickupPlugin.autoSmelt.contains(p.getName()))
                        {
                            AutoPickupPlugin.autoSmelt.remove(p.getName());
                        } else
                        {
                            AutoPickupPlugin.autoSmelt.add(p.getName());
                        }
                        Common.openGui(p);
                    }
                } else if (name.contains("AutoBlock"))
                {
                    if (p.hasPermission("AutoBlock.Toggle"))
                    {
                        if (AutoPickupPlugin.autoBlock.contains(p.getName()))
                        {
                            AutoPickupPlugin.autoBlock.remove(p.getName());
                        } else
                        {
                            AutoPickupPlugin.autoBlock.add(p.getName());
                        }
                        Common.openGui(p);
                    }
                } else if (name.contains("FullNotify"))
                {
                    if (p.hasPermission("FullNotify.Toggle"))
                    {
                        if (AutoPickupPlugin.fullNotify.contains(p.getName()))
                        {
                            AutoPickupPlugin.fullNotify.remove(p.getName());
                        } else
                        {
                            AutoPickupPlugin.fullNotify.add(p.getName());
                        }
                        Common.openGui(p);
                    }
                } else if ( ! name.contains("auto"))
                {
                    if (name.contains("Close"))
                    {
                        p.closeInventory();
                    } else if (name.contains("Smelt"))
                    {
                        if(p.hasPermission("AutoSmelt.command"))
                        {
                            AutoSmelt.smelt(p);
                        }
                    } else if (name.contains("Block"))
                    {
                        if(p.hasPermission("AutoBlock.command"))
                        {
                            AutoBlock.block(p);
                        }
                    }
                }
            }
        } catch (NullPointerException | ClassCastException ignored)
        {
            //TODO: do something here
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e)
    {
        if (e.getPlayer().hasPermission("AutoPickup.enabled"))
        {
            AutoPickupPlugin.autoPickup.add(e.getPlayer().getName());
        }
        if (e.getPlayer().hasPermission("AutoBlock.enabled"))
        {
            AutoPickupPlugin.autoBlock.add(e.getPlayer().getName());
        }
        if (e.getPlayer().hasPermission("AutoSmelt.enabled"))
        {
            AutoPickupPlugin.autoSmelt.add(e.getPlayer().getName());
        }
        if (e.getPlayer().hasPermission("FullNotify.enabled"))
        {
            AutoPickupPlugin.fullNotify.add(e.getPlayer().getName());
        }
        fixPicks(e.getPlayer());
    }

    private static boolean fixPick(ItemStack is)
    {
        try
        {
            if (Config.usingAutoSell && is.getType().name().toLowerCase().contains("pickaxe"))
            {
                ItemMeta im = is.getItemMeta();
                List < String > lore = im.getLore();
                if ( ! lore.get(0).equals(ChatColor.MAGIC + "DATA"))
                {
                    return false;
                }
                String name = lore.get(1);
                lore.remove(0);
                lore.remove(0);
                if (name.equals("null"))
                {
                    name = null;
                }
                im.setDisplayName(name);
                im.setLore(lore);
                is.setItemMeta(im);
                return true;
            }
        }catch (Exception ignored)
        {
        }
        return false;
    }

    private void fixPicks(Player p)
    {
        if ( ! Config.usingAutoSell)
        {
            return;
        }
        boolean update = false;
        for (ItemStack is:p.getInventory())
        {
            if (fixPick(is))
            {
                update = true;
            }
        }
        if (update)
        {
            p.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent e)
    {
        //TODO: do we want to remove them from the arrays?
        AutoPickupPlugin.autoPickup.remove(e.getPlayer().getName());
        AutoPickupPlugin.autoBlock.remove(e.getPlayer().getName());
        AutoPickupPlugin.autoSmelt.remove(e.getPlayer().getName());
        AutoPickupPlugin.fullNotify.remove(e.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShift(PlayerToggleSneakEvent e)
    {
        if (Config.usingAutoSell)
        {
            if (e.isSneaking())
            {
                try
                {
                    ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
                    ItemMeta im = is.getItemMeta();
                    if ( ! is.getType().name().toLowerCase().contains("pickaxe"))
                    {
                        return;
                    }
                    String name = im.getDisplayName();
                    if (name == null)
                    {
                        name = "null";
                    }
                    ArrayList < String > lore = new ArrayList <> ();
                    List < String > oldLore = im.getLore();
                    if (oldLore != null && oldLore.size() != 0 && oldLore.get(0).equals(ChatColor.MAGIC + "DATA"))
                    {
                        return;
                    }
                    lore.add(ChatColor.MAGIC + "DATA");
                    lore.add(name);
                    if (oldLore != null)
                    {
                        lore.addAll(oldLore);
                    }

                    im.setLore(lore);
                    im.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Shift Right Click to Sell Your Items");
                    is.setItemMeta(im);
                    e.getPlayer().updateInventory();
                } catch (NullPointerException ignored)
                {
                }
            }else
            {
                fixPicks(e.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent e)
    {
        fixPick(e.getEntity().getItemStack());
        if ( ! Config.getBlockedWorlds().contains(e.getEntity().getWorld())
            && LocationActions.doAutoActions(e.getEntity(), e.getLocation()))
        {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onKill(EntityDeathEvent e)
    {
        Player killer = e.getEntity().getKiller();
        if (killer == null || e.getEntity()instanceof Player || Config.getBlockedWorlds().contains(killer.getWorld()))
        {
            return;
        }

        if (Config.autoMob)
        {
            ArrayList < ItemStack > newDrops = new ArrayList <> ();
            for (ItemStack drop:e.getDrops())
            {
                HashMap < Integer, ItemStack > remaining = killer.getInventory().addItem(drop);
                newDrops.addAll(remaining.values());
            }
            if ( ! newDrops.isEmpty())
            {
                Util.warn(killer);
            }

            e.getDrops().clear();
            if ( ! Config.deleteOnFull)
            {
                for (ItemStack is:newDrops)
                {
                    e.getDrops().add(is);
                }
            }
        }

        if (Config.autoMobXP)
        {
            killer.giveExp(e.getDroppedExp());
            e.setDroppedExp(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFish(PlayerFishEvent e)
    {
        if ( ! Config.getBlockedWorlds().contains(e.getPlayer().getWorld())
            && Config.autoMob
            && e.getCaught() != null
            && e.getCaught() instanceof Item)
        {
            Item item = (Item)e.getCaught();
            Collection < ItemStack > newDrops = e.getPlayer().getInventory().addItem(item.getItemStack()).values();

            if ( ! newDrops.isEmpty())
            {
                Util.warn(e.getPlayer());
            }

            if (Config.deleteOnFull || newDrops.isEmpty())
            {
                item.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent e)
    {
        if ( ! Config.autoChest && e.getBlock().getType().name().contains("CHEST"))
        {
            return;
        }

        ItemStack inhand = e.getPlayer().getInventory().getItemInMainHand();
        if (Config.fortuneData != null)
        {
            String worldId = e.getBlock().getWorld().getUID().toString();
            List < String > list = Config.fortuneData.getStringList(worldId);
            String vecString = e.getBlock().getLocation().toVector().toString();
            if (list.contains(vecString))
            {
                inhand = null;
                list.remove(vecString);
                Config.fortuneData.set(worldId, list);
            }
        }
        if (Config.getBlockedWorlds().contains(e.getPlayer().getWorld()))
        {
            return;
        }
        // String name = e.getPlayer().getName();

        LocationActions.add(e.getBlock().getLocation(), e.getPlayer(), inhand);

        if (Config.infinityPick
            && e.getPlayer().hasPermission("AutoPickup.infinity")
            && e.getPlayer().getInventory().getItemInMainHand() != null
            && e.getPlayer().getInventory().getItemInMainHand().getType().name().contains("PICKAXE"))
        {
            e.getPlayer().getInventory().getItemInMainHand().setDurability((short)1);
            e.getPlayer().updateInventory();
        }
    }
}
