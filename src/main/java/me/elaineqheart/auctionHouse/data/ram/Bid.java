package me.elaineqheart.auctionHouse.data.ram;

import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.SettingManager;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class Bid {

    private final UUID player;
    private final String playerName;
    private final Date date;
    private final double bid;

    public Bid(Player player, Date date, double bid) {
        this.player = player.getUniqueId();
        this.playerName = player.getDisplayName();
        this.date = date;
        this.bid = bid;
    }

    public UUID getPlayerID() {return player;}
    public String getPlayerName() {return playerName;}
    public double getPrice() {return bid;}
    public String getTimeAgo() {
        long seconds = (new Date().getTime() - date.getTime()) / 1000;
        return StringUtils.getTimeTrimmed(seconds);
    }
    public static double nextMinBid(double bid) {return Math.ceil(SettingManager.bidIncreaseRatio * bid + bid);}
}
