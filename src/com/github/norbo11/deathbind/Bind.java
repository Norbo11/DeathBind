package com.github.norbo11.deathbind;

import org.bukkit.inventory.ItemStack;

public class Bind
{
    public Bind(int id, ItemStack item)
    {
        this.id = id;
        this.item = item;
    }
    int id;
    ItemStack item;
}
