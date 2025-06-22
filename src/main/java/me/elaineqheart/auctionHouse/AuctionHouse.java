package me.elaineqheart.auctionHouse;

import me.elaineqheart.auctionHouse.GUI.GUIListener;
import me.elaineqheart.auctionHouse.GUI.GUIManager;
import me.elaineqheart.auctionHouse.GUI.other.AnvilGUIListener;
import me.elaineqheart.auctionHouse.ah.SettingManager;
import me.elaineqheart.auctionHouse.commands.AuctionHouseCommand;
import me.elaineqheart.auctionHouse.ah.CustomConfigBannedPlayers;
import me.elaineqheart.auctionHouse.ah.ItemManager;
import me.elaineqheart.auctionHouse.ah.ItemNoteStorageUtil;
import me.elaineqheart.auctionHouse.commands.ReloadCommand;
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
        ItemManager.init();
        guiManager = new GUIManager();
        GUIListener guiListener = new GUIListener(guiManager);
        Bukkit.getPluginManager().registerEvents(guiListener, this);
        Bukkit.getPluginManager().registerEvents(new AnvilGUIListener(), this);

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Bukkit.getLogger().severe("No registered Vault provider found!");
            getServer().getPluginManager().disablePlugin(this);
        }
        getCommand("ah").setExecutor(new AuctionHouseCommand());
        getCommand("ah").setTabCompleter(new AuctionHouseCommand());
        getCommand("ahreload").setExecutor(new ReloadCommand());
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
        CustomConfigBannedPlayers.get().options().copyDefaults(true);
        CustomConfigBannedPlayers.save();

        //also, you need a regular config.yml to generate the folder where the .yml files are

        getLogger().info("AuctionHouse enabled in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {
        for(Player p : Bukkit.getOnlinePlayers()){
            p.closeInventory();
        }
    }

}
