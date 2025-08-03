package me.elaineqheart.auctionHouse;

import me.elaineqheart.auctionHouse.GUI.GUIListener;
import me.elaineqheart.auctionHouse.GUI.GUIManager;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.AnvilGUIListener;
import me.elaineqheart.auctionHouse.data.CustomConfigBannedPlayers;
import me.elaineqheart.auctionHouse.data.Messages;
import me.elaineqheart.auctionHouse.data.items.ItemNoteStorageUtil;
import me.elaineqheart.auctionHouse.commands.AuctionHouseCommands;
import me.elaineqheart.auctionHouse.world.displays.DisplayListener;
import me.elaineqheart.auctionHouse.world.npc.NPCListener;
import me.elaineqheart.auctionHouse.world.displays.UpdateDisplay;
import me.elaineqheart.auctionHouse.data.DisplaysConfig;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class AuctionHouse extends JavaPlugin {

    private static AuctionHouse instance;
    private static GUIManager guiManager;
    public static AuctionHouse getPlugin() {return instance;}
    public static GUIManager getGuiManager() {return guiManager;}

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        guiManager = new GUIManager();
        GUIListener guiListener = new GUIListener(guiManager);
        Bukkit.getPluginManager().registerEvents(guiListener, this);
        Bukkit.getPluginManager().registerEvents(new AnvilGUIListener(), this);

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Bukkit.getLogger().severe("No registered Vault provider found!");
            getServer().getPluginManager().disablePlugin(this);
        }

        getCommand("ah").setExecutor(new AuctionHouseCommands());
        getCommand("ah").setTabCompleter(new AuctionHouseCommands());
        Bukkit.getPluginManager().registerEvents(new NPCListener(), this);
        Bukkit.getPluginManager().registerEvents(new DisplayListener(), this);

        //Setup config.yml
        reloadConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        //Setup bannedPlayers.yml
        CustomConfigBannedPlayers.setup();
        //Setup customConfigEntities.yml
        DisplaysConfig.setup();

        Messages.setup();
        Messages.get().options().copyDefaults(true);
        Messages.save();

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
    }

}
