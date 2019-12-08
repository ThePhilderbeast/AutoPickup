package com.philderbeast.autopickup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.function.Predicate;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.plugin.PluginManagerMock;
import be.seeseemelk.mockbukkit.MockBukkit;

public class AutoPickupPluginTest {

	private ServerMock server;
    private AutoPickupPlugin plugin;
	private PluginManagerMock pluginManager;

    @Before
    public void setUp()
    {
        server = MockBukkit.mock();
        pluginManager = server.getPluginManager();
        plugin = (AutoPickupPlugin) MockBukkit.load((Class<AutoPickupPlugin>) AutoPickupPlugin.class);
    }

    @After
    public void tearDown()
    {
        MockBukkit.unload();
    }

    @Test
    public void dummyTest()
    {
        assertTrue(true);
    }

    @Test
    public void playerJoin()
    {
        PlayerMock player = server.addPlayer();

        // this failes due to an error int he mocking library
        // pluginManager.assertEventFired(event -> { return event instanceof PlayerJoinEvent; });

        // //no permissions so they arnt added
        // assertFalse(AutoPickupPlugin.autoBlock.contains(player.getName()));
        // assertFalse(AutoPickupPlugin.autoPickup.contains(player.getName()));
        // assertFalse(AutoPickupPlugin.autoSmelt.contains(player.getName()));

        // server.execute("autopickup", player);
        // assertTrue(AutoPickupPlugin.autoPickup.contains(player.getName()));
    }


}