package com.github.norbo11.deathbind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class PluginListener implements Listener
{
    public PluginListener(DeathBind p)
    {
        this.p = p;
    }
    DeathBind p;
    //We use this hashmap to make sure that if 2 players die at the same time before respawning, they will receive only their items.
    Map<String, List<ItemStack>> savedItems = new HashMap<String, List<ItemStack>>();
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        //Check if the circumstance that the player has died in is inside our config, and is true.
        if (p.getConfig().getBoolean("circumstances." + event.getEntity().getLastDamageCause().getCause().toString()))
        {
            List<ItemStack> binded = p.methods.getBindedItems(event.getEntity().getPlayer().getName());
            for (ItemStack item : binded) //Go through every item that the player has bound
            {
                //Count the amount of that item that the player has in his inventory. Match only if the ID, durability and enchantments are exactly the same.
                int amount = 0;
                for (ItemStack stack : event.getEntity().getInventory().getContents())
                    if (stack != null)
                        if (stack.getTypeId() == item.getTypeId() && stack.getDurability() == item.getDurability() && stack.getEnchantments().equals(item.getEnchantments()))
                            amount = amount + stack.getAmount();
                
                if (amount > 0) //If there is more that 1 of that item in the player's inventory
                {
                    
                    int receivingAmount = p.getConfig().getInt("maxAmountOfSavedItems"); //Assume the player will receive the maxiumum
                    int toBeDropped = 0; //Assume that we will drop nothing on the floor
                    
                    if (amount > p.getConfig().getInt("maxAmountOfSavedItems")) //If the amount of this item inside the player's inventory is over our maxiumum
                    {
                        //We keep the receiving amount the maxiumum (receivingAmount = p.getConfig().getInt("maxAmountOfSavedItems"))
                        toBeDropped = amount - p.getConfig().getInt("maxAmountOfSavedItems"); //Drop anything above the maxiumum
                    } else receivingAmount = amount; //If the player has less than the maxiumum, simply make him receive what he has without dropping anything.
                    
                    int excess = receivingAmount % item.getMaxStackSize(); //The remainder after we make our full stacks. So if we are receiving 65 on an item like cobble, the excess is 1.
                    int stacks = (receivingAmount - excess) / item.getMaxStackSize(); //We take our excess away from what we are receiving and then divide by the maxiumum stack size to get the amount of stacks the user will get of the item.
                    
                    List<ItemStack> materials = new ArrayList<ItemStack>(); //This is the stacks that the player will receive of this item
                    ItemStack material;
                    for (int i = 0; i < stacks; i++) //Add however many stacks we have
                    {
                        material = new ItemStack(item.getType(), item.getMaxStackSize(), item.getDurability());
                        material.addEnchantments(item.getEnchantments());
                        materials.add(material);
                    }
                    
                    if (excess > 0) //After we are finished with the stacks, add the excess ass a seperate stack
                    {
                        material = new ItemStack(item.getType(), excess, item.getDurability());
                        material.addEnchantments(item.getEnchantments());
                        materials.add(material);
                    }

                    //Put the materials object into the entry in savedItems - if the player doesnt have an entry (and therefore throws an exception), create one.
                    try { 
                        savedItems.get(event.getEntity().getName()).addAll(materials);
                    } catch (Exception e)
                    {
                        savedItems.put(event.getEntity().getName(), materials);
                    }
                    
                    List<ItemStack> newDrops = new ArrayList<ItemStack>();  //The drops that will actually be dropped
                    int droppedAmount = 0;
                    for (ItemStack oldDrop : event.getDrops()) //Go through all the drops in this event
                    {
                        //If the drop is not matching to this item we are going through, add him to the newDrops object
                        if (oldDrop.getTypeId() != item.getTypeId() || oldDrop.getDurability() != item.getDurability() || !oldDrop.getEnchantments().equals(item.getEnchantments()))
                            newDrops.add(oldDrop);
                        else if (droppedAmount < toBeDropped) //If we have not yet dropped all that we need to drop
                        {
                            ItemStack temp;
                            //If dropping this amount goes over our maximum stack size, then fill the temp object with the maximum possible stack.
                            if (toBeDropped - droppedAmount > item.getMaxStackSize()) temp = new ItemStack(item.getType(), item.getMaxStackSize(), item.getDurability());
                            else temp = new ItemStack(item.getType(), toBeDropped - droppedAmount, item.getDurability()); //If its less than our maximum stack size then just add whatever is left.
                            temp.addEnchantments(item.getEnchantments());
                            
                            newDrops.add(temp); //Add this temp object to our drops.
                            droppedAmount = droppedAmount + temp.getAmount(); //Add the amount we are dropping to the droppedAmount.
                        }
                    }
                    event.getDrops().clear(); //Clear the drops of the event
                    event.getDrops().addAll(newDrops); //Make the drops of the event our newDrops object.
                }
            }
        }
    }
   
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        //If the user has any items saved
        if (savedItems.containsKey(event.getPlayer().getName()))
        {
            //This holds the amounts of our objects so that we will display "You receive 128 of cobble" instead of "You receive 64 of cobble" x2.
            //The key in this map is a string, because if it was an item stack, we couldnt compare it, because comparing item stacks also compares amounts, and the binded list returns all binded items with the amount of "1".
            //This is why we use a string representation format as if its inside the file, eg 35-1[31~2|21~3...
            HashMap<String, Integer> amounts = new HashMap<String, Integer>();
            for (ItemStack temp : savedItems.get(event.getPlayer().getName())) //Go through the items that the player has saved
            {
                event.getPlayer().getInventory().addItem(temp); //Add them to his inventory
                //Put the item inside the amounts map. If it doesnt exist we put in just the stack amount, if it does exist, we add the amount to whats in there already.
                try 
                { 
                    amounts.get(temp);
                    amounts.put(temp.getType() + "-" + temp.getDurability() + p.methods.getEnchantments(temp), amounts.get(temp.getType() + "-" + temp.getDurability() + p.methods.getEnchantments(temp)) + temp.getAmount());
                } catch (Exception e) { amounts.put(temp.getType() + "-" + temp.getDurability() + p.methods.getEnchantments(temp), temp.getAmount()); }
            }
            for (Entry<String, Integer> stack : amounts.entrySet()) //Go through every amount in the map, split it up, display to the user.
            {
                String mat = stack.getKey().split("-")[0];
                String amount = stack.getValue() + "";
                String damage = stack.getKey().split("-")[1].split("\\[")[0];
                String enchantments = "";
                if (stack.getKey().contains("[")) //If it contains a [, it means enchantments are present.
                    enchantments = p.methods.convertEnchantments(stack.getKey().split("-")[1].split("\\[")[1]);
                event.getPlayer().sendMessage(p.pluginTag + "You receive " + ChatColor.GOLD + amount + ChatColor.BLUE +  " of " + ChatColor.GOLD + mat + ChatColor.BLUE + ":" + ChatColor.GOLD + damage + ChatColor.BLUE + enchantments);
            }
            savedItems.remove(event.getPlayer().getName()); //Remove the entry of the player from our saved items map.
        }
    }
}
