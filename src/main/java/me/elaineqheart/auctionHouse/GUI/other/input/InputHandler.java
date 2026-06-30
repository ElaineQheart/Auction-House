package me.elaineqheart.auctionHouse.GUI.other.input;

import org.bukkit.entity.Player;

public interface InputHandler {

    void execute(Player p, String typedText);

    void onClose(Player p);

}
