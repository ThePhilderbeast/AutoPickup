package me.MnMaxon.AutoPickup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MainListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        if (AutoPickupPlugin.FortuneData != null && AutoPickupPlugin.fortuneList.contains(e.getBlock().getType())) {
            String worldId = e.getBlock().getWorld().getUID().toString();
            List<String> list = AutoPickupPlugin.FortuneData.getStringList(worldId);
            String vecString = e.getBlock().getLocation().toVector().toString();
            if (!list.contains(vecString)) {
                list.add(vecString);
                AutoPickupPlugin.FortuneData.set(worldId, list);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void giveBreakXP(BlockBreakEvent e) {
        if (AutoPickupPlugin.autoBlockXp && !AutoPickupPlugin.getBlockedWorlds().contains(e.getBlock().getWorld())) {
            e.getPlayer().giveExp(e.getExpToDrop());
            e.setExpToDrop(0);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(PlayerInteractEvent e) {
        try {
            if (AutoPickupPlugin.usingAutoSell && e.getAction().name().toLowerCase().contains("right") && e.getPlayer().isSneaking() && e.getItem().getType().name().toLowerCase().contains("pickaxe"))
                Bukkit.dispatchCommand(e.getPlayer(), "sellall");
        } catch (Exception ignored) {
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        try {
            e.getCurrentItem().getData().getItemType();
            if (e.getInventory().getName().equals(ChatColor.BLUE + "AutoPickup")) {
                e.setCancelled(true);
                Player p = (Player) e.getWhoClicked();
                String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase());
                if (name.contains("autopickup")) {
                    if (p.hasPermission("AutoPickup.Toggle")) {
                        if (AutoPickupPlugin.autoPickup.contains(p.getName()))
                            AutoPickupPlugin.autoPickup.remove(p.getName());
                        else AutoPickupPlugin.autoPickup.add(p.getName());
                        AutoPickupPlugin.openGui(p);
                    }
                } else if (name.contains("autosmelt")) {
                    if (p.hasPermission("AutoSmelt.Toggle")) {
                        if (AutoPickupPlugin.autoSmelt.contains(p.getName()))
                            AutoPickupPlugin.autoSmelt.remove(p.getName());
                        else AutoPickupPlugin.autoSmelt.add(p.getName());
                        AutoPickupPlugin.openGui(p);
                    }
                } else if (name.contains("autoblock")) {
                    if (p.hasPermission("AutoBlock.Toggle")) {
                        if (AutoPickupPlugin.autoBlock.contains(p.getName()))
                            AutoPickupPlugin.autoBlock.remove(p.getName());
                        else AutoPickupPlugin.autoBlock.add(p.getName());
                        AutoPickupPlugin.openGui(p);
                    }
                } else if (name.contains("autosell")) {
                    if (p.hasPermission("AutoSell.Toggle")) {
                        if (AutoPickupPlugin.autoSell.contains(p.getName()))
                            AutoPickupPlugin.autoSell.remove(p.getName());
                        else AutoPickupPlugin.autoSell.add(p.getName());
                        AutoPickupPlugin.openGui(p);
                    }
                } else if (!name.contains("auto"))
                    if (name.contains("close")) p.closeInventory();
                    else if (name.contains("smelt")) AutoSmelt.smelt(p);
                    else if (name.contains("block")) AutoBlock.block(p);
            }
        } catch (NullPointerException | ClassCastException ignored) {
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission("AutoPickup.enabled")) AutoPickupPlugin.autoPickup.add(e.getPlayer().getName());
        if (e.getPlayer().hasPermission("AutoBlock.enabled")) AutoPickupPlugin.autoBlock.add(e.getPlayer().getName());
        if (e.getPlayer().hasPermission("AutoSmelt.enabled")) AutoPickupPlugin.autoSmelt.add(e.getPlayer().getName());
        if (e.getPlayer().hasPermission("AutoSell.enabled") && AutoPickupPlugin.usingQuickSell)
            AutoPickupPlugin.autoSell.add(e.getPlayer().getName());
        fixPicks(e.getPlayer());
    }

    public static boolean fixPick(ItemStack is) {
        try {
            if (AutoPickupPlugin.usingAutoSell && is.getType().name().toLowerCase().contains("pickaxe")) {
                ItemMeta im = is.getItemMeta();
                List<String> lore = im.getLore();
                if (!lore.get(0).equals(ChatColor.MAGIC + "DATA")) return false;
                String name = lore.get(1);
                lore.remove(0);
                lore.remove(0);
                if (name.equals("null")) name = null;
                im.setDisplayName(name);
                im.setLore(lore);
                is.setItemMeta(im);
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private void fixPicks(Player p) {
        if (!AutoPickupPlugin.usingAutoSell) return;
        boolean update = false;
        for (ItemStack is : p.getInventory()) if (fixPick(is)) update = true;
        if (update) p.updateInventory();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent e) {
        AutoPickupPlugin.autoPickup.remove(e.getPlayer().getName());
        AutoPickupPlugin.autoBlock.remove(e.getPlayer().getName());
        AutoPickupPlugin.autoSmelt.remove(e.getPlayer().getName());
        AutoPickupPlugin.autoSell.remove(e.getPlayer().getName());
        AutoPickupPlugin.warnCooldown.remove(e.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShift(PlayerToggleSneakEvent e) {
        if (AutoPickupPlugin.usingAutoSell) if (e.isSneaking()) try {
            ItemStack is = e.getPlayer().getItemInHand();
            ItemMeta im = is.getItemMeta();
            if (!is.getType().name().toLowerCase().contains("pickaxe")) return;
            String name = im.getDisplayName();
            if (name == null) name = "null";
            ArrayList<String> lore = new ArrayList<>();
            List<String> oldLore = im.getLore();
            if (oldLore != null && oldLore.size() != 0 && oldLore.get(0).equals(ChatColor.MAGIC + "DATA")) return;
            lore.add(ChatColor.MAGIC + "DATA");
            lore.add(name);
            if (oldLore != null) lore.addAll(oldLore);
            im.setLore(lore);
            im.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Shift Right Click to Sell Your Items");
            is.setItemMeta(im);
            e.getPlayer().updateInventory();
        } catch (NullPointerException ignored) {
        }
        else fixPicks(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent e) {
        fixPick(e.getEntity().getItemStack());
        if (!AutoPickupPlugin.getBlockedWorlds().contains(e.getEntity().getWorld()) && SuperLoc.doStuff(e.getEntity(), e.getLocation()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onKill(EntityDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer == null || e.getEntity() instanceof Player || AutoPickupPlugin.getBlockedWorlds().contains(killer.getWorld()))
            return;
        if (AutoPickupPlugin.autoMob) {
            ArrayList<ItemStack> newDrops = new ArrayList<>();
            for (ItemStack drop : e.getDrops()) {
                HashMap<Integer, ItemStack> remaining = killer.getInventory().addItem(drop);
                for (ItemStack remainder : remaining.values()) newDrops.add(remainder);
            }
            if (!newDrops.isEmpty()) AutoPickupPlugin.warn(killer);
            e.getDrops().clear();
            if (!AutoPickupPlugin.deleteOnFull) for (ItemStack is : newDrops) e.getDrops().add(is);
        }
        if (AutoPickupPlugin.autoMobXP) {
            killer.giveExp(e.getDroppedExp());
            e.setDroppedExp(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFish(PlayerFishEvent e) {
        if (!AutoPickupPlugin.getBlockedWorlds().contains(e.getPlayer().getWorld()) && AutoPickupPlugin.autoMob && e.getCaught() != null && e.getCaught() instanceof Item) {
            Item item = (Item) e.getCaught();
            Collection<ItemStack> newDrops = e.getPlayer().getInventory().addItem(item.getItemStack()).values();
            if (!newDrops.isEmpty()) AutoPickupPlugin.warn(e.getPlayer());
            if (AutoPickupPlugin.deleteOnFull || newDrops.isEmpty()) item.remove();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (!AutoPickupPlugin.autoChest && e.getBlock().getType().name().contains("CHEST")) return;
        ItemStack inhand = e.getPlayer().getItemInHand();
        if (AutoPickupPlugin.FortuneData != null) {
            String worldId = e.getBlock().getWorld().getUID().toString();
            List<String> list = AutoPickupPlugin.FortuneData.getStringList(worldId);
            String vecString = e.getBlock().getLocation().toVector().toString();
            if (list.contains(vecString)) {
                inhand = null;
                list.remove(vecString);
                AutoPickupPlugin.FortuneData.set(worldId, list);
            }
        }
        if (AutoPickupPlugin.getBlockedWorlds().contains(e.getPlayer().getWorld())) return;
        String name = e.getPlayer().getName();
        
        Location aboveBlockLoc = e.getBlock().getLocation().add(0, 1, 0);
        Block aboveBlock = e.getPlayer().getWorld().getBlockAt(aboveBlockLoc);
        
        if(aboveBlock.getType() == Material.SUGAR_CANE_BLOCK ){
            SuperLoc.add(aboveBlock.getLocation(), e.getPlayer(), AutoPickupPlugin.autoPickup.contains(name), AutoPickupPlugin.autoSmelt.contains(name), AutoPickupPlugin.autoBlock.contains(name), inhand);
        }
        SuperLoc.add(e.getBlock().getLocation(), e.getPlayer(), AutoPickupPlugin.autoPickup.contains(name), AutoPickupPlugin.autoSmelt.contains(name), AutoPickupPlugin.autoBlock.contains(name), inhand);
        if (AutoPickupPlugin.infinityPick && e.getPlayer().hasPermission("AutoPickup.infinity") && e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType().name().contains("PICKAXE")) {
            e.getPlayer().getItemInHand().setDurability((short) 1);
            e.getPlayer().updateInventory();
        }
    }
}
