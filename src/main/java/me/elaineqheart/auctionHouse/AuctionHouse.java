package me.elaineqheart.auctionHouse;

import me.elaineqheart.auctionHouse.GUI.GUIListener;
import me.elaineqheart.auctionHouse.GUI.GUIManager;
import me.elaineqheart.auctionHouse.GUI.other.AnvilGUIManager;
import me.elaineqheart.auctionHouse.commands.DynamicCommandRegisterer;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.data.PlayerPreferencesManager;
import me.elaineqheart.auctionHouse.listeners.AhConfigurationListener;
import me.elaineqheart.auctionHouse.listeners.PlayerJoinCollectListener;
import me.elaineqheart.auctionHouse.world.displays.DisplayListener;
import me.elaineqheart.auctionHouse.world.displays.KillListener;
import me.elaineqheart.auctionHouse.world.displays.UpdateDisplay;
import me.elaineqheart.auctionHouse.world.npc.NPCListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class AuctionHouse extends JavaPlugin {

    private static AuctionHouse instance;
    private static GUIManager guiManager;
    private static AnvilGUIManager anvilManager;
    public static AuctionHouse getPlugin() {return instance;}
    public static GUIManager getGuiManager() {return guiManager;}
    public static AnvilGUIManager getAnvilManager() {return anvilManager;}

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        guiManager = new GUIManager();
        GUIListener guiListener = new GUIListener(guiManager);
        anvilManager = new AnvilGUIManager();
        Bukkit.getPluginManager().registerEvents(guiListener, this);
        Bukkit.getPluginManager().registerEvents(anvilManager, this);

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Bukkit.getLogger().severe("No registered Vault provider found!");
            getServer().getPluginManager().disablePlugin(this);
        }

        Bukkit.getPluginManager().registerEvents(new NPCListener(), this);
        Bukkit.getPluginManager().registerEvents(new DisplayListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinCollectListener(), this);
        Bukkit.getPluginManager().registerEvents(new AhConfigurationListener(), this);
        KillListener.register();

        //Setup config.yml
        reloadConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        //Setup other yml files
        ConfigManager.setupConfigs();

        Messages.setup();
        Messages.get().options().copyDefaults(true);
        Messages.save();

        //if(SettingManager.useRedis) RedisManager.connect();

        try {
            ItemNoteStorage.loadNotes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PlayerPreferencesManager.setup();

        DynamicCommandRegisterer.init();
        UpdateDisplay.init();
        //NoteStorage.purge();

        getLogger().info("AuctionHouse enabled in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {
        PlayerPreferencesManager.disable();
        guiManager.forceCloseAll();
        //if(SettingManager.useRedis) RedisManager.disconnect();
    }

}
