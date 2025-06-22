package me.elaineqheart.auctionHouse.commands;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.ah.CustomConfigBannedPlayers;
import me.elaineqheart.auctionHouse.ah.ItemNoteStorageUtil;
import me.elaineqheart.auctionHouse.ah.SettingManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        try {
            ItemNoteStorageUtil.loadNotes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CustomConfigBannedPlayers.reload();
        AuctionHouse.getPlugin().reloadConfig();
        SettingManager.loadData();

        if(commandSender instanceof Player p ) p.sendMessage(ChatColor.YELLOW + "The auction house plugin has reloaded.");
        AuctionHouse.getPlugin().getLogger().info("reloaded");

        return true;
    }
}
