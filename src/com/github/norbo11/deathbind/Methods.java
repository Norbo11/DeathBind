package com.github.norbo11.deathbind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Methods
{
    public Methods(DeathBind p)
    {
        this.p = p;
    }
    DeathBind p;
    
    //Lists binds by using a Bind object instead of a ItemStack object - this is because the Bind object has IDs. This should only be used when IDs are required.
    public List<Bind> listBinds(String player)
    {
        List<Bind> returnValue = new ArrayList<Bind>();
        List<ItemStack> binded = getBindedItems(player);
        int i = 0; //For assigning IDs
        for (ItemStack temp2 : binded)
        {
            returnValue.add(new Bind(i, temp2));
            i++;
        }
        return returnValue;
    }
    
    public void bind(Player player)
    {
        if (player.getItemInHand().getType() != Material.AIR)
        {
            ItemStack item = player.getItemInHand();
            List<ItemStack> binded = getBindedItems(player.getName());
            
            //Go through every binded item, if the ID, durability and enchantments match then set the contains flag to true.
            boolean contains = false;
            for (ItemStack stack : binded)
                if (stack.getTypeId() == item.getTypeId() && stack.getDurability() == item.getDurability() && stack.getEnchantments().equals(item.getEnchantments()))
                    contains = true;
            
            //If the item has not already been bound by the player
            if (!contains)
            {
                //If the player has not reached the maximum amount of binds allowed
                if (binded.size() < p.getConfig().getInt("maxAmountOfBinds"))
                {
                    //If he hasn't bound anything before, add a new line to the file in this format: PlayerName:ItemID-Damage[Enchantment1ID~Enchantment1LEVEL|Enchantment2ID~Enchantment2LEVEL
                    //, Separates items
                    //- Separates damage value from ID
                    //Everything after [ is an enchantment
                    //Enchantments are separated by |
                    //~ Separates the enchantment ID from it's level
                    if (binded.size() == 0)
                    {
                        String detailsToSave = item.getTypeId() + "-" + item.getDurability() + getEnchantments(item);
                        p.methodsFile.addToFile(p.FILE_SAVED_BINDS, player.getName() + ":" + detailsToSave + "\n");
                    } else //If the user has bound something already
                    {     
                        String oldDetails = ""; //This is everything after the ":" that is currently in the file, formed by the loop below
                        for (ItemStack stack : binded)
                        {
                            String temp = stack.getTypeId() + "-" + stack.getDurability() + getEnchantments(stack);
                            oldDetails = oldDetails + temp + ",";
                        }
                        oldDetails = oldDetails.substring(0, oldDetails.length() - 1); //Remove the comma at the end
                        
                        String detailsToSave = "," + item.getTypeId() + "-" + item.getDurability() + getEnchantments(item); //This is the new item details we have to save after the old details.
                        
                        //We read all the contents of the file, then replace the player's entry (Playername:olddetails) with the same thing, plus the new details to save.
                        String contents = p.methodsFile.readFile(p.FILE_SAVED_BINDS);
                        contents = contents.replace(player.getName() + ":" + oldDetails + "\n", player.getName() + ":" + oldDetails + detailsToSave + "\n");
                        p.methodsFile.setFile(p.FILE_SAVED_BINDS, contents);
                    }
                    //In this message, we replace the "[" because we have to convert a string into a number, and the string cant have a [ in it obviously
                    player.sendMessage(p.pluginTag + "You have succesfully bound " + ChatColor.GOLD + item.getType().toString() + ":" + ChatColor.GOLD + item.getDurability() + ChatColor.BLUE + convertEnchantments(getEnchantments(item).replace("[", "")) + "! You will now keep it on certain deaths. " + ChatColor.GOLD + "(/bind deaths)");
                } else p.methodsError.tooManyBinds(player);
            } else p.methodsError.itemAlreadyBound(player, item.getType().toString() + ":" + item.getDurability() + convertEnchantments(getEnchantments(item).replace("[", "")));
        } else p.methodsError.notHoldingAnItem(player);
    }
    
    //This returns the enchantments of the specified item stack like we would write in the file. [31~2|21~3 for example (thats two enchantments, a level 2 enchantment 31, and level 3 enchantment 21.)
    public String getEnchantments(ItemStack item)
    {
        String returnValue = "";
        if (item.getEnchantments().size() > 0) //If the item even has any enchantments... if it doesnt, the string returned is nothing.
        {
            returnValue = "[";
            for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) //Go through all the enchantments in the item then form our format. At the end we use substring to remove the last pipe |
                returnValue = returnValue + entry.getKey().getId() + "~" + entry.getValue() + "|";
            returnValue = returnValue.substring(0, returnValue.length() - 1);
        }
        return returnValue;
    }
    
    public void unBind(Player player, String id)
    {
        if (p.methodsMisc.isInteger(id))
        {
            List<Bind> bindList = listBinds(player.getName());
            List<ItemStack> binded = getBindedItems(player.getName());
            
            //We go through the bindList returned by listBinds which simply gives IDs to our binded items. If the ID that the user specified is really an id of a bound item, we set the valid flag to true.
            boolean valid = false;
            for (Bind bind : bindList)
                if (bind.id == Integer.parseInt(id)) valid = true;
            
            if (valid)
            {
                //Like in the bind() method, we need to get the string of old details so we can later replace it.
                String oldDetails = "";
                for (ItemStack stack : binded)
                    oldDetails = oldDetails + stack.getTypeId() + "-" + stack.getDurability() + getEnchantments(stack) + ",";
                oldDetails = oldDetails.substring(0, oldDetails.length() - 1);
                
                //Store a Bind object of the item that the user wants to remove for later use. We get that from the bindList (the IDs match the order of the array list)
                Bind item = bindList.get(Integer.parseInt(id));
                binded.remove(item.item); //Remove it from our binded list (not the bindList, the binded list, thats the actual list of item stacks that the player has bound.)
                
                String contents = p.methodsFile.readFile(p.FILE_SAVED_BINDS);
                if (binded.size() > 0) //We check if the new list of binded items even has any items in it
                {
                    String details = ""; //This is the details after removal of the item
                    for (ItemStack stack : binded)
                    {
                        String temp = stack.getTypeId() + "-" + stack.getDurability() + getEnchantments(stack);
                        details = details + temp + ",";
                    }
                    if (details.contains(",")) details = details.substring(0, details.length() - 1); //Dont remove the if statement!
                    
                    //Replace the player's entry with our new details (which should just be the leftover binded items)
                    contents = contents.replace(player.getName() + ":" + oldDetails + "\n", player.getName() + ":" + details + "\n");
                } else contents = contents.replace(player.getName() + ":" + oldDetails + "\n", ""); //If it doesnt, we remove the entry of the player completely.
                p.methodsFile.setFile(p.FILE_SAVED_BINDS, contents);
                player.sendMessage(p.pluginTag + "You have succesfully un-bound " + ChatColor.GOLD + item.item.getType().toString() + ":" + ChatColor.GOLD + item.item.getDurability() + ChatColor.BLUE + convertEnchantments(getEnchantments(item.item).replace("[", ""))+ "!");
            } else p.methodsError.notABindId(player);
        } else p.methodsError.notANumber(player);
    }
    
    //Returns a list of item stacks of all the binded items of the player. this uses a lot of string splitting and parsing.
    public List<ItemStack> getBindedItems(String player)
    {
        List<String> contents = p.methodsFile.splitByLine(p.methodsFile.readFile(p.FILE_SAVED_BINDS));
        List<ItemStack> binded = new ArrayList<ItemStack>();
        for (String line : contents)
        {
            if (!line.startsWith("#"))
            {
                String[] split = line.split(":");
                if (split[0].equalsIgnoreCase(player))
                {
                    for (String temp : split[1].split(","))
                    {
                        String itemID = temp.split("-")[0];
                        String afterID = temp.split("-")[1];
                        String damage;
                        HashMap<String, String> enchantment = new HashMap<String, String>();
                        if (temp.contains("["))
                        {
                            damage = afterID.split("\\[")[0];
                            String enchantments = afterID.split("\\[")[1];
                            if (enchantments.contains("|"))
                            {
                                for (String temp2 : enchantments.split("\\|"))
                                {
                                    String id = temp2.split("~")[0];
                                    String level = temp2.split("~")[1];
                                    enchantment.put(id, level);
                                }
                            } else enchantment.put(enchantments.split("~")[0], enchantments.split("~")[1]);
                        } else damage = afterID;
                        ItemStack temp3 = new ItemStack(Material.getMaterial(Integer.parseInt(itemID)), 1, Short.parseShort(damage));
                        HashMap<Enchantment, Integer> actualEnchantment = new HashMap<Enchantment, Integer>();
                        for (Entry<String, String> entry : enchantment.entrySet())
                            actualEnchantment.put(Enchantment.getById(Integer.parseInt(entry.getKey())), Integer.parseInt(entry.getValue()));
                        temp3.addEnchantments(actualEnchantment);
                        binded.add(temp3);   
                    }
                    break;
                }
            }
        }
        return binded;
    }
    
    //Converts the specified int into greek letters.
    public String getEnchantmentLevel(int level)
    {
        String returnValue = "";
        if (level == 1) returnValue = "I";
        if (level == 2) returnValue = "II";
        if (level == 3) returnValue = "III";
        if (level == 4) returnValue = "IV";
        if (level == 5) returnValue = "V";
        if (level == 6) returnValue = "VI";
        return returnValue;
    }
    
    //Converts the bukkit enchantment name, like DAMAGE_ALL into a proper enchantment name for the user, like Sharpness. (bukkit doesnt have a method for this :()
    public String getEnchantmentName(String enchantment)
    {
        String returnValue = "";
        if (enchantment.equalsIgnoreCase("ARROW_DAMAGE")) returnValue = "Power";
        if (enchantment.equalsIgnoreCase("ARROW_FIRE")) returnValue = "Flame";
        if (enchantment.equalsIgnoreCase("ARROW_INFINITE")) returnValue = "Infinity";
        if (enchantment.equalsIgnoreCase("ARROW_KNOCKBACK")) returnValue = "Punch";
        if (enchantment.equalsIgnoreCase("DAMAGE_ALL")) returnValue = "Sharpness";
        if (enchantment.equalsIgnoreCase("DAMAGE_ARTHROPODS")) returnValue = "Bane of Arthopods";
        if (enchantment.equalsIgnoreCase("DAMAGE_UNDEAD")) returnValue = "Smite";
        if (enchantment.equalsIgnoreCase("DIG_SPEED")) returnValue = "Efficiency";
        if (enchantment.equalsIgnoreCase("DURABILITY")) returnValue = "Unbreaking";
        if (enchantment.equalsIgnoreCase("FIRE_ASPECT")) returnValue = "Fire Aspect";
        if (enchantment.equalsIgnoreCase("KNOCKBACK")) returnValue = "Knockback";
        if (enchantment.equalsIgnoreCase("LOOT_BONUS_BLOCKS")) returnValue = "Fortune";
        if (enchantment.equalsIgnoreCase("LOOT_BONUS_MOBS")) returnValue = "Looting";
        if (enchantment.equalsIgnoreCase("OXYGEN")) returnValue = "Respiration";
        if (enchantment.equalsIgnoreCase("PROTECTION_ENVIRONMENTAL")) returnValue = "Protection";
        if (enchantment.equalsIgnoreCase("PROTECTION_EXPLOSIONS")) returnValue = "Blast Protection";
        if (enchantment.equalsIgnoreCase("PROTECTION_FALL")) returnValue = "Feather Falling";
        if (enchantment.equalsIgnoreCase("PROTECTION_FIRE")) returnValue = "Fire Protection";
        if (enchantment.equalsIgnoreCase("PROTECTION_PROJECTILE")) returnValue = "Projectile Protection";
        if (enchantment.equalsIgnoreCase("SILK_TOUCH")) returnValue = "Silk Touch";
        if (enchantment.equalsIgnoreCase("WATER_WORKER")) returnValue = "Aqua Affinity";
        return returnValue;
    }
    
    //This parses a string that we read from the text file (like [31~2|21~3) and changes it into a colored string that the player can read, like "with Fire Aspect III"
    //Returns an empty string if the gives string to parse is empty.
    public String convertEnchantments(String toParse)
    {
        String returnValue = "";
        if (!toParse.equals(""))
        {
            String enchantments = "";
            if (toParse.contains("|"))
            {
                for (String temp : toParse.split("\\|"))
                {
                    String id = temp.split("~")[0];
                    String level = temp.split("~")[1];
                    enchantments = enchantments + ChatColor.GOLD + getEnchantmentName(Enchantment.getById(Integer.parseInt(id)).getName()) + " " + getEnchantmentLevel(Integer.parseInt(level)) + ChatColor.BLUE + ", ";
                }
                enchantments = enchantments.substring(0, enchantments.length() - 2);
            } else enchantments = ChatColor.GOLD + getEnchantmentName(Enchantment.getById(Integer.parseInt(toParse.split("~")[0])).getName()) + " " + getEnchantmentLevel(Integer.parseInt(toParse.split("~")[1])) + ChatColor.BLUE;
            returnValue = " with " + enchantments;
        }
        return returnValue;
    }
    
    //Displays to the user all of his current binds.
    public void viewBinds(Player player)
    {
        List<Bind> binded = listBinds(player.getName());
        if (binded.size() > 0)
        {
            player.sendMessage(p.pluginTag + "Bound items (Item:Damage with Enchantments): ");

            for (Bind bind : binded)
                player.sendMessage(p.pluginTag + "[" + bind.id + "] " + ChatColor.GOLD + bind.item.getType().toString() + ":" + bind.item.getDurability() + ChatColor.BLUE + convertEnchantments(getEnchantments(bind.item).replace("[", "")));
            
            player.sendMessage(p.pluginTag + "The maxiumum amount of each item that will be saved on death: " + ChatColor.GOLD + p.getConfig().getInt("maxAmountOfSavedItems"));
            player.sendMessage(p.pluginTag + "Unbind items with " + ChatColor.GOLD + "'/unbind [ID]'" + ChatColor.BLUE + " - replace [ID] with the ID of the bind as listed above.");
        } else player.sendMessage(p.pluginTag + "No binds available. Bind with " + ChatColor.GOLD + "/bind [item ID | item name]");
    }
}
