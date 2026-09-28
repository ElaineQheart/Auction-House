package me.elaineqheart.auctionHouse.commands;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.SettingManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import me.elaineqheart.auctionHouse.pluginDependencies.DiscordSRVHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

// Everything that happens after an auction was created: feedback message, sound, announcements and the Discord message.
// Shared by the /ah sell + /ah bid commands and the sell menu.
public class ListingAnnouncer {

    public static void afterListing(Player p, ItemNote note, ItemStack inputItem, double price, boolean isBIDAuction, int amount) {
        p.sendMessage(M.getFormatted("command-feedback.auction", price));
        Sounds.createAuction(p);

        // Announce the new auction to all players who have announcements enabled
        String itemName = note.getItemName();
        if (SettingManager.auctionAnnouncementsEnabled) {
            String announcement = M.getFormatted(
                    isBIDAuction ? "chat.bid-announcement" : "chat.auction-announcement",
                    price,
                    "%player%", M.formatPlayer(p.getDisplayName(), p.getUniqueId()),
                    "%item%", itemName,
                    "%amount%", String.valueOf(amount));
            if (SettingManager.auctionSetupTime == 0) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (ConfigManager.playerPreferences.hasAnnouncementsEnabled(onlinePlayer.getUniqueId()) && !onlinePlayer.equals(p)) {
                        onlinePlayer.sendMessage(announcement);
                    }
                }
            } else {
                AuctionHouse.getScheduler().globalRegionalScheduler().runDelayed(() -> { //delay has to be > 0 in Folia
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (ConfigManager.playerPreferences.hasAnnouncementsEnabled(onlinePlayer.getUniqueId()) && !onlinePlayer.equals(p)) {
                            onlinePlayer.sendMessage(announcement);
                        }
                    }
                }, SettingManager.auctionSetupTime * 20);
            }
        }

        //Discord SRV
        String key = isBIDAuction ? "discord-srv.new-bid-auction-title" : "discord-srv.new-bin-auction-title";
        String title = Pattern.compile("§.").splitAsStream(M.getFormatted(key,
                price,
                "%player%", M.formatPlayer(p.getDisplayName(), p.getUniqueId()),
                "%item%", itemName,
                "%amount%", String.valueOf(amount))).collect(Collectors.joining());
        DiscordSRVHook.sendAuctionListingToDiscord(p, inputItem, title);
    }

}
