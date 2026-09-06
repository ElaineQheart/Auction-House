package me.elaineqheart.auctionHouse;

import me.elaineqheart.auctionHouse.GUI.GUIListener;
import me.elaineqheart.auctionHouse.GUI.GUIManager;
import me.elaineqheart.auctionHouse.GUI.other.input.InputGUIManager;
import me.elaineqheart.auctionHouse.commands.DynamicCommandRegisterer;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import me.elaineqheart.auctionHouse.listeners.AhConfigurationListener;
import me.elaineqheart.auctionHouse.listeners.PlayerJoinCollectListener;
import me.elaineqheart.auctionHouse.pluginDependencies.AuctionHousePAPIExpansion;
import me.elaineqheart.auctionHouse.pluginDependencies.DiscordSRVHook;
import me.elaineqheart.auctionHouse.pluginDependencies.LocaleAPIExtension;
import me.elaineqheart.auctionHouse.world.displays.DisplayKillListener;
import me.elaineqheart.auctionHouse.world.displays.DisplayListener;
import me.elaineqheart.auctionHouse.world.displays.UpdateDisplay;
import me.elaineqheart.auctionHouse.world.npc.NPCListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.morepaperlib.MorePaperLib;
import space.arim.morepaperlib.scheduling.GracefulScheduling;

import java.io.IOException;

public final class AuctionHouse extends JavaPlugin {

    private static AuctionHouse instance;
    private static GUIManager guiManager;
    private static InputGUIManager inputGUIManager;
    public static GUIManager getGuiManager() {return guiManager;}
    public static InputGUIManager getInputManager() {return inputGUIManager;}
    private MorePaperLib morePaperLib;
    public static AuctionHouse getInstance() {return instance;}
    public static GracefulScheduling getScheduler() {
        //morePaperLib.scheduling()... It uses Paper's threaded-regions schedulers if Folia is used, otherwise it falls back to the default Bukkit scheduler.
        return instance.morePaperLib.scheduling();
    }

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        LocaleAPIExtension.setup();
        instance = this;
        guiManager = new GUIManager();
        GUIListener guiListener = new GUIListener(guiManager, this);
        inputGUIManager = new InputGUIManager(this);
        morePaperLib = new MorePaperLib(instance);

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getServer().getLogger().severe("No registered Vault provider found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new NPCListener(), this);
        getServer().getPluginManager().registerEvents(new DisplayListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinCollectListener(), this);
        getServer().getPluginManager().registerEvents(new AhConfigurationListener(), this);
        DisplayKillListener.register();

        ConfigManager.setupConfigs();

        //if(SettingManager.useRedis) RedisManager.connect();

        try {
            ItemNoteStorage.loadNotes();
        } catch (IOException e) {
            getLogger().severe("Failed to load Auction House item data");
            throw new RuntimeException(e);
        }

        DynamicCommandRegisterer.init();
        UpdateDisplay.init();
        //NoteStorage.purge();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new AuctionHousePAPIExpansion().register();
            getLogger().info("PlaceholderAPI expansion registered.");
        }
        if (getServer().getPluginManager().getPlugin("DiscordSRV") != null)
            DiscordSRVHook.register();

        getLogger().info("AuctionHouse enabled in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {
        ConfigManager.playerPreferences.disable();
        if(guiManager != null) guiManager.forceCloseAll();
        if(inputGUIManager != null) inputGUIManager.forceCloseAll();
        if (getServer().getPluginManager().getPlugin("DiscordSRV") != null) {
            DiscordSRVHook.unregister();
        }
        //if(SettingManager.useRedis) RedisManager.disconnect();
    }


    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
