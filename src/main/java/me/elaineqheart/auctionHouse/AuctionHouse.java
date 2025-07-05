package me.elaineqheart.auctionHouse;

import me.elaineqheart.auctionHouse.GUI.GUIListener;
import me.elaineqheart.auctionHouse.GUI.GUIManager;
import me.elaineqheart.auctionHouse.GUI.other.AnvilGUIListener;
import me.elaineqheart.auctionHouse.ah.CustomConfigBannedPlayers;
import me.elaineqheart.auctionHouse.ah.ItemNoteStorageUtil;
import me.elaineqheart.auctionHouse.ah.SettingManager;
import me.elaineqheart.auctionHouse.commands.AuctionHouseCommands;
import me.elaineqheart.auctionHouse.world.DisplayListener;
import me.elaineqheart.auctionHouse.world.NPCListener;
import me.elaineqheart.auctionHouse.world.UpdateDisplay;
import me.elaineqheart.auctionHouse.world.files.DisplaysConfig;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class AuctionHouse extends JavaPlugin {

    private static AuctionHouse getPlugin;
    private static GUIManager guiManager;
    public static AuctionHouse getPlugin() {return getPlugin;}
    public static GUIManager getGuiManager() {return guiManager;}

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        getPlugin = this;
        guiManager = new GUIManager();
        GUIListener guiListener = new GUIListener(guiManager);
        Bukkit.getPluginManager().registerEvents(guiListener, this);
        Bukkit.getPluginManager().registerEvents(new AnvilGUIListener(), this); //GUI

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Bukkit.getLogger().severe("No registered Vault provider found!");
            getServer().getPluginManager().disablePlugin(this);
        }

        getCommand("ah").setExecutor(new AuctionHouseCommands());
        getCommand("ah").setTabCompleter(new AuctionHouseCommands());
        Bukkit.getPluginManager().registerEvents(new NPCListener(), this);
        Bukkit.getPluginManager().registerEvents(new DisplayListener(), this);

        //load the data of the notes file
        try {
            ItemNoteStorageUtil.loadNotes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SettingManager.loadData();
        //Setup config.yml
        super.reloadConfig(); //reload if there were changes
        getConfig().options().copyDefaults(true);
        saveConfig();
        //Setup bannedPlayers.yml
        CustomConfigBannedPlayers.setup();
        CustomConfigBannedPlayers.get().options().copyDefaults(false);
        CustomConfigBannedPlayers.save();
        //also, you need a regular config.yml to generate the folder where the .yml files are, but I now I actually use it for custom settings
        //Setup customConfigEntities.yml
        DisplaysConfig.setup();
        DisplaysConfig.get().options().copyDefaults(false);
        DisplaysConfig.save();

        UpdateDisplay.init(); //init the display update task to update block displays

        getLogger().info("AuctionHouse enabled in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {
        for(Player p : Bukkit.getOnlinePlayers()){
            p.closeInventory();
        }
    }

}
