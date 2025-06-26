package me.elaineqheart.auctionHouse.commands;

import me.elaineqheart.auctionHouse.Permissions;
import me.elaineqheart.auctionHouse.world.CreateDisplay;
import me.elaineqheart.auctionHouse.world.CreateNPC;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SummonCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player p) {
            if(strings.length < 1) {
                return false;
            }

            //get the player location
            Location loc = p.getLocation();
            Location blockLoc = new Location(loc.getWorld(), loc.getBlockX()+0.5, loc.getBlockY(), loc.getBlockZ()+0.5);

            if(strings[0].equals("npc")) {
                CreateNPC.createAuctionMaster(blockLoc);
            } else if(strings[0].equals("display")) {
                if(strings.length < 3) {
                    p.sendMessage("/ahsummon display <type> <rank number>");
                    return true;
                }

                int itemNumber = 0;
                try {
                    itemNumber = Integer.parseInt(strings[2]);
                    if(itemNumber < 1) {
                        p.sendMessage("Item rank number must be greater than 0");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    p.sendMessage("Invalid item rank number. Please enter a valid number");
                    return true;
                }
                switch (strings[1]) {
                    case "highest_price":
                        CreateDisplay.createDisplayHighestPrice(blockLoc, itemNumber);
                        break;
                    case "ending_soon":
                        CreateDisplay.createDisplayEndingSoon(blockLoc, itemNumber);
                        break;
                    case "lowest_price":
                        CreateDisplay.createDisplayLowestPrice(blockLoc, itemNumber);
                        break;
                }
            }

        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> params = new ArrayList<>();
        if (strings.length==1) {
            //check for every item if it's half typed out, then add accordingly to the params list
            List<String> assetParams = new ArrayList<>(List.of(new String[]{"npc", "display"}));
            for (String p : assetParams) {
                if (p.indexOf(strings[0]) == 0){
                    params.add(p);
                }
            }

        } else if (strings.length==2 && strings[0].equals("display")) {
            //add the display types
            List<String> displayTypes = new ArrayList<>(List.of("highest_price", "ending_soon", "lowest_price"));
            for (String p : displayTypes) {
                if (p.indexOf(strings[1]) == 0){
                    params.add(p);
                }
            }
        }
        return params;
    }
}
