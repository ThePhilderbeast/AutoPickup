package com.philderbeast.autopickup;

import com.philderbeast.autopickup.actions.AutoBlock;
import com.philderbeast.autopickup.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Config
{

	private static String configFolder;

	public static boolean infinityPick = false;
	public static boolean deleteOnFull = true;
	public static boolean warnOnFull = false;
	public static boolean autoBlockXp = true;
	public static boolean autoMob = true;
	public static boolean autoMobXP = true;
	public static boolean extraInfo = false;
    public static boolean usingQuickSell = false;
	public static boolean smeltFortune = false;
    public static boolean usingAutoSell = false;
	public static boolean usingStackableItems = false;
	public static boolean usingPrisonGems = false;
	public static boolean allowBlockGui;
    public static boolean autoChest;

    public static YamlConfiguration messageConfig = null;
    public static YamlConfiguration fortuneData = null;

    private static YamlConfiguration mainConfig = null;
	private static YamlConfiguration smeltConfig = null;
	private static YamlConfiguration worldConfig = null;
	private static YamlConfiguration fortuneConfig = null;


	public static List < String > smeltList = new ArrayList<>();
	public static final List < Material > fortuneList = new ArrayList <> ();
	public static final HashMap < Material, Short > smeltBlacklist = new HashMap <> ();
    private static final List < String > blockedWorlds = new ArrayList <> ();

    public static void setConfigFolder(String configFolder)
    {
        Config.configFolder = configFolder;
    }

    public static void saveAll()
    {
		try
        {
            mainConfig.save(configFolder + "/Config.yml");
            messageConfig.save(configFolder + "/Messages.yml");
            smeltConfig.save(configFolder + "/Smelt Blacklist.yml");
            worldConfig.save(configFolder + "/World Blacklist.yml");
            fortuneConfig.save(configFolder + "/Advanced Fortune.yml");
		}catch (IOException e)
        {
			e.printStackTrace();
		}
	}

	public static void reloadConfigs()
	{
		mainConfig = new YamlConfiguration();
        messageConfig = new YamlConfiguration();
        smeltConfig = new YamlConfiguration();
        worldConfig = new YamlConfiguration();
        fortuneConfig = new YamlConfiguration();

        defaultConfigs();

        try{
            mainConfig.load(configFolder + "/Config.yml");
            messageConfig.load(configFolder + "/Messages.yml");
            smeltConfig.load(configFolder + "/Smelt Blacklist.yml");
            worldConfig.load(configFolder + "/World Blacklist.yml");
            fortuneConfig.load(configFolder + "/Advanced Fortune.yml");
        } catch (InvalidConfigurationException | IOException ignored)
        {
            Bukkit.getLogger().severe("Failed to load all configs, using defaults");
        }

        if (fortuneData != null)
		{
            try {
			    fortuneData.save(configFolder + "/Fortune Data");
            } catch (IOException ignored)
            {
            }
		}
        fortuneData = null;
        Message.setup();


		if (mainConfig.getBoolean("AutoBlock Quartz"))
        {
            AutoBlock.convertTo.put(Material.QUARTZ, Material.QUARTZ_BLOCK);
            AutoBlock.convertNum.put(Material.QUARTZ, 4);
        }else
        {
            AutoBlock.convertTo.remove(Material.QUARTZ);
            AutoBlock.convertNum.remove(Material.QUARTZ);
        }

        if (mainConfig.getBoolean("AutoSmelt Compat Mode"))
        {
            smeltList.clear();
        }else
        {
            //add the list of items we smelt
			//TODO: move this somewhere better
            smeltList = Arrays.asList("SPONGE",
                                      "LOG",
                                      "LOG_2",
                                      "CACTUS",
                                      "SAND",
                                      "COBBLESTONE",
                                      "SMOOTH_BRICK",
                                      "CLAY",
                                      "CLAY_BALL",
                                      "NETHERRACK",
                                      "DIAMOND_ORE",
                                      "REDSTONE_ORE",
                                      "LAPIS_ORE",
                                      "IRON_ORE",
                                      "QUARTZ_ORE",
                                      "COAL_ORE",
                                      "GOLD_ORE",
                                      "EMERALD_ORE",
                                      "RAW_CHICKEN",
                                      "RAW_FISH",
                                      "MUTTON",
                                      "RAW_BEEF",
                                      "RABBIT",
                                      "POTATO_ITEM",
                                      "PORK");
        }

		smeltFortune = fortuneConfig.getBoolean("Smelt Fortune");
        fortuneList.clear();
        if (fortuneConfig.getBoolean("Fortune All"))
        {
            fortuneData = new YamlConfiguration();
            try
            {
                fortuneData.load(configFolder + "/Fortune Data");
            } catch (InvalidConfigurationException | IOException ignored)
            {
            }

            for (Object o:fortuneConfig.getList("Fortune All Whitelist"))
            {
                if (o != null)
                {
                    Material material = Material.matchMaterial(o.toString());
                    if (material == null)
                    {
                        Bukkit.getLogger().severe(o.toString() + "Is not a valid block name in: Advanced Fortune.yml");
                    } else
                    {
                        fortuneList.add(material);
                    }
                }
            }
        }

        extraInfo = mainConfig.getBoolean("Gui.Contact Info");

        blockedWorlds.clear();

        if (worldConfig.getBoolean("Enable Blacklist"))
        {
            for (Object raw:worldConfig.getList("Blacklist"))
            {
                if (raw instanceof String)blockedWorlds.add((String)raw);
                {
                    smeltBlacklist.clear();
                }
            }
        }

        if (smeltConfig.getBoolean("Enable Blacklist"))
        {
            for (Object raw:smeltConfig.getList("Blacklist"))
            {
                if ( ! (raw instanceof String))
                {
                    continue;
                }
                String[] split = ((String)raw).split(":");
                Material mat = Material.matchMaterial(split[0]);
                if (mat == null)
                {
                    Bukkit.getLogger().severe(ChatColor.RED + "[AutoPickup] The blacklist item: '" + split[0] + "' could not be found");
                    continue;
                }
                short data = -1;
                if (split.length > 1)
                {
                    try
                    {
                        data = Short.valueOf(split[1].replace(" ", ""));
                    }catch (NumberFormatException ex)
                    {
                        Bukkit.getLogger().severe(ChatColor.RED + "[AutoPickup] The blacklist item: '" + raw + "' does not have a valid data number");
                        data = -1;
                    }
                }
                smeltBlacklist.put(mat, data);
            }
        }

        infinityPick = mainConfig.getBoolean("Infinity Pick");
        deleteOnFull = mainConfig.getBoolean("Full Inventory.Delete Item");
        warnOnFull = mainConfig.getBoolean("Full Inventory.Warn");
        autoMob = mainConfig.getBoolean("Mob.AutoPickup");
        autoBlockXp = mainConfig.getBoolean("Block AutoXP");
        autoMobXP = mainConfig.getBoolean("Mob.AutoXP");
        autoChest = mainConfig.getBoolean("AutoChest");
        allowBlockGui = mainConfig.getBoolean("Allow BlockGui Permission");

        saveAll();

	}

	private static void defaultConfigs()
	{
		HashMap < String, Object > defaults = new HashMap<>();
		defaults.put("Infinity Pick", false);
        defaults.put("Gui.Contact Info", true);
        defaults.put("Full Inventory.Delete Item", true);
        defaults.put("Full Inventory.Warn", true);
        defaults.put("AutoSmelt Compat Mode", true);
        defaults.put("AutoBlock Quartz", true);
        defaults.put("Mob.AutoPickup", true);
        defaults.put("Mob.AutoXP", true);
        defaults.put("Block AutoXP", true);
        defaults.put("Allow BlockGui Permission", false);
        defaults.put("Auto Chest", true);

		for (Map.Entry < String, Object > entry:defaults.entrySet())
		{
			//if we dont have a config use the default
            if (mainConfig.get(entry.getKey()) == null)
            {
                mainConfig.set(entry.getKey(), entry.getValue());
            }
		}

		//default autosmelt to on
		if (smeltConfig.get("Enable Blacklist") == null)
		{
            smeltConfig.set("Enable Blacklist", true);
		}

		//smelting fortune default to just giving infor about it
		if (fortuneConfig.get("Info") == null)
        {
            fortuneConfig.set("Info", Arrays.asList("Smelt Fortune means if you have autosmelt on, when you mine something like an iron ore, fortune effects will work on it, meaning you would get more iron ingots if you had fortune",
                    "Fortune all allows to add make fortune work on anything.  For example, you could mine a gold ore with a fortune pick, and get few gold ores as the result.",
                    "To prevent ore duping, this plugin will need to keep a list of placed blocks.  This will require some more RAM and hard drive space (This should only require a few MB)",
                    "The Fortune All Whitelist allows you to determine which blocks are affected by fortune, so you don't get billions of stacks of cobble",
                    "NOTE: The Fortune All Whitelist does not replace the default vanilla fortune whitelist, it just adds to it"));
        }

		//Fortune everything defaults to off
		if (fortuneConfig.get("Fortune All") == null)
        {
            fortuneConfig.set("Fortune All", false);
        }

		//fortune all whitelist/
        if (fortuneConfig.get("Fortune All Whitelist") == null)
        {
            fortuneConfig.set("Fortune All Whitelist", Arrays.asList("GOLD_ORE",
																	 "IRON_ORE",
																	 "DIAMOND_ORE",
																	 "LAPIS_ORE",
																	 "QUARTZ_ORE",
																	 "MYCEL")); //why are we adding mycel?
        }

		//blacklist coal from being smelted by default
        if (smeltConfig.get("Blacklist") == null)
        {
            smeltConfig.set("Blacklist", Arrays.asList("Coal:1"));
        }

		//disable world blacklist
        if (worldConfig.get("Enable Blacklist") == null)
        {
            worldConfig.set("Enable Blacklist", false);
        }

		//examble blacklist worlds
        if (worldConfig.get("Blacklist") == null)
        {
            worldConfig.set("Blacklist", Arrays.asList("ExampleWorld", "2nd_Example"));
        }

	}

    public static List < World > getBlockedWorlds()
    {
        ArrayList < World > worlds = new ArrayList <> ();
        for (String s:blockedWorlds)
        {
            World w = Bukkit.getWorld(s);
            if (w != null)worlds.add(w);
        }
        return worlds;
    }
}
