package com.philderbeast.autopickup.API;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMockFactory;

import com.philderbeast.autopickup.AutoPickupPlugin;

public class AutoPickupMethodsTest {

    private ServerMock server;
    private AutoPickupPlugin plugin;
    private PlayerMock player;
    
    @Before
    public void setUp()
    {
        server = MockBukkit.mock();
        plugin = (AutoPickupPlugin) MockBukkit.load(AutoPickupPlugin.class);
        plugin.onEnable();

        PlayerMockFactory factory = new PlayerMockFactory(server);
		player = factory.createRandomPlayer();
        server.addPlayer(player);
    }

    @After
    public void tearDown()
    {
        MockBukkit.unmock();
        
    }

    @Test
    public void openGui()
    {
        AutoPickupMethods.openGui(player);
    }

    @Test
    @Deprecated
    public void smeltInventory()
    {
        AutoPickupMethods.smeltInventory(player);
    }
    
    @Test
    public void blockInventory()
    {
        AutoPickupMethods.blockInventory(player);
        AutoPickupMethods.blockInventory(player, true);
    }


}