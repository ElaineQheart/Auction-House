package me.elaineqheart.auctionHouse.commands;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.impl.CancelAuctionGUI;
import me.elaineqheart.auctionHouse.GUI.impl.CollectSoldItemGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.ram.AuctionHouseStorage;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.*;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.data.*;
import me.elaineqheart.auctionHouse.world.displays.CreateDisplay;
import me.elaineqheart.auctionHouse.world.displays.UpdateDisplay;
import me.elaineqheart.auctionHouse.world.npc.CreateNPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// https://github.com/VelixDevelopments/Imperat

// #don't try to fix what's not broken

public class AuctionHouseCommands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof ConsoleCommandSender) {
            if(strings.length == 1 && (strings[0].equals(Messages.getFormatted("commands.reload")))) {
                reload();
                AuctionHouse.getPlugin().getLogger().info("reloaded files");
                return true;
            }
        }

        if(commandSender instanceof Player p){
            if(strings.length==0) {
                if(BannedPlayersUtil.checkIsBannedSendMessage(p)) {
                    return true;
                }
                AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(p), p);
            }
            if(strings.length==1 && strings[0].equals(Messages.getFormatted("commands.sell"))) {
                p.sendMessage(Messages.getFormatted("command-feedback.usage"));
            }
            if((strings.length==2 || strings.length==3) && strings[0].equals(Messages.getFormatted("commands.sell"))) {
                if(BannedPlayersUtil.checkIsBannedSendMessage(p)) {
                    return true;
                }
                if(AuctionHouseStorage.getNumberOfAuctions(p) >= Permissions.getAuctionSlots(p)) {
                    p.sendMessage(Messages.getFormatted("command-feedback.reached-max-auctions",
                            "%limit%", String.valueOf(Permissions.getAuctionSlots(p))));
                    return true;
                }
                ItemStack item = p.getInventory().getItemInMainHand();
                if(item.getType().equals(Material.AIR)){
                    p.sendMessage(Messages.getFormatted("command-feedback.no-item-in-hand"));
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
                                p.sendMessage(Messages.getFormatted("command-feedback.invalid-number"));
                                return true;
                        }
                    } catch (Exception f) {
                        p.sendMessage(Messages.getFormatted("command-feedback.invalid-number"));
                        return true;
                    }

                }
                if(price<=0){
                    p.sendMessage(Messages.getFormatted("command-feedback.invalid-number2"));
                    return true;
                }
                int amount = item.getAmount();
                if(strings.length == 3) {
                    try {
                        amount = Integer.parseInt(strings[2]);
                        if(amount < 1 || amount > item.getAmount()) throw new RuntimeException();
                    } catch (Exception e) {
                        p.sendMessage(Messages.getFormatted("command-feedback.invalid-number7"));
                        return true;
                    }
                }
                if(Blacklist.isBlacklisted(item)) {
                    p.sendMessage(Messages.getFormatted("command-feedback.item-blacklisted"));
                    p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 0.1f);
                    return true;
                }
                ItemStack inputItem = item.clone();
                inputItem.setAmount(amount);
                ItemNoteStorage.createNote(p, inputItem, price);
                item.setAmount(item.getAmount() - amount);
                p.sendMessage(Messages.getFormatted("command-feedback.auction", "%price%", StringUtils.formatPrice(price)));
                
                // Announce the new auction to all players who have announcements enabled
                if(SettingManager.auctionAnnouncementsEnabled) {
                    String itemName = StringUtils.getItemName(inputItem, p.getWorld());
                    String announcement = Messages.getFormatted("chat.auction-announcement",
                            "%player%", p.getDisplayName(),
                            "%item%", itemName,
                            "%amount%", String.valueOf(amount),
                            "%price%", StringUtils.formatPrice(price));
                    for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if(PlayerPreferencesManager.hasAnnouncementsEnabled(onlinePlayer.getUniqueId()) && !onlinePlayer.equals(p)) {
                            onlinePlayer.sendMessage(announcement);
                        }
                    }
                }

            }
            // /ah announce - toggle announcements
            if(strings.length == 1 && SettingManager.auctionAnnouncementsEnabled && strings[0].equals(Messages.getFormatted("commands.announce"))) {
                boolean newState = PlayerPreferencesManager.toggleAnnouncements(p);
                if(newState) {
                    p.sendMessage(Messages.getFormatted("command-feedback.announcements-enabled"));
                } else {
                    p.sendMessage(Messages.getFormatted("command-feedback.announcements-disabled"));
                }
                return true;
            }
            if(strings.length == 2 && strings[0].equals("view")) {
                String noteId = strings[1];
                ItemNote note = AuctionHouseStorage.getNote(noteId);
                if(note == null
                    || !note.getPlayerUUID().equals(p.getUniqueId())
                    || note.getBuyerName() == null || note.getBuyerName().isEmpty()) return true;
                Sounds.click(p);
                AhConfiguration configuration = new AhConfiguration(0, AuctionHouseGUI.Sort.HIGHEST_PRICE, "", p, false);
                if(!note.isSold()) {
                    AuctionHouse.getGuiManager().openGUI(new CancelAuctionGUI(note, configuration), p);
                } else {
                    AuctionHouse.getGuiManager().openGUI(new CollectSoldItemGUI(note, configuration), p);
                }
            }
            // /ah admin
            if(p.hasPermission(SettingManager.permissionModerate) && strings.length > 0) {
                if(strings.length == 1 && strings[0].equals(Messages.getFormatted("commands.admin"))) {
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(0, AuctionHouseGUI.Sort.HIGHEST_PRICE, "", p, true), p);
                } else if (strings.length < 4 && strings[0].equals(Messages.getFormatted("commands.ban"))) {
                    p.sendMessage(Messages.getFormatted("command-feedback.ban-usage"));
                } else if (strings.length != 2 && strings[0].equals(Messages.getFormatted("commands.pardon"))) {
                    p.sendMessage(Messages.getFormatted("command-feedback.pardon-usage"));
                    // /ah ban player:
                } else if (strings.length > 3 && strings[0].equals(Messages.getFormatted("commands.ban"))) {
                    Player targetPlayer = Bukkit.getPlayer(strings[1]);
                    if (targetPlayer==null) {
                        p.sendMessage(Messages.getFormatted("command-feedback.player-not-found"));
                        return true;
                    }
                    try {
                        int duration = Integer.parseInt(strings[2]);
                        if (duration <= 0) {
                            p.sendMessage(Messages.getFormatted("command-feedback.invalid-number3"));
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
                        BannedPlayersUtil.saveBannedPlayer(targetPlayer, duration, reason.toString());
                        p.sendMessage(Messages.getFormatted("command-feedback.ban",
                                "%player%", targetPlayer.getDisplayName(),
                                "%duration%", String.valueOf(duration),
                                "%reason%", reason.toString()));
                    } catch (Exception e) {
                        p.sendMessage(Messages.getFormatted("command-feedback.invalid-number4"));
                    }
                    // /ah pardon player:
                } else if (strings.length == 2 && strings[0].equals(Messages.getFormatted("commands.pardon"))) {
                    String input = strings[1];
                    ConfigurationSection section = ConfigManager.bannedPlayers.get().getConfigurationSection("BannedPlayers");
                    if (section == null) {
                        p.sendMessage(Messages.getFormatted("command-feedback.no-banned-players"));
                        return true;
                    }
                    for(String key : section.getKeys(false)) {
                        String path = "BannedPlayers." + key + ".PlayerName";
                        String playerName = ConfigManager.bannedPlayers.get().getString(path);
                        if (playerName == null) continue;
                        if (playerName.equals(input)) {
                            ConfigManager.bannedPlayers.get().set("BannedPlayers." + key, null);
                            ConfigManager.bannedPlayers.save();
                            p.sendMessage(Messages.getFormatted("command-feedback.pardon",
                                    "%player%", playerName));
                            return true;
                        }
                    }
                    p.sendMessage(Messages.getFormatted("command-feedback.not-banned"));

                } else if (strings[0].equals(Messages.getFormatted("commands.reload"))) {
                    reload();
                    p.sendMessage(Messages.getFormatted("command-feedback.reload"));
                    AuctionHouse.getPlugin().getLogger().info("reloaded");
                    return true;

                } else if (strings[0].equals(Messages.getFormatted("commands.summon"))) {
                    if(strings.length < 2) {
                        p.sendMessage(Messages.getFormatted("command-feedback.summon-usage"));
                        return true;
                    }
                    //get the player location
                    Location loc = p.getLocation();
                    Location middleBlockLoc = new Location(loc.getWorld(), loc.getBlockX()+0.5, loc.getBlockY(), loc.getBlockZ()+0.5);
                    Location blockLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());


                    if(strings[1].equals(Messages.getFormatted("commands.npc"))) {
                        if(strings.length < 4) {
                            p.sendMessage(Messages.getFormatted("command-feedback.npc-usage"));
                            return true;
                        }
                        CreateNPC.createAuctionMaster(middleBlockLoc, strings[3]);
                    } else if(strings[1].equals(Messages.getFormatted("commands.display"))) {
                        if(strings.length < 4) {
                            p.sendMessage(Messages.getFormatted("command-feedback.display-usage"));
                            return true;
                        }

                        int itemNumber;
                        try {
                            itemNumber = Integer.parseInt(strings[3]);
                            if(itemNumber < 1) {
                                p.sendMessage(Messages.getFormatted("command-feedback.invalid-number5"));
                                return true;
                            }
                        } catch (NumberFormatException e) {
                            p.sendMessage(Messages.getFormatted("command-feedback.invalid-number6"));
                            return true;
                        }
                        for(Location displayLoc : UpdateDisplay.locations.keySet()) {
                            if(Objects.equals(blockLoc.getWorld(), displayLoc.getWorld()) && blockLoc.distance(displayLoc) < 2.1) {
                                p.sendMessage(Messages.getFormatted("command-feedback.no-space-for-display"));
                                return true;
                            }
                        }
                        if(strings[2].equals(Messages.getFormatted("commands.highest_price"))) {
                            CreateDisplay.createDisplayHighestPrice(blockLoc, itemNumber);
                        } else if (strings[2].equals(Messages.getFormatted("commands.ending_soon"))) {
                            CreateDisplay.createDisplayEndingSoon(blockLoc, itemNumber);
                        } else {
                            p.sendMessage(Messages.getFormatted("command-feedback.display-usage"));
                            return true;
                        }
                    }
                } else if (strings.length == 2 && strings[1].equals(Messages.getFormatted("commands.undo"))) {
                    if (Blacklist.undo()) {
                        p.sendMessage(Messages.getFormatted("command-feedback.blacklist-undo"));
                    } else {
                        p.sendMessage(Messages.getFormatted("command-feedback.blacklist-undo-error"));
                    }
                    return true;
                } else if (strings.length < 3 && strings[0].equals(Messages.getFormatted("commands.blacklist"))) {
                    p.sendMessage(Messages.getFormatted("command-feedback.blacklist-usage"));
                    return true;
                } else if (strings.length == 3 && strings[0].equals(Messages.getFormatted("commands.blacklist"))
                        && strings[1].equals(Messages.getFormatted("commands.add"))) {
                    if (strings[2].equals(Messages.getFormatted("commands.exact")) || strings[2].equals(Messages.getFormatted("commands.material"))) {
                        ItemStack item = p.getInventory().getItemInMainHand();
                        if (item.getType().equals(Material.AIR)) {
                            p.sendMessage(Messages.getFormatted("command-feedback.blacklist-no-item-in-hand"));
                            return true;
                        }
                        ItemMeta meta = item.getItemMeta();
                        assert meta != null;
                        if (strings[2].equals(Messages.getFormatted("commands.exact"))) {
                            Blacklist.addExact(item);
                        } else {
                            Blacklist.addMaterial(item.getType().toString());
                        }
                        p.sendMessage(Messages.getFormatted("command-feedback.blacklist-success", "%item%", item.getType().name()));
                        return true;
                    }
                    p.sendMessage(Messages.getFormatted("command-feedback.blacklist-usage"));
                    return true;
                } else if (strings.length == 4 && strings[0].equals(Messages.getFormatted("commands.blacklist"))
                    && strings[1].equals(Messages.getFormatted("commands.add"))) {

                    if (strings[2].equals(Messages.getFormatted("commands.exact")) || strings[2].equals(Messages.getFormatted("commands.material"))) return true;

                    if(strings[2].equals(Messages.getFormatted("commands.contains_lore"))) {
                        Blacklist.addLoreContains(strings[3]);
                    } else if (strings[2].equals(Messages.getFormatted("commands.name_contains"))) {
                        Blacklist.addNameContains(strings[3]);
                    }
                    p.sendMessage(Messages.getFormatted("command-feedback.blacklist-name-success", "%name%", strings[3]));
                    return true;
                } else if (strings.length == 2 && strings[0].equals(Messages.getFormatted("commands.test"))
                        && strings[1].equals(Messages.getFormatted("commands.save-item-to-layout-file"))) {
                    p.sendMessage(Messages.getFormatted("command-feedback.item-saved-to-layout-file"));
                    Layout.saveItem(p.getInventory().getItemInMainHand());
                    return true;
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
            List<String> assetParams = new ArrayList<>(List.of(new String[]{
                    Messages.getFormatted("commands.sell")
            }));
            if(SettingManager.auctionAnnouncementsEnabled) assetParams.add(Messages.getFormatted("commands.announce"));
            if(commandSender.hasPermission(SettingManager.permissionModerate)) {
                assetParams.add(Messages.getFormatted("commands.admin"));
                assetParams.add(Messages.getFormatted("commands.ban"));
                assetParams.add(Messages.getFormatted("commands.pardon"));
                assetParams.add(Messages.getFormatted("commands.reload"));
                assetParams.add(Messages.getFormatted("commands.summon"));
                assetParams.add(Messages.getFormatted("commands.blacklist"));
                assetParams.add(Messages.getFormatted("commands.test"));
            }
            for (String p : assetParams) {
                if (p.indexOf(strings[0]) == 0){
                    params.add(p);
                }
            }

        }
        if(strings.length == 2 && strings[0].equals(Messages.getFormatted("commands.ban"))) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                params.add(p.getDisplayName());
            }
        } else if (strings.length == 2 && strings[0].equals(Messages.getFormatted("commands.pardon"))) {
            ConfigurationSection section = ConfigManager.bannedPlayers.get().getConfigurationSection("BannedPlayers");
            if (section != null) {
                for(String key : section.getKeys(false)) {
                    String path = "BannedPlayers." + key + ".PlayerName";
                    params.add(ConfigManager.bannedPlayers.get().getString(path));
                }
            }
        } else if (strings.length == 2 && strings[0].equals(Messages.getFormatted("commands.summon"))) {
            List<String> summonTypes = new ArrayList<>(List.of(new String[]{Messages.getFormatted("commands.npc"),
                    Messages.getFormatted("commands.display")}));
            for (String p : summonTypes) {
                if (p.indexOf(strings[1]) == 0) {
                    params.add(p);
                }
            }
        } else if (strings.length == 2 && strings[0].equals(Messages.getFormatted("commands.blacklist"))) {
            List<String> summonTypes = new ArrayList<>(List.of(new String[]{Messages.getFormatted("commands.add"),
                    Messages.getFormatted("commands.undo")}));
            for (String p : summonTypes) {
                if (p.indexOf(strings[1]) == 0) {
                    params.add(p);
                }
            }
        } else if (strings.length == 3 && strings[0].equals(Messages.getFormatted("commands.summon")) && strings[1].equals(Messages.getFormatted("commands.display"))) {
            List<String> displayTypes = new ArrayList<>(List.of(new String[]{Messages.getFormatted("commands.highest_price"),
                    Messages.getFormatted("commands.ending_soon")}));
            for (String p : displayTypes) {
                if (p.indexOf(strings[2]) == 0){
                    params.add(p);
                }
            }
        } else if (strings.length == 3 && strings[0].equals(Messages.getFormatted("commands.summon")) && strings[1].equals(Messages.getFormatted("commands.npc"))) {
            List<String> displayTypes = new ArrayList<>(List.of(new String[]{Messages.getFormatted("commands.facing")}));
            for (String p : displayTypes) {
                if (p.indexOf(strings[2]) == 0) {
                    params.add(p);
                }
            }
        } else if (strings.length == 3 && strings[0].equals(Messages.getFormatted("commands.blacklist")) && strings[1].equals(Messages.getFormatted("commands.add"))) {
            List<String> displayTypes = new ArrayList<>(List.of(new String[]{Messages.getFormatted("commands.exact"),
                    Messages.getFormatted("commands.material"), Messages.getFormatted("commands.name_contains"),
                    Messages.getFormatted("commands.contains_lore")}));
            for (String p : displayTypes) {
                if (p.indexOf(strings[2]) == 0){
                    params.add(p);
                }
            }
        } else if (strings.length == 4 && strings[0].equals(Messages.getFormatted("commands.summon")) && strings[1].equals(Messages.getFormatted("commands.npc"))) {
            List<String> displayTypes = new ArrayList<>(List.of(new String[]{Messages.getFormatted("commands.north"), Messages.getFormatted("commands.east"),
                    Messages.getFormatted("commands.south"), Messages.getFormatted("commands.west")}));
            for (String p : displayTypes) {
                if (p.indexOf(strings[3]) == 0) {
                    params.add(p);
                }
            }
        } else if (strings.length == 2 && strings[0].equals(Messages.getFormatted("commands.test"))) {
            List<String> summonTypes = new ArrayList<>(List.of(new String[]{Messages.getFormatted("commands.save-item-to-layout-file")}));
            for (String p : summonTypes) {
                if (p.indexOf(strings[1]) == 0) {
                    params.add(p);
                }
            }
        }
        return params;
    }


    private static void reload() {
        ConfigManager.reloadConfigs();
        try {
            ItemNoteStorage.loadNotes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AuctionHouse.getPlugin().reloadConfig();
        SettingManager.loadData();
        UpdateDisplay.reload();
        Messages.reload();
        Layout.reload();
    }
}
