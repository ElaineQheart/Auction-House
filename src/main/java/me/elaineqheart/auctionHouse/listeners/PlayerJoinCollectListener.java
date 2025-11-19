package me.elaineqheart.auctionHouse.listeners;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.CollectSoldItemGUI;
import me.elaineqheart.auctionHouse.data.items.StringUtils;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNote;
import me.elaineqheart.auctionHouse.data.persistentStorage.NoteStorage;
import me.elaineqheart.auctionHouse.data.yml.Messages;
import me.elaineqheart.auctionHouse.data.yml.SettingManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinCollectListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!SettingManager.autoCollect) return;
        Bukkit.getScheduler().runTask(AuctionHouse.getPlugin(), () -> {
            Player p = event.getPlayer();
            for(ItemNote note : NoteStorage.mySortedDateCreated(p.getUniqueId())) {
                if (!note.isSold()) continue;
                int amount = note.getItem().getAmount() - note.getPartiallySoldAmountLeft();
                CollectSoldItemGUI.collect(p, note.getNoteID().toString(), amount, note.getSoldPrice());
                if(SettingManager.soldMessageEnabled) p.sendMessage(Messages.getFormatted("chat.sold-message.auto-collect",
                        "%player%", note.getBuyerName(),
                        "%item%", note.getItemName(),
                        "%price%", StringUtils.formatPrice(note.getSoldPrice()),
                        "%amount%", String.valueOf(amount)));
            }
        });

    }

}
