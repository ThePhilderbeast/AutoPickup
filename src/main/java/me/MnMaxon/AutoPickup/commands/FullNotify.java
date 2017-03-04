package me.MnMaxon.AutoPickup.commands;

import me.MnMaxon.AutoPickup.AutoPickupPlugin;
import me.MnMaxon.AutoPickup.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FullNotify implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(cmd.getName().equals("FullNotify"))
        {
            Player p = (Player)sender;
            if (!p.hasPermission("FullNotify.command"))
            {
                p.sendMessage(Message.ERROR0NO_PERM + "");
            } else if (args.length == 0)
            {
                if (!p.hasPermission("FullNotify.toggle"))
                {
                    p.sendMessage(Message.ERROR0NO_PERM + "");
                } else if (AutoPickupPlugin.fullNotify.contains(p.getName()))
                {
                    AutoPickupPlugin.fullNotify.remove(p.getName());
                    p.sendMessage(Message.SUCCESS0TOGGLE0NOTIFY_OFF + "");
                } else
                {
                    AutoPickupPlugin.fullNotify.add(p.getName());
                    p.sendMessage(Message.SUCCESS0TOGGLE0NOTIFY_ON + "");
                }
            } else if (args.length > 0)
            {
                Common.displayHelp(p);
            }
        }
        return false;
    }

}
