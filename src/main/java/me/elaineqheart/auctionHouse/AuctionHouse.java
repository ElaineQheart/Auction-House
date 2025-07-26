package me.elaineqheart.auctionHouse;

import me.elaineqheart.auctionHouse.GUI.GUIListener;
import me.elaineqheart.auctionHouse.GUI.GUIManager;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.AnvilGUIListener;
import me.elaineqheart.auctionHouse.ah.CustomConfigBannedPlayers;
import me.elaineqheart.auctionHouse.ah.ItemNoteStorageUtil;
import me.elaineqheart.auctionHouse.ah.Messages;
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

    private static AuctionHouse plugin;
    private static GUIManager guiManager;
    public static AuctionHouse getPlugin() {return plugin;}
    public static GUIManager getGuiManager() {return guiManager;}

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        plugin = this;
        guiManager = new GUIManager();
        GUIListener guiListener = new GUIListener(guiManager);
        Bukkit.getPluginManager().registerEvents(guiListener, this);
        Bukkit.getPluginManager().registerEvents(new AnvilGUIListener(), this);

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().severe("No registered Vault provider found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("ah").setExecutor(new AuctionHouseCommands());
        getCommand("ah").setTabCompleter(new AuctionHouseCommands());
        Bukkit.getPluginManager().registerEvents(new NPCListener(), this);
        Bukkit.getPluginManager().registerEvents(new DisplayListener(), this);

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        Messages.init(this);

        CustomConfigBannedPlayers.setup();
        CustomConfigBannedPlayers.get().options().copyDefaults(false);
        CustomConfigBannedPlayers.save();

        DisplaysConfig.setup();
        DisplaysConfig.get().options().copyDefaults(false);
        DisplaysConfig.save();

        try {
            ItemNoteStorageUtil.loadNotes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UpdateDisplay.init();

        getLogger().info("AuctionHouse enabled in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if (p.getOpenInventory().getTopInventory().getHolder() instanceof InventoryGUI) {
                p.closeInventory();
            }
        }
        getLogger().info("AuctionHouse has been disabled.");
    }
}