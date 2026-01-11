package me.elaineqheart.auctionHouse.GUI.other;

import org.bukkit.entity.Player;

public interface AnvilHandler {

    void execute(Player p, String typedText);

    void onClose(Player p);

}
