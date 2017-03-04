package me.MnMaxon.AutoPickup;

import me.MnMaxon.AutoPickup.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class AutoPickupPlugin extends JavaPlugin
{

    //TODO: move these
    public static List < String > autoSmelt = new ArrayList<>();
    public static List < String > autoPickup = new ArrayList<>();
    public static List < String > autoBlock = new ArrayList<>();
    public static List < String > autoSell = new ArrayList<>();
    public static List < String > fullNotify = new ArrayList<>();

    @Override
    public void onDisable()
    {
        Config.saveAll();
    }

    @Override
    public void onEnable()
    {
        Config.setConfigFolder(this.getDataFolder().getAbsolutePath());
        Config.reloadConfigs();

        //TODO: this dosn't seem to be registering
        getServer().getPluginManager().registerEvents(new MainListener(), this);

        ArrayList < String > plugins = new ArrayList <> ();

        this.getCommand("AutoPickup").setExecutor(new AutoPickup());
        this.getCommand("AutoSmelt").setExecutor(new AutoSmeltCommand());
        this.getCommand("AutoBlock").setExecutor(new AutoBlockCommand());
        this.getCommand("AutoSell").setExecutor(new AutoSell());
        this.getCommand("FullNotify").setExecutor(new FullNotify());

        //Set up compatablility Listeners
        if (getServer().getPluginManager().getPlugin("MythicMobs") != null)
        {
            getServer().getPluginManager().registerEvents(new MythicMobListener(), this);
        }
        if (getServer().getPluginManager().getPlugin("TokenEnchant") != null)
        {
            getServer().getPluginManager().registerEvents(new TokenEnchantListener(), this);
        }
        if (getServer().getPluginManager().getPlugin("QuickSell") != null)
        {
            plugins.add("QuickSell");
            Config.usingQuickSell = true;
        }
        if (getServer().getPluginManager().getPlugin("StackableItems") != null)
        {
            plugins.add("StackableItems");
            Config.usingStackableItems = true;
        }
        if (getServer().getPluginManager().getPlugin("AutoSell") != null)
        {
            plugins.add("AutoSell");
            Config.usingAutoSell = true;
        }
        if (getServer().getPluginManager().getPlugin("MythicDrops") != null)
        {
            plugins.add("MythicDrops");
            getServer().getPluginManager().registerEvents(new MythicListener(), this);
        }
        if (getServer().getPluginManager().getPlugin("MyPet") != null)
        {
            plugins.add("MyPet");
            if ( ! plugins.contains("MythicDrops"))
            {
                getServer().getPluginManager().registerEvents(new MythicListener(), this);
            }
        }
        if (getServer().getPluginManager().getPlugin("FortuneBlocks") != null)
        {
            plugins.add("FortuneBlocks");
            Config.usingCompat = true;
        }
        if (getServer().getPluginManager().getPlugin("PrisonGems") != null)
        {
            plugins.add("PrisonGems");
            Config.usingPrisonGems = true;
        }
        if ( ! plugins.isEmpty())
        {
            String message = "[AutoPickup] Detected you are using ";
            for (String pName:plugins)
            {
                if ( ! message.endsWith(" "))
                {
                    message = message + ", ";
                }
                message = message + pName;
            }
            Bukkit.getLogger().info(message);
        }

        for (Player p:Bukkit.getOnlinePlayers())
        {
            if (p.hasPermission("AutoPickup.enabled"))
            {
                AutoPickupPlugin.autoPickup.add(p.getName());
            }
            if (p.hasPermission("AutoBlock.enabled"))
            {
                AutoPickupPlugin.autoBlock.add(p.getName());
            }
            if (p.hasPermission("AutoSmelt.enabled"))
            {
                AutoPickupPlugin.autoSmelt.add(p.getName());
            }
            if (p.hasPermission("AutoSell.enabled") && Config.usingQuickSell)
            {
                AutoPickupPlugin.autoSell.add(p.getName());
            }
            if (p.hasPermission("FullNotify.enabled"))
            {
                AutoPickupPlugin.fullNotify.add(p.getName());
            }
        }
    }
}
