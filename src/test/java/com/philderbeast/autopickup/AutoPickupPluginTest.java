package com.philderbeast.autopickup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMockFactory;

public class AutoPickupPluginTest {

	private ServerMock server;
    private AutoPickupPlugin plugin;
    
    @Before
    public void setUp()
    {
        server = MockBukkit.mock();
        PlayerMockFactory factory = new PlayerMockFactory(server);
        PlayerMock player = factory.createRandomPlayer();
        server.addPlayer(player);
        plugin = (AutoPickupPlugin) MockBukkit.load(AutoPickupPlugin.class);
    }

    @After
    public void tearDown()
    {
        MockBukkit.unmock();
        
    }

    @Test
    public void playerJoin()
    {
        PlayerMockFactory factory = new PlayerMockFactory(server);
		PlayerMock player = factory.createRandomPlayer();
        server.addPlayer(player);
        server.getPluginManager().assertEventFired(PlayerJoinEvent.class, event -> event.getPlayer().equals(player)); 

        // no permissions so they arnt added
        Assert.assertFalse(AutoPickupPlugin.autoBlock.contains(player.getName()));
        Assert.assertFalse(AutoPickupPlugin.autoPickup.contains(player.getName()));
        Assert.assertFalse(AutoPickupPlugin.autoSmelt.contains(player.getName()));
        Assert.assertFalse(AutoPickupPlugin.fullNotify.contains(player.getName()));

    }

    @Test
    public void playerWithPermsJoin()
    {
        PlayerMockFactory factory = new PlayerMockFactory(server);
        PlayerMock player = factory.createRandomPlayer();
        PermissionAttachment pa = player.addAttachment(plugin);
        pa.setPermission("autopickup.enabled", true);
        pa.setPermission("autoblock.enabled", true);
        pa.setPermission("autosmelt.enabled", true);
        pa.setPermission("fullnotify.enabled", true);
        server.addPlayer(player);
        server.getPluginManager().assertEventFired(PlayerJoinEvent.class, event -> event.getPlayer().equals(player)); 

        // permissions so they should be in everything
        Assert.assertTrue(AutoPickupPlugin.autoBlock.contains(player.getName()));
        Assert.assertTrue(AutoPickupPlugin.autoPickup.contains(player.getName()));
        Assert.assertTrue(AutoPickupPlugin.autoSmelt.contains(player.getName()));
        Assert.assertTrue(AutoPickupPlugin.fullNotify.contains(player.getName()));

    }

}