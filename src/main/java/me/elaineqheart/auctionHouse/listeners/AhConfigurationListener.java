package me.elaineqheart.auctionHouse.listeners;

import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.SellSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AhConfigurationListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();;
        ConfigManager.playerPreferences.saveInstance(p.getUniqueId(), AhConfiguration.getInstance(p));
        AhConfiguration.removeInstance(p);
        SellSession.remove(p.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        ConfigManager.playerPreferences.loadInstance(p);
    }

}
