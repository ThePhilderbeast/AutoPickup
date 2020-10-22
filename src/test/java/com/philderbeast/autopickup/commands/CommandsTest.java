package com.philderbeast.autopickup.commands;

import org.bukkit.permissions.PermissionAttachment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMockFactory;

import com.philderbeast.autopickup.AutoPickupPlugin;

public class CommandsTest {

    private ServerMock server;
    private AutoPickupPlugin plugin;
    private PlayerMock player;
    
    @Before
    public void setUp()
    {
        server = MockBukkit.mock();
        plugin = (AutoPickupPlugin) MockBukkit.load(AutoPickupPlugin.class);

        PlayerMockFactory factory = new PlayerMockFactory(server);
        player = factory.createRandomPlayer();
        PermissionAttachment pa = player.addAttachment(plugin);
        pa.setPermission("autopickup.enabled", true);
        pa.setPermission("autoblock.enabled", true);
        pa.setPermission("autosmelt.enabled", true);
        pa.setPermission("fullnotify.enabled", true);
        server.addPlayer(player);
    }

    @After
    public void tearDown()
    {
        MockBukkit.unmock();
    }

    @Test
    public void pickupToggle()
    {
        PermissionAttachment pa = player.addAttachment(plugin);
        pa.setPermission("AutoPickup.enabled", true);
    }

}