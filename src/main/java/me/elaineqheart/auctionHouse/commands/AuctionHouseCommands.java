package me.elaineqheart.auctionHouse.commands;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.impl.CollectSoldItemGUI;
import me.elaineqheart.auctionHouse.GUI.impl.MyAuctionsGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.Permissions;
import me.elaineqheart.auctionHouse.data.CustomConfigBannedPlayers;
import me.elaineqheart.auctionHouse.data.DisplaysConfig;
import me.elaineqheart.auctionHouse.data.SettingManager;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.items.ItemNote;
import me.elaineqheart.auctionHouse.data.items.ItemNoteStorageUtil;
import me.elaineqheart.auctionHouse.data.messages.Messages;
import me.elaineqheart.auctionHouse.data.messages.MessagesConfig;
import me.elaineqheart.auctionHouse.world.displays.CreateDisplay;
import me.elaineqheart.auctionHouse.world.displays.UpdateDisplay;
import me.elaineqheart.auctionHouse.world.npc.CreateNPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// https://github.com/VelixDevelopments/Imperat

public class AuctionHouseCommands implements CommandExecutor, TabCompleter {
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
                if(ItemNoteStorageUtil.numberOfAuctions(p) >= SettingManager.defaultMaxAuctions) {
                    p.sendMessage(ChatColor.YELLOW + "You can only have " + SettingManager.defaultMaxAuctions + " auctions at a time");
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
                p.sendMessage(ChatColor.YELLOW + "You have put up an auction for " + StringUtils.formatPrice(price,0));

            }
            if(strings.length == 2 && strings[0].equals("view")) {
                String noteId = strings[1];
                ItemNote note = ItemNoteStorageUtil.findNoteByID(noteId);
                if(note == null) return true;
                if(note == null) return true;
                Sounds.click(p);
                AuctionHouse.getGuiManager().openGUI(new CollectSoldItemGUI(note, MyAuctionsGUI.MySort.ALL_AUCTIONS), p);
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

                } else if (strings[0].equals("reload")) {
                    try {
                        ItemNoteStorageUtil.loadNotes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    CustomConfigBannedPlayers.reload();
                    AuctionHouse.getPlugin().reloadConfig();
                    SettingManager.loadData();
                    DisplaysConfig.reload();
                    UpdateDisplay.reload();
                    MessagesConfig.reload();

                    p.sendMessage(ChatColor.YELLOW + "The auction house plugin has reloaded.");
                    AuctionHouse.getPlugin().getLogger().info("reloaded");
                    return true;

                } else if (strings[0].equals("summon")) {
                    if(strings.length < 2) {
                        p.sendMessage("/ah summon <entity>");
                        return true;
                    }
                    //get the player location
                    Location loc = p.getLocation();
                    Location middleBlockLoc = new Location(loc.getWorld(), loc.getBlockX()+0.5, loc.getBlockY(), loc.getBlockZ()+0.5);
                    Location blockLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());


                    if(strings[1].equals("npc")) {
                        if(strings.length < 4) {
                            p.sendMessage("/ah summon npc facing <direcction>");
                            return true;
                        }
                        CreateNPC.createAuctionMaster(middleBlockLoc, strings[3]);
                    } else if(strings[1].equals("display")) {
                        if(strings.length < 4) {
                            p.sendMessage("/ahsummon display <type> <rank number>");
                            return true;
                        }

                        int itemNumber;
                        try {
                            itemNumber = Integer.parseInt(strings[3]);
                            if(itemNumber < 1) {
                                p.sendMessage("Item rank number must be greater than 0");
                                return true;
                            }
                        } catch (NumberFormatException e) {
                            p.sendMessage("Invalid item rank number. Please enter a valid number");
                            return true;
                        }
                        for(Location displayLoc : UpdateDisplay.locations.keySet()) {
                            if(Objects.equals(blockLoc.getWorld(), displayLoc.getWorld()) && blockLoc.distance(displayLoc) < 2.1) {
                                p.sendMessage(ChatColor.YELLOW + "There is already a display here. Please remove it first.");
                                return true;
                            }
                        }
                        switch (strings[2]) {
                            case "highest_price":
                                CreateDisplay.createDisplayHighestPrice(blockLoc, itemNumber);
                                break;
                            case "ending_soon":
                                CreateDisplay.createDisplayEndingSoon(blockLoc, itemNumber);
                                break;
                        }
                    }
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
                assetParams.add("reload");
                assetParams.add("summon");
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
        } else if (strings.length == 2 && strings[0].equals("pardon")) {
            ConfigurationSection section = CustomConfigBannedPlayers.get().getConfigurationSection("BannedPlayers");
            if (section != null) {
                for(String key : section.getKeys(false)) {
                    String path = "BannedPlayers." + key + ".PlayerName";
                    params.add(CustomConfigBannedPlayers.get().getString(path));
                }
            }
        } else if (strings.length == 2 && strings[0].equals("summon")) {
            List<String> summonTypes = new ArrayList<>(List.of(new String[]{"npc", "display"}));
            for (String p : summonTypes) {
                if (p.indexOf(strings[1]) == 0){
                    params.add(p);
                }
            }
        } else if (strings.length == 3 && strings[0].equals("summon") && strings[1].equals("display")) {
            List<String> displayTypes = new ArrayList<>(List.of(new String[]{"highest_price", "ending_soon"}));
            for (String p : displayTypes) {
                if (p.indexOf(strings[2]) == 0){
                    params.add(p);
                }
            }
        } else if (strings.length == 3 && strings[0].equals("summon") && strings[1].equals("npc")) {
            List<String> displayTypes = new ArrayList<>(List.of(new String[]{"facing"}));
            for (String p : displayTypes) {
                if (p.indexOf(strings[2]) == 0) {
                    params.add(p);
                }
            }
        } else if (strings.length == 4 && strings[0].equals("summon") && strings[1].equals("npc")) {
            List<String> displayTypes = new ArrayList<>(List.of(new String[]{"north", "south", "west", "east"}));
            for (String p : displayTypes) {
                if (p.indexOf(strings[3]) == 0) {
                    params.add(p);
                }
            }
        }
        return params;
    }
}
