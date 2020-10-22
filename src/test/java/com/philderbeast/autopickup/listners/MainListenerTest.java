package com.philderbeast.autopickup.listners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMockFactory;

import com.philderbeast.autopickup.AutoPickupPlugin;

public class MainListenerTest {

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
    public void breackBlock()
    {
        player.setGameMode(GameMode.SURVIVAL);
        BlockMock block = server.addSimpleWorld("world").getBlockAt(0, 0, 0);
        block.setType(Material.STONE);
        Assert.assertTrue(player.simulateBlockBreak(block));
		server.getPluginManager().assertEventFired(BlockDamageEvent.class);
		server.getPluginManager().assertEventFired(BlockBreakEvent.class);
		block.assertType(Material.AIR);
    }

}