package com.github.norbo11.deathbind;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MethodsError
{
    public MethodsError(DeathBind p)
    {
        this.p = p;
    }
    DeathBind p;
    
    public void notAPlayer(CommandSender sender)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "Sorry, this command cannot be executed from the console!");
    }

    public void tooManyBinds(CommandSender sender)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "You have reached the maximum amount of items that you can bind! " + ChatColor.GOLD + "(" + p.getConfig().getInt("maxAmountOfBinds") + ")");
    }
    
    public void itemAlreadyBound(CommandSender sender, String item)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "You have already bound " + item + "!");
    }

    public void noPermission(CommandSender sender)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "You do not have permission to do this.");
    }

    public void displayHelp(CommandSender sender)
    {
        sender.sendMessage(p.pluginTag + "/bind " + ChatColor.GOLD + "- Binds the item you're currently holding.");
        sender.sendMessage(p.pluginTag + "/unbind [ID]" + ChatColor.GOLD + "- Unbinds the item with the specified ID as shown in /bind list.");
        sender.sendMessage(p.pluginTag + "/bind list " + ChatColor.GOLD + "- Lists all your bound items.");
        sender.sendMessage(p.pluginTag + "/bind deaths " + ChatColor.GOLD + "- Lists all circumstances which you can die by and get your items back.");
        sender.sendMessage(p.pluginTag + "/bind help " + ChatColor.GOLD + "- Displays this help message.");
        sender.sendMessage(p.pluginTag + "ItemBind " + ChatColor.GOLD + "v" + p.v + ChatColor.BLUE + " by Norbo11");
    }

    public void itemNotBound(Player player, String item)
    {
        player.sendMessage(p.pluginTag + ChatColor.RED + "You have not bound " + ChatColor.GOLD + item + ChatColor.BLUE + "!");
    }

    public void notHoldingAnItem(Player player)
    {
        player.sendMessage(p.pluginTag + ChatColor.RED + "You are not holding an item!");
    }
    
    //Converts a boolean into "yes" or "no"
    private String convertToYesNo(boolean toConvert)
    {
        if (toConvert) return "yes";
        else return "no";
    }

    //Displays to the user the list of circumstances and their values in the config.
    public void displayCircumstances(CommandSender sender)
    {
        String BLOCK_EXPLOSION = convertToYesNo(p.getConfig().getBoolean("circumstances.BLOCK_EXPLOSION"));
        String CONTACT = convertToYesNo(p.getConfig().getBoolean("circumstances.CONTACT"));
        String CUSTOM = convertToYesNo(p.getConfig().getBoolean("circumstances.CUSTOM"));
        String DROWNING = convertToYesNo(p.getConfig().getBoolean("circumstances.DROWNING"));
        String ENTITY_ATTACK = convertToYesNo(p.getConfig().getBoolean("circumstances.ENTITY_ATTACK"));
        String ENTITY_EXPLOSION = convertToYesNo(p.getConfig().getBoolean("circumstances.ENTITY_EXPLOSION"));
        String FALL = convertToYesNo(p.getConfig().getBoolean("circumstances.FALL"));
        String FIRE = convertToYesNo(p.getConfig().getBoolean("circumstances.FIRE"));
        String FIRE_TICK = convertToYesNo(p.getConfig().getBoolean("circumstances.FIRE_TICK"));
        String LAVA = convertToYesNo(p.getConfig().getBoolean("circumstances.LAVA"));
        String LIGHTNING = convertToYesNo(p.getConfig().getBoolean("circumstances.LIGHTNING"));
        String MAGIC = convertToYesNo(p.getConfig().getBoolean("circumstances.MAGIC"));
        String POISON = convertToYesNo(p.getConfig().getBoolean("circumstances.POISON"));
        String PROJECTILE = convertToYesNo(p.getConfig().getBoolean("circumstances.PROJECTILE"));
        String STARVATION = convertToYesNo(p.getConfig().getBoolean("circumstances.STARVATION"));
        String SUFFOCATION = convertToYesNo(p.getConfig().getBoolean("circumstances.SUFFOCATION"));
        String SUICIDE = convertToYesNo(p.getConfig().getBoolean("circumstances.SUICIDE"));
        String VOID = convertToYesNo(p.getConfig().getBoolean("circumstances.VOID"));
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Block explosion: " + ChatColor.BLUE + BLOCK_EXPLOSION);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Contact (cactus etc): " + ChatColor.BLUE + CONTACT);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Custom (another plugin): " + ChatColor.BLUE + CUSTOM);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Drowning: " + ChatColor.BLUE + DROWNING);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Attacked by player/monster: " + ChatColor.BLUE + ENTITY_ATTACK);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Player/monster nearby explosion: " + ChatColor.BLUE + ENTITY_EXPLOSION);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Fall damage: " + ChatColor.BLUE + FALL);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Direct contact with fire: " + ChatColor.BLUE + FIRE);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Burning (but not inside fire): " + ChatColor.BLUE + FIRE_TICK);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Direct contact with lava: " + ChatColor.BLUE + LAVA);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Struck by lightning: " + ChatColor.BLUE + LIGHTNING);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Potion damage: " + ChatColor.BLUE + MAGIC);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Poison: " + ChatColor.BLUE + POISON);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Projectile (arrow etc): " + ChatColor.BLUE + PROJECTILE);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Starvation: " + ChatColor.BLUE + STARVATION);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Suffocation (inside block): " + ChatColor.BLUE + SUFFOCATION);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Suicide (/kill): " + ChatColor.BLUE + SUICIDE);
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "Falling into void: " + ChatColor.BLUE + VOID);

    }

    public void notABindId(Player player)
    {
        player.sendMessage(p.pluginTag + ChatColor.RED + "Invalid bind ID!");
    }

    public void notANumber(Player player)
    {
        player.sendMessage(p.pluginTag + ChatColor.RED + "That is not a number!");
    }
}
