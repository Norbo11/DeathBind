package com.github.norbo11.deathbind;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PluginCommandExecutor implements CommandExecutor
{
    public PluginCommandExecutor(DeathBind p)
    {
        this.p = p;
    }
    DeathBind p;
    
    public boolean onCommand(CommandSender sender, Command command, String cmdlabel, String[] args)
    {
        String cmd = command.getName();
        if (cmd.equalsIgnoreCase("unbind"))
        {
            if (sender instanceof Player)
            {
                if (sender.hasPermission("deathbind.use"))
                {
                    Player player = (Player) sender;
                    if (args.length == 1) p.methods.unBind(player, args[0]);
                    else p.methodsError.displayHelp(sender);
                    return true;
                } else p.methodsError.noPermission(sender);
            } else p.methodsError.notAPlayer(sender);
        }
        if (cmd.equalsIgnoreCase("bind"))
        {
            if (args.length == 1)
            {
                if (args[0].equalsIgnoreCase("help"))
                {
                    if (sender.hasPermission("deathbind.use"))
                    {
                        p.methodsError.displayHelp(sender);
                        return true;
                    } else p.methodsError.noPermission(sender);
                }
                if (args[0].equalsIgnoreCase("deaths"))
                {
                    if (sender.hasPermission("deathbind.circumstances"))
                    {
                        p.methodsError.displayCircumstances(sender);
                        return true;
                    } else p.methodsError.noPermission(sender);
                }
                if (args[0].equalsIgnoreCase("list"))
                {
                    if (sender instanceof Player)
                    {
                        if (sender.hasPermission("deathbind.use"))
                        {
                            Player player = (Player) sender;
                            p.methods.viewBinds(player);
                            return true;
                        } else p.methodsError.noPermission(sender);
                    } else p.methodsError.notAPlayer(sender);
                }
            } else
            if (sender instanceof Player)
            {
                if (sender.hasPermission("deathbind.use"))
                {
                    Player player = (Player) sender;
                    
                    if (args.length == 0) p.methods.bind(player);
                    else p.methodsError.displayHelp(sender);
                    return true;
                } else p.methodsError.noPermission(sender);
            } else p.methodsError.notAPlayer(sender);
        } else p.methodsError.noPermission(sender);
        return true;
    }
}
