package com.github.norbo11.deathbind;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathBind extends JavaPlugin
{
    //Instances
    public PluginCommandExecutor commandExecutor = new PluginCommandExecutor(this);
    public MethodsError methodsError = new MethodsError(this);
    public MethodsFile methodsFile = new MethodsFile(this);
    public MethodsMisc methodsMisc = new MethodsMisc(this);
    public Methods methods = new Methods(this);
    
    //Variables
    public String pluginTag = ChatColor.BLUE + "[DeathBind] ";
    public String FILE_SAVED_BINDS = "savedBinds.txt";
    Logger log;
    String v;
    
    //Files
    public File pluginDir;
    public File pluginConfig;
    public File savedBinds;
    
    
    public void onEnable()
    {
        log = getLogger();
        v = getDescription().getVersion();
        pluginDir = getDataFolder();
        pluginConfig = new File(pluginDir, "config.yml");
        savedBinds = new File(pluginDir, FILE_SAVED_BINDS);
        
        // Make config
        if (!pluginConfig.exists())
        {
            log.info("Creating config file...");
            getConfig().options().copyDefaults();
            saveDefaultConfig();
        }
        
        //Make a savedBinds.txt files
        if (!savedBinds.exists()) try
        {
            savedBinds.createNewFile();
            String content = 
                    "##############################################################################################\n" +
            		"#                                    Saved binds file                                        #\n" +
            		"# You may delete lines if you wish, but IT IS NOT RECOMMENDED TO MESS WITH INDIVIDUAL        #\n" +
            		"# ENTRIES UNLESS YOU KNOW WHAT YOU ARE DOING! INVALID ENTRIES WILL CAUSE UNDEFINED BEHAVIOUR #\n" +
            		"#                                                                                            #\n" +
            		"# Items are in this format:                                                                  #\n" +
            		"# ItemID-Damage[Enchantment1ID~Enchantment1LEVEL|Enchantment2ID~Enchantment2LEVEL            #\n" +
                    "#                                                                                            #\n" +
            		"# Binds are seperated by commas, and an entry for a player starts with 'Name:'.              #\n" +
                    "#                                                                                            #\n" +
            		"# For example, the following binds Norbo11 to a fully repaired diamond sword with            #\n" +
            		"# Knockback I and Fire Aspect II, and a bookshelf:                                           #\n" +
                    "#                                                                                            #\n" +
            		"# Norbo11:276-0[19~1|20~2,47-0                                                               #\n" +
                    "##############################################################################################\n";
            methodsFile.setFile(savedBinds.getName(), content);
        } catch (IOException e)
        {
            log.severe("Error occured when creating a savedBinds.txt file!");
            e.printStackTrace();
            getPluginLoader().disablePlugin(this);
            return;
        }
        
        getCommand("bind").setExecutor(commandExecutor);
        getCommand("unbind").setExecutor(commandExecutor);
        getServer().getPluginManager().registerEvents(new PluginListener(this), this);
        
        log.info("ItemBind v" + v + " succesfully enabled!");
        
    }
    
    public void onDisable()
    {
        log.info("ItemBind v" + v + " succesfully disabled!");

    }
}
