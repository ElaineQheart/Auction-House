package me.elaineqheart.auctionHouse.ah;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AuctionHouseCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player p){
            if(strings.length==0) {
                AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(p), p);
            }
            if(strings.length==1) {
                p.sendMessage(ChatColor.YELLOW + "Usage: /ah sell <price>");
            }
            if(strings.length==2) {
                if(ItemNoteStorageUtil.numberOfAuctions(p) >= 10) {
                    p.sendMessage(ChatColor.YELLOW + "You can only have 10 auctions at a time");
                    return true;
                }
                ItemStack item = p.getInventory().getItemInMainHand();
                if(item.getType().equals(Material.AIR)){
                    p.sendMessage(ChatColor.YELLOW + "You need to hold an item in your hand to sell it");
                    return true;
                }
                try{
                    Integer.parseInt(strings[1]);
                }catch (Exception e){
                    p.sendMessage("That is not a valid number");
                    return true;
                }
                int price = Integer.parseInt(strings[1]);
                if(price<=0){
                    p.sendMessage("That is not a valid price");
                    return true;
                }
                ItemNoteStorageUtil.createNote(p,item,price);
                item.setAmount(0);

            }

        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> params = new ArrayList<>();
        if(strings.length==1) {
            //check for every item if it's half typed out, then add accordingly to the params list
            List<String> assetParams = List.of(new String[]{"sell"});
            for (String p : assetParams) {
                if (p.indexOf(strings[0]) == 0){
                    params.add(p);
                }
            }

        }
        return params;
    }
}
