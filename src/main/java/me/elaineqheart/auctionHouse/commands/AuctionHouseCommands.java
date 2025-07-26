package me.elaineqheart.auctionHouse.commands;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.Permissions;
import me.elaineqheart.auctionHouse.ah.CustomConfigBannedPlayers;
import me.elaineqheart.auctionHouse.ah.ItemNoteStorageUtil;
import me.elaineqheart.auctionHouse.ah.Messages;
import me.elaineqheart.auctionHouse.ah.SettingManager;
import me.elaineqheart.auctionHouse.world.CreateDisplay;
import me.elaineqheart.auctionHouse.world.CreateNPC;
import me.elaineqheart.auctionHouse.world.UpdateDisplay;
import me.elaineqheart.auctionHouse.world.files.DisplaysConfig;
import org.bukkit.Bukkit;
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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AuctionHouseCommands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player p)){
            commandSender.sendMessage("This command can only be run by a player.");
            return true;
        }

        if(strings.length==0) {
            if(CustomConfigBannedPlayers.checkIsBannedSendMessage(p)) {
                return true;
            }
            AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(p), p);
            return true;
        }

        if(strings.length==1 && strings[0].equalsIgnoreCase("sell")) {
            p.sendMessage(Messages.get("usage.sell"));
            return true;
        }

        if(strings.length==2 && strings[0].equalsIgnoreCase("sell")) {
            if(CustomConfigBannedPlayers.checkIsBannedSendMessage(p)) {
                return true;
            }
            if(ItemNoteStorageUtil.numberOfAuctions(p) >= 10) {
                p.sendMessage(Messages.get("max-auctions"));
                return true;
            }
            ItemStack item = p.getInventory().getItemInMainHand();
            if(item.getType().equals(Material.AIR)){
                p.sendMessage(Messages.get("no-item-in-hand"));
                return true;
            }
            double price;
            try{
                price = Double.parseDouble(strings[1]);
            } catch (NumberFormatException e) {
                try {
                    String value = strings[1].substring(0, strings[1].length() - 1);
                    String suffix = strings[1].substring(strings[1].length() - 1).toLowerCase();
                    price = Double.parseDouble(value);
                    switch (suffix) {
                        case "k": price *= 1_000; break;
                        case "m": price *= 1_000_000; break;
                        default:
                            p.sendMessage(Messages.get("invalid-number"));
                            return true;
                    }
                } catch (Exception ex) {
                    p.sendMessage(Messages.get("invalid-number"));
                    return true;
                }
            }
            if(price <= 0){
                p.sendMessage(Messages.get("invalid-price"));
                return true;
            }
            ItemNoteStorageUtil.createNote(p,item.clone(), (int) price);
            item.setAmount(0);
            String formattedPrice = NumberFormat.getInstance(Locale.US).format(price);
            p.sendMessage(Messages.getFormatted("auction-created", "%price%", formattedPrice, "%currency%", SettingManager.currencySymbol));
            return true;
        }

        if(p.hasPermission(Permissions.MODERATE) && strings.length > 0) {
            String subCommand = strings[0].toLowerCase();
            switch (subCommand) {
                case "admin":
                    if (strings.length == 1) {
                        AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(0, AuctionHouseGUI.Sort.HIGHEST_PRICE, "", p, true), p);
                    }
                    break;
                case "ban":
                    if (strings.length < 4) {
                        p.sendMessage(Messages.get("usage.ban"));
                    } else {
                        Player targetPlayer = Bukkit.getPlayer(strings[1]);
                        if (targetPlayer == null) {
                            p.sendMessage(Messages.get("player-not-online"));
                            return true;
                        }
                        try {
                            int duration = Integer.parseInt(strings[2]);
                            if (duration <= 0) {
                                p.sendMessage(Messages.get("invalid-duration"));
                                return true;
                            }
                            StringBuilder reason = new StringBuilder();
                            for (int i = 3; i < strings.length; i++) {
                                reason.append(strings[i]).append(i == strings.length - 1 ? "" : " ");
                            }
                            CustomConfigBannedPlayers.banPlayer(targetPlayer, p, duration, reason.toString());
                            p.sendMessage(Messages.getFormatted("player-banned", "%player%", targetPlayer.getName(), "%time%", String.valueOf(duration), "%reason%", reason.toString()));
                        } catch (NumberFormatException e) {
                            p.sendMessage(Messages.get("invalid-duration"));
                        }
                    }
                    break;
                case "pardon":
                    if (strings.length != 2) {
                        p.sendMessage(Messages.get("usage.pardon"));
                    } else {
                        String targetName = strings[1];
                        if (CustomConfigBannedPlayers.unbanPlayer(targetName)) {
                            p.sendMessage(Messages.getFormatted("player-unbanned", "%player%", targetName));
                        } else {
                            p.sendMessage(Messages.get("player-not-banned"));
                        }
                    }
                    break;
                case "reload":
                    try {
                        ItemNoteStorageUtil.loadNotes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    AuctionHouse.getPlugin().reloadConfig();
                    SettingManager.loadData();
                    CustomConfigBannedPlayers.reload();
                    DisplaysConfig.reload();
                    UpdateDisplay.reload();
                    p.sendMessage(Messages.get("plugin-reloaded"));
                    break;
                case "reloadmessages":
                    Messages.reload();
                    p.sendMessage(Messages.get("messages-reloaded"));
                    break;
                case "summon":
                    if (strings.length < 2) {
                        p.sendMessage(Messages.get("usage.summon"));
                        return true;
                    }
                    Location loc = p.getLocation();
                    Location middleBlockLoc = new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5);
                    Location blockLoc = loc.getBlock().getLocation();

                    if (strings[1].equalsIgnoreCase("npc")) {
                        if (strings.length < 4 || !strings[2].equalsIgnoreCase("facing")) {
                            p.sendMessage(Messages.get("usage.summon-npc"));
                            return true;
                        }
                        CreateNPC.createAuctionMaster(middleBlockLoc, strings[3]);
                        p.sendMessage(Messages.get("npc-summoned"));
                    } else if (strings[1].equalsIgnoreCase("display")) {
                        if (strings.length < 4) {
                            p.sendMessage(Messages.get("usage.summon-display"));
                            return true;
                        }
                        int itemNumber;
                        try {
                            itemNumber = Integer.parseInt(strings[3]);
                        } catch (NumberFormatException e) {
                            p.sendMessage(Messages.get("invalid-number"));
                            return true;
                        }
                        if (itemNumber < 1) {
                            p.sendMessage(Messages.get("invalid-rank"));
                            return true;
                        }
                        for (Location displayLoc : UpdateDisplay.locations.keySet()) {
                            if (Objects.equals(blockLoc.getWorld(), displayLoc.getWorld()) && blockLoc.distance(displayLoc) < 1.5) {
                                p.sendMessage(Messages.get("display-exists"));
                                return true;
                            }
                        }
                        switch (strings[2].toLowerCase()) {
                            case "highest_price":
                                CreateDisplay.createDisplayHighestPrice(blockLoc, itemNumber);
                                p.sendMessage(Messages.get("display-summoned"));
                                break;
                            case "ending_soon":
                                CreateDisplay.createDisplayEndingSoon(blockLoc, itemNumber);
                                p.sendMessage(Messages.get("display-summoned"));
                                break;
                            default:
                                p.sendMessage(Messages.get("invalid-display-type"));
                                break;
                        }
                    }
                    break;
            }
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        String currentArg = args[args.length - 1].toLowerCase();

        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>(List.of("sell"));
            if (sender.hasPermission(Permissions.MODERATE)) {
                subCommands.addAll(List.of("admin", "ban", "pardon", "reload", "reloadmessages", "summon"));
            }
            for (String sub : subCommands) {
                if (sub.startsWith(currentArg)) {
                    completions.add(sub);
                }
            }
            return completions;
        }

        String subCommand = args[0].toLowerCase();
        if (sender.hasPermission(Permissions.MODERATE)) {
            switch (subCommand) {
                case "ban":
                    if (args.length == 2) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.getName().toLowerCase().startsWith(currentArg)) {
                                completions.add(p.getName());
                            }
                        }
                        return completions;
                    }
                    break;
                case "pardon":
                    if (args.length == 2) {
                        return CustomConfigBannedPlayers.getBannedPlayers();
                    }
                    break;
                case "summon":
                    if (args.length == 2) {
                        if ("npc".startsWith(currentArg)) completions.add("npc");
                        if ("display".startsWith(currentArg)) completions.add("display");
                    } else if (args.length == 3) {
                        if (args[1].equalsIgnoreCase("display")) {
                            if ("highest_price".startsWith(currentArg)) completions.add("highest_price");
                            if ("ending_soon".startsWith(currentArg)) completions.add("ending_soon");
                        } else if (args[1].equalsIgnoreCase("npc")) {
                            if ("facing".startsWith(currentArg)) completions.add("facing");
                        }
                    } else if (args.length == 4 && args[1].equalsIgnoreCase("npc") && args[2].equalsIgnoreCase("facing")) {
                        for (String dir : List.of("north", "south", "east", "west")) {
                            if (dir.startsWith(currentArg)) {
                                completions.add(dir);
                            }
                        }
                    }
                    break;
            }
        }
        return completions;
    }
}