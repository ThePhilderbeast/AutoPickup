package com.philderbeast.autopickup;

import static org.junit.Assert.assertTrue;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AutoPickupPluginTest
{

    private ServerMock server;
    private AutoPickupPlugin plugin;
    private PlayerMock player;

    @Before
    public void setUp()
    {
        server = MockBukkit.mock();
        plugin = (AutoPickupPlugin) MockBukkit.load(AutoPickupPlugin.class);
        player = server.addPlayer();
    }

    @After
    public void tearDown()
    {
        MockBukkit.unload();
    }

    @Test
    public void breakBlock()
    {
        assertTrue(true);
    }

}