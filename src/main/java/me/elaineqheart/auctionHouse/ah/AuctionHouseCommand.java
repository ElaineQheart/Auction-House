package me.elaineqheart.auctionHouse.ah;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
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
                if(CustomConfigBannedPlayers.checkIsBannedSendMessage(p)) {
                    return true;
                }
                AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(p), p);
            }
            if(strings.length==1 && strings[0].equals("sell")) {
                p.sendMessage(ChatColor.YELLOW + "Usage: /ah sell <price>");
            }
            if(strings.length==2 && strings[0].equals("sell")) {
                if(CustomConfigBannedPlayers.checkIsBannedSendMessage(p)) {
                    return true;
                }
                if(ItemNoteStorageUtil.numberOfAuctions(p) >= 10) {
                    p.sendMessage(ChatColor.YELLOW + "You can only have 10 auctions at a time");
                    return true;
                }
                ItemStack item = p.getInventory().getItemInMainHand();
                if(item.getType().equals(Material.AIR)){
                    p.sendMessage(ChatColor.YELLOW + "You need to hold an item in your hand to sell it");
                    return true;
                }
                int price;
                try{
                    price = Integer.parseInt(strings[1]);
                } catch (Exception e) {
                    try{
                        price = Integer.parseInt(strings[1].substring(0, strings[1].length()-1));
                        String suffix = strings[1].substring(strings[1].length()-1).toLowerCase();
                        switch (suffix) {
                            case "k":
                                price *= 1000;
                                break;
                            case "m":
                                price *= 1000000;
                                break;
                            default:
                                p.sendMessage("That is not a valid number");
                                return true;
                        }
                    } catch (Exception f) {
                        p.sendMessage("That is not a valid number");
                        return true;
                    }

                }
                if(price<=0){
                    p.sendMessage("That is not a valid price");
                    return true;
                }
                ItemNoteStorageUtil.createNote(p,item,price);
                item.setAmount(0);
                p.sendMessage(ChatColor.YELLOW + "You have put up an auction for " + ChatColor.GOLD + price + ChatColor.YELLOW + " coins");

            }
            // /ah admin
            if(p.hasPermission(Permissions.MODERATE) && strings.length > 0) {
                if(strings.length == 1 && strings[0].equals("admin")) {
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(0, AuctionHouseGUI.Sort.HIGHEST_PRICE, "", p, true), p);
                } else if (strings.length < 4 && strings[0].equals("ban")) {
                    p.sendMessage(ChatColor.YELLOW + "Usage: /ah ban <player> <time in days> <reason>");
                } else if (strings.length != 2 && strings[0].equals("pardon")) {
                    p.sendMessage(ChatColor.YELLOW + "Usage: /ah pardon <player>");
                    // /ah ban player:
                } else if (strings.length > 3 && strings[0].equals("ban")) {
                    Player targetPlayer = Bukkit.getPlayer(strings[1]);
                    if (targetPlayer==null) {
                        p.sendMessage(ChatColor.YELLOW + "That player is not online");
                        return true;
                    }
                    try {
                        int duration = Integer.parseInt(strings[2]);
                        if (duration <= 0) {
                            p.sendMessage(ChatColor.YELLOW + "That is not a valid amount of days");
                            return true;
                        }
                        //use a StringBuilder to get all arguments
                        StringBuilder reason = new StringBuilder();
                        for (int i = 3; i < strings.length; i++) {
                            reason.append(strings[i]);
                            if (i != strings.length - 1) {
                                reason.append(" ");
                            }
                        }
                        CustomConfigBannedPlayers.saveBannedPlayer(targetPlayer, duration, reason.toString());
                        p.sendMessage(ChatColor.AQUA + "-------------------------------------------------");
                        p.sendMessage(ChatColor.YELLOW + targetPlayer.getDisplayName() + " was banned from the ah for " + duration + " days.");
                        p.sendMessage("Reason: " + reason);
                        p.sendMessage(ChatColor.AQUA + "-------------------------------------------------");
                    } catch (Exception e) {
                        p.sendMessage(ChatColor.YELLOW + "That is not a valid duration");
                    }
                    // /ah pardon player:
                } else if (strings.length == 2 && strings[0].equals("pardon")) {
                    String input = strings[1];
                    ConfigurationSection section = CustomConfigBannedPlayers.get().getConfigurationSection("BannedPlayers");
                    if (section == null) {
                        p.sendMessage(ChatColor.YELLOW + "There are no players banned");
                        return true;
                    }
                    for(String key : section.getKeys(false)) {
                        String path = "BannedPlayers." + key + ".PlayerName";
                        String playerName = CustomConfigBannedPlayers.get().getString(path);
                        if (playerName == null) continue;
                        if (playerName.equals(input)) {
                            CustomConfigBannedPlayers.get().set("BannedPlayers." + key, null);
                            CustomConfigBannedPlayers.save();
                            p.sendMessage(ChatColor.AQUA + "-------------------------------------------------");
                            p.sendMessage(ChatColor.WHITE + "Unbanned " + playerName + " from the auction house");
                            p.sendMessage(ChatColor.AQUA + "-------------------------------------------------");
                            return true;
                        }
                    }
                    p.sendMessage("That player isn't banned from the auction house");

                }
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
            List<String> assetParams = new ArrayList<>(List.of(new String[]{"sell"}));
            if(commandSender.hasPermission(Permissions.MODERATE)) {
                assetParams.add("admin");
                assetParams.add("ban");
                assetParams.add("pardon");
            }
            for (String p : assetParams) {
                if (p.indexOf(strings[0]) == 0){
                    params.add(p);
                }
            }

        }
        if(strings.length == 2 && strings[0].equals("ban")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                params.add(p.getDisplayName());
            }
        }
        if(strings.length == 2 && strings[0].equals("pardon")) {
            ConfigurationSection section = CustomConfigBannedPlayers.get().getConfigurationSection("BannedPlayers");
            if (section != null) {
                for(String key : section.getKeys(false)) {
                    String path = "BannedPlayers." + key + ".PlayerName";
                    params.add(CustomConfigBannedPlayers.get().getString(path));
                }
            }
        }
        return params;
    }
}
