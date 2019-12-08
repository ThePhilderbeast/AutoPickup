package com.philderbeast.autopickup;

import com.philderbeast.autopickup.commands.AutoBlockCommand;
import com.philderbeast.autopickup.commands.AutoPickup;
import com.philderbeast.autopickup.commands.AutoSmeltCommand;
import com.philderbeast.autopickup.commands.FullNotify;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;

import com.philderbeast.autopickup.listners.MainListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.PluginDescriptionFile;

public final class AutoPickupPlugin extends JavaPlugin
{

    //TODO: move these
    public static final List < String > autoSmelt = new ArrayList<>();
    public static final List < String > autoPickup = new ArrayList<>();
    public static final List < String > autoBlock = new ArrayList<>();
    public static final List < String > fullNotify = new ArrayList<>();
    public static final HashMap < String, Long > warnCooldown = new HashMap<>();
    
    public AutoPickupPlugin()
    {
        super();
    }

    protected AutoPickupPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
    {
        super(loader, description, dataFolder, file);
    }

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

        getServer().getPluginManager().registerEvents(new MainListener(), this);

        this.getCommand("autopickup").setExecutor(new AutoPickup());
        this.getCommand("autosmelt").setExecutor(new AutoSmeltCommand());
        this.getCommand("autoblock").setExecutor(new AutoBlockCommand());
        this.getCommand("fullnotify").setExecutor(new FullNotify());

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
        }
    }
}