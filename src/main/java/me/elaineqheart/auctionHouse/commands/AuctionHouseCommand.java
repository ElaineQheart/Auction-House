package me.elaineqheart.auctionHouse.commands;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionViewGUI;
import me.elaineqheart.auctionHouse.GUI.impl.CancelAuctionGUI;
import me.elaineqheart.auctionHouse.GUI.impl.CollectSoldItemGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.SettingManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.Blacklist;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.AuctionHouseStorage;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import me.elaineqheart.auctionHouse.world.displays.CreateDisplay;
import me.elaineqheart.auctionHouse.world.displays.UpdateDisplay;
import me.elaineqheart.auctionHouse.world.npc.NPCManager;
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
import java.util.UUID;

// https://github.com/VelixDevelopments/Imperat

// #don't try to fix what's not broken

public class AuctionHouseCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof ConsoleCommandSender) {
            if(strings.length == 1 && (strings[0].equals(M.getFormatted("commands.reload")))) {
                reload();
                AuctionHouse.getPlugin().getLogger().info("reloaded files");
                return true;
            }
        }

        if(commandSender instanceof Player p){
            if(strings.length==0) {
                if(ConfigManager.bannedPlayers.checkIsBannedSendMessage(p)) {
                    return true;
                }
                AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(p), p);
            }
            if(strings.length==1 && strings[0].equals(M.getFormatted("commands.about"))) {
                p.sendMessage("§6> §7§l---------------[ §dAuction House§7§l ]---------------");
                p.sendMessage("§6> §7Made by:§6 ElaineQheart");
                p.sendMessage("§6> §7Plugin Version:§6 " + AuctionHouse.getPlugin().getDescription().getVersion());
                p.sendMessage("§6> §7Contact:§e§n https://discord.gg/ePTwfDK6AY");
                p.sendMessage("§6> §e§nhttps://www.spigotmc.org/threads/auction-house.690682/");
                p.sendMessage("§6>");
                //p.sendMessage("§6> §7You are told to be patient - so the thief has time to flee");
                //p.sendMessage("§6> §7The proletarians have nothing to lose but their chains");
                p.sendMessage("§6> §7§l---------------[ §dAuction House§7§l ]---------------");
            }
            if(strings.length==1 && strings[0].equals(M.getFormatted("commands.help"))) {
                p.sendMessage(M.getFormatted("command-feedback.help-prefix"));
                List<String> commands = Objects.requireNonNull(M.get().getConfigurationSection("command-feedback.help")).getKeys(false).stream().sorted().toList();
                for(String cm : commands) {
                    String message = M.getFormatted("command-feedback.help." + cm);
                    if(cm.equals(M.getFormatted("commands.sell")) && !SettingManager.BINAuctions) continue;
                    if(cm.equals(M.getFormatted("commands.bid")) && !SettingManager.BIDAuctions) continue;
                    if(cm.equals(M.getFormatted("commands.announce")) && !SettingManager.auctionAnnouncementsEnabled) continue;
                    if(adminCommands().contains(cm) && !p.hasPermission(SettingManager.permissionModerate)) continue;
                    p.sendMessage(message);
                }
            }
            if(strings.length==1 && strings[0].equals(M.getFormatted("commands.sell")) && SettingManager.BINAuctions) {
                p.sendMessage(M.getFormatted("command-feedback.usage"));
            }
            if(strings.length==1 && strings[0].equals(M.getFormatted("commands.bid")) && SettingManager.BIDAuctions) {
                p.sendMessage(M.getFormatted("command-feedback.bid-usage"));
            }
            if((strings.length==2 || strings.length==3) &&
                    (strings[0].equals(M.getFormatted("commands.sell")) && SettingManager.BINAuctions
                            || strings[0].equals(M.getFormatted("commands.bid")) && SettingManager.BIDAuctions)) {
                if(ConfigManager.bannedPlayers.checkIsBannedSendMessage(p)) {
                    return true;
                }
                if(AuctionHouseStorage.getNumberOfAuctions(p.getUniqueId()) >= ConfigManager.permissions.getAuctionSlots(p)) {
                    p.sendMessage(M.getFormatted("command-feedback.reached-max-auctions",
                            "%limit%", String.valueOf(ConfigManager.permissions.getAuctionSlots(p))));
                    return true;
                }
                ItemStack item = p.getInventory().getItemInMainHand();
                if(item.getType().equals(Material.AIR)){
                    p.sendMessage(M.getFormatted("command-feedback.no-item-in-hand"));
                    return true;
                }
                double price = StringUtils.parsePositiveNumber(strings[1]);
                if (price == -1) {
                    p.sendMessage(M.getFormatted("command-feedback.invalid-number"));
                    return true;
                }
                if (price == 0) {
                    p.sendMessage(M.getFormatted("command-feedback.invalid-number2"));
                    return true;
                }
                if (strings[0].equals(M.getFormatted("commands.sell")) && price < SettingManager.minBINPrice) {
                    p.sendMessage(M.getFormatted("command-feedback.min-bin", SettingManager.minBINPrice));
                    return true;
                } else if (strings[0].equals(M.getFormatted("commands.bid")) && price < SettingManager.minBIDPrice) {
                    p.sendMessage(M.getFormatted("command-feedback.min-bid", SettingManager.minBIDPrice));
                    return true;
                }
                int amount = item.getAmount();
                if(strings.length == 3) {
                    try {
                        amount = Integer.parseInt(strings[2]);
                        if(amount < 1 || amount > item.getAmount()) throw new RuntimeException();
                    } catch (Exception e) {
                        p.sendMessage(M.getFormatted("command-feedback.invalid-number7"));
                        return true;
                    }
                }
                if(Blacklist.isBlacklisted(item)) {
                    p.sendMessage(M.getFormatted("command-feedback.item-blacklisted"));
                    p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 0.1f);
                    return true;
                }
                ItemStack inputItem = item.clone();
                inputItem.setAmount(amount);
                ItemNoteStorage.createNote(p, inputItem, price, strings[0].equals(M.getFormatted("commands.bid")));
                item.setAmount(item.getAmount() - amount);
                p.sendMessage(M.getFormatted("command-feedback.auction", price));
                
                // Announce the new auction to all players who have announcements enabled
                if(SettingManager.auctionAnnouncementsEnabled) {
                    String itemName = StringUtils.getItemName(inputItem);
                    String announcement = M.getFormatted("chat.auction-announcement", price,
                            "%player%", M.formatPlayer(p.getDisplayName(), p.getUniqueId()),
                            "%item%", itemName,
                            "%amount%", String.valueOf(amount));
                    Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () -> {
                        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if(ConfigManager.playerPreferences.hasAnnouncementsEnabled(onlinePlayer.getUniqueId()) && !onlinePlayer.equals(p)) {
                                onlinePlayer.sendMessage(announcement);
                            }
                        }
                    }, SettingManager.auctionSetupTime * 20);
                }

            }
            // /ah announce - toggle announcements
            if(strings.length == 1 && SettingManager.auctionAnnouncementsEnabled && strings[0].equals(M.getFormatted("commands.announce"))) {
                boolean newState = ConfigManager.playerPreferences.toggleAnnouncements(p);
                if(newState) {
                    p.sendMessage(M.getFormatted("command-feedback.announcements-enabled"));
                } else {
                    p.sendMessage(M.getFormatted("command-feedback.announcements-disabled"));
                }
                return true;
            }
            if(strings.length == 2 && strings[0].equals("view")) {
                ItemNote note = AuctionHouseStorage.getNote(UUID.fromString(strings[1]));
                if(note == null
                    || !note.getPlayerUUID().equals(p.getUniqueId()) && !note.isOnAuction()
                    || note.getPlayerUUID().equals(p.getUniqueId()) && (note.getBuyerName() == null || note.getBuyerName().isEmpty())) return true;
                Sounds.click(p);
                AhConfiguration configuration = AhConfiguration.getInstance(p).setPlayer(p.getUniqueId());
                configuration.setShouldClose(true);
                if(!note.getPlayerUUID().equals(p.getUniqueId())) {
                    AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, configuration, 0, AhConfiguration.View.AUCTION_HOUSE), p);
                } else if(!note.isSold()) {
                    AuctionHouse.getGuiManager().openGUI(new CancelAuctionGUI(note, configuration), p);
                } else {
                    AuctionHouse.getGuiManager().openGUI(new CollectSoldItemGUI(note, configuration), p);
                }
            }
            // /ah admin
            if(p.hasPermission(SettingManager.permissionModerate) && strings.length > 0) {
                if(strings.length == 1 && strings[0].equals(M.getFormatted("commands.admin"))) {
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(0, AuctionHouseGUI.Sort.HIGHEST_PRICE, "", p, true), p);
                } else if (strings.length < 4 && strings[0].equals(M.getFormatted("commands.ban"))) {
                    p.sendMessage(M.getFormatted("command-feedback.ban-usage"));
                } else if (strings.length != 2 && strings[0].equals(M.getFormatted("commands.pardon"))) {
                    p.sendMessage(M.getFormatted("command-feedback.pardon-usage"));
                    // /ah ban player:
                } else if (strings.length > 3 && strings[0].equals(M.getFormatted("commands.ban"))) {
                    Player targetPlayer = Bukkit.getPlayer(strings[1]);
                    if (targetPlayer==null) {
                        p.sendMessage(M.getFormatted("command-feedback.player-not-found"));
                        return true;
                    }
                    try {
                        int duration = Integer.parseInt(strings[2]);
                        if (duration <= 0) {
                            p.sendMessage(M.getFormatted("command-feedback.invalid-number3"));
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
                        ConfigManager.bannedPlayers.saveBannedPlayer(targetPlayer, duration, reason.toString());
                        p.sendMessage(M.getFormatted("command-feedback.ban",
                                "%player%", M.formatPlayer(targetPlayer.getDisplayName(), targetPlayer.getUniqueId()),
                                "%duration%", String.valueOf(duration),
                                "%reason%", reason.toString()));
                    } catch (Exception e) {
                        p.sendMessage(M.getFormatted("command-feedback.invalid-number4"));
                    }
                    // /ah pardon player:
                } else if (strings.length == 2 && strings[0].equals(M.getFormatted("commands.pardon"))) {
                    String input = strings[1];
                    ConfigurationSection section = ConfigManager.bannedPlayers.getCustomFile().getConfigurationSection("BannedPlayers");
                    if (section == null) {
                        p.sendMessage(M.getFormatted("command-feedback.no-banned-players"));
                        return true;
                    }
                    for(String key : section.getKeys(false)) {
                        String path = "BannedPlayers." + key + ".PlayerName";
                        String playerName = ConfigManager.bannedPlayers.getCustomFile().getString(path);
                        if (playerName == null) continue;
                        if (playerName.equals(input)) {
                            ConfigManager.bannedPlayers.getCustomFile().set("BannedPlayers." + key, null);
                            ConfigManager.bannedPlayers.save();
                            p.sendMessage(M.getFormatted("command-feedback.pardon",
                                    "%player%", M.formatPlayer(playerName, UUID.fromString(key))));
                            return true;
                        }
                    }
                    p.sendMessage(M.getFormatted("command-feedback.not-banned"));

                } else if (strings[0].equals(M.getFormatted("commands.reload"))) {
                    reload();
                    p.sendMessage(M.getFormatted("command-feedback.reload"));
                    AuctionHouse.getPlugin().getLogger().info("reloaded");
                    return true;

                } else if (strings[0].equals(M.getFormatted("commands.summon"))) {
                    if(strings.length < 2) {
                        p.sendMessage(M.getFormatted("command-feedback.summon-usage"));
                        return true;
                    }
                    //get the player location
                    Location loc = p.getLocation();
                    Location middleBlockLoc = new Location(loc.getWorld(), loc.getBlockX()+0.5, loc.getBlockY(), loc.getBlockZ()+0.5);
                    Location blockLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());


                    if(strings[1].equals(M.getFormatted("commands.npc"))) {
                        if(strings.length < 4) {
                            p.sendMessage(M.getFormatted("command-feedback.npc-usage"));
                            return true;
                        }
                        NPCManager.createAuctionMaster(middleBlockLoc, strings[3]);
                    } else if(strings[1].equals(M.getFormatted("commands.display"))) {
                        if(strings.length < 4) {
                            p.sendMessage(M.getFormatted("command-feedback.display-usage"));
                            return true;
                        }

                        int itemNumber;
                        try {
                            itemNumber = Integer.parseInt(strings[3]);
                            if(itemNumber < 1) {
                                p.sendMessage(M.getFormatted("command-feedback.invalid-number5"));
                                return true;
                            }
                        } catch (NumberFormatException e) {
                            p.sendMessage(M.getFormatted("command-feedback.invalid-number6"));
                            return true;
                        }
                        for(Location displayLoc : UpdateDisplay.locations.keySet()) {
                            if(Objects.equals(blockLoc.getWorld(), displayLoc.getWorld()) && blockLoc.distance(displayLoc) < 2.1) {
                                p.sendMessage(M.getFormatted("command-feedback.no-space-for-display"));
                                return true;
                            }
                        }
                        if(CreateDisplay.notEnoughSpace(loc)) {
                            p.sendMessage(M.getFormatted("command-feedback.no-air-space-for-display"));
                            return true;
                        }
                        if(strings[2].equals(M.getFormatted("commands.highest_price"))) {
                                CreateDisplay.createDisplayHighestPrice(blockLoc, itemNumber);
                        } else if (strings[2].equals(M.getFormatted("commands.ending_soon"))) {
                            CreateDisplay.createDisplayEndingSoon(blockLoc, itemNumber);
                        } else {
                            p.sendMessage(M.getFormatted("command-feedback.display-usage"));
                            return true;
                        }
                    }
                } else if (strings.length == 2 && strings[1].equals(M.getFormatted("commands.undo"))) {
                    if (ConfigManager.blacklist.undo()) {
                        p.sendMessage(M.getFormatted("command-feedback.blacklist-undo"));
                    } else {
                        p.sendMessage(M.getFormatted("command-feedback.blacklist-undo-error"));
                    }
                    return true;
                } else if (strings.length < 3 && strings[0].equals(M.getFormatted("commands.blacklist"))) {
                    p.sendMessage(M.getFormatted("command-feedback.blacklist-usage"));
                    return true;
                } else if (strings.length == 3 && strings[0].equals(M.getFormatted("commands.blacklist"))
                        && strings[1].equals(M.getFormatted("commands.add"))) {
                     if (strings[2].equals(M.getFormatted("commands.all"))) {
                         ConfigManager.blacklist.addAll();
                        p.sendMessage(M.getFormatted("command-feedback.blacklist-all"));
                        return true;
                    }
                    if (strings[2].equals(M.getFormatted("commands.exact")) || strings[2].equals(M.getFormatted("commands.material"))
                            || strings[2].equals(M.getFormatted("commands.item_model"))) {
                        ItemStack item = p.getInventory().getItemInMainHand();
                        if (item.getType().equals(Material.AIR)) {
                            p.sendMessage(M.getFormatted("command-feedback.blacklist-no-item-in-hand"));
                            return true;
                        }
                        ItemMeta meta = item.getItemMeta();
                        assert meta != null;
                        if (strings[2].equals(M.getFormatted("commands.exact"))) {
                            ConfigManager.blacklist.addExact(item);
                        } else if (strings[2].equals(M.getFormatted("commands.material"))){
                            ConfigManager.blacklist.addMaterial(item.getType().toString());
                        } else if (strings[2].equals(M.getFormatted("commands.item_model"))) {
                            if(item.getItemMeta().getItemModel() == null) {
                                p.sendMessage(M.getFormatted("command-feedback.blacklist-no-model"));
                                return true;
                            }
                            else ConfigManager.blacklist.addItemModel(item.getItemMeta().getItemModel().getKey());
                            p.sendMessage(M.getFormatted("command-feedback.blacklist-name-success", "%name%",
                                    item.getItemMeta().getItemModel().getKey()));
                            return true;
                        }
                        p.sendMessage(M.getFormatted("command-feedback.blacklist-success", "%item%", item.getType().name()));
                        return true;
                    }
                    p.sendMessage(M.getFormatted("command-feedback.blacklist-usage"));
                    return true;
                } else if (strings.length == 4 && strings[0].equals(M.getFormatted("commands.blacklist"))
                    && strings[1].equals(M.getFormatted("commands.add"))) {

                    if (strings[2].equals(M.getFormatted("commands.exact")) || strings[2].equals(M.getFormatted("commands.material"))) return true;

                    if(strings[2].equals(M.getFormatted("commands.contains_lore"))) {
                        ConfigManager.blacklist.addLoreContains(strings[3]);
                    } else if (strings[2].equals(M.getFormatted("commands.name_contains"))) {
                        ConfigManager.blacklist.addNameContains(strings[3]);
                    } else if (strings[2].equals(M.getFormatted("commands.custom_model_data"))) {
                        ConfigManager.blacklist.addCustomModelData(strings[3]);
                    } else if (strings[2].equals(M.getFormatted("commands.item_model"))) {
                        ConfigManager.blacklist.addItemModel((strings[3]));
                    }
                    p.sendMessage(M.getFormatted("command-feedback.blacklist-name-success", "%name%", strings[3]));
                    return true;
                } else if (strings.length == 2 && strings[0].equals(M.getFormatted("commands.test"))
                        && strings[1].equals(M.getFormatted("commands.save-item-to-layout-file"))) {
                    p.sendMessage(M.getFormatted("command-feedback.item-saved-to-layout-file"));
                    ConfigManager.layout.saveItem(p.getInventory().getItemInMainHand());
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
            List<String> assetParams = new ArrayList<>();
            assetParams.add(M.getFormatted("commands.about"));
            assetParams.add(M.getFormatted("commands.help"));
            if(SettingManager.BINAuctions) assetParams.add(M.getFormatted("commands.sell"));
            if(SettingManager.BIDAuctions) assetParams.add(M.getFormatted("commands.bid"));
            if(SettingManager.auctionAnnouncementsEnabled) assetParams.add(M.getFormatted("commands.announce"));
            if(commandSender.hasPermission(SettingManager.permissionModerate)) assetParams.addAll(adminCommands());
            for (String p : assetParams) {
                if (p.indexOf(strings[0]) == 0){
                    params.add(p);
                }
            }

        }
        if(strings.length == 2 && strings[0].equals(M.getFormatted("commands.ban"))) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                params.add(p.getDisplayName());
            }
        } else if (strings.length == 2 && strings[0].equals(M.getFormatted("commands.pardon"))) {
            ConfigurationSection section = ConfigManager.bannedPlayers.getCustomFile().getConfigurationSection("BannedPlayers");
            if (section != null) {
                for(String key : section.getKeys(false)) {
                    String path = "BannedPlayers." + key + ".PlayerName";
                    params.add(ConfigManager.bannedPlayers.getCustomFile().getString(path));
                }
            }
        } else if (strings.length == 2 && strings[0].equals(M.getFormatted("commands.summon"))) {
            List<String> summonTypes = new ArrayList<>(List.of(new String[]{M.getFormatted("commands.npc"),
                    M.getFormatted("commands.display")}));
            for (String p : summonTypes) {
                if (p.indexOf(strings[1]) == 0) {
                    params.add(p);
                }
            }
        } else if (strings.length == 2 && strings[0].equals(M.getFormatted("commands.blacklist"))) {
            List<String> summonTypes = new ArrayList<>(List.of(new String[]{M.getFormatted("commands.add"),
                    M.getFormatted("commands.undo")}));
            for (String p : summonTypes) {
                if (p.indexOf(strings[1]) == 0) {
                    params.add(p);
                }
            }
        } else if (strings.length == 3 && strings[0].equals(M.getFormatted("commands.summon")) && strings[1].equals(M.getFormatted("commands.display"))) {
            List<String> displayTypes = new ArrayList<>(List.of(new String[]{M.getFormatted("commands.highest_price"),
                    M.getFormatted("commands.ending_soon")}));
            for (String p : displayTypes) {
                if (p.indexOf(strings[2]) == 0){
                    params.add(p);
                }
            }
        } else if (strings.length == 3 && strings[0].equals(M.getFormatted("commands.summon")) && strings[1].equals(M.getFormatted("commands.npc"))) {
            List<String> displayTypes = new ArrayList<>(List.of(new String[]{M.getFormatted("commands.facing")}));
            for (String p : displayTypes) {
                if (p.indexOf(strings[2]) == 0) {
                    params.add(p);
                }
            }
        } else if (strings.length == 3 && strings[0].equals(M.getFormatted("commands.blacklist")) && strings[1].equals(M.getFormatted("commands.add"))) {
            List<String> displayTypes = new ArrayList<>(List.of(new String[]{M.getFormatted("commands.exact"),
                    M.getFormatted("commands.material"), M.getFormatted("commands.name_contains"),
                    M.getFormatted("commands.contains_lore"), M.getFormatted("commands.item_model"),
                    M.getFormatted("commands.custom_model_data"), M.getFormatted("commands.all")}));
            for (String p : displayTypes) {
                if (p.indexOf(strings[2]) == 0){
                    params.add(p);
                }
            }
        } else if (strings.length == 4 && strings[0].equals(M.getFormatted("commands.summon")) && strings[1].equals(M.getFormatted("commands.npc"))) {
            List<String> displayTypes = new ArrayList<>(List.of(new String[]{M.getFormatted("commands.north"), M.getFormatted("commands.east"),
                    M.getFormatted("commands.south"), M.getFormatted("commands.west")}));
            for (String p : displayTypes) {
                if (p.indexOf(strings[3]) == 0) {
                    params.add(p);
                }
            }
        } else if (strings.length == 2 && strings[0].equals(M.getFormatted("commands.test"))) {
            List<String> summonTypes = new ArrayList<>(List.of(new String[]{M.getFormatted("commands.save-item-to-layout-file")}));
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
        SettingManager.loadData();
        UpdateDisplay.reload();
    }

    private static List<String> adminCommands() {
        List<String> commandsList = new ArrayList<>();
        commandsList.add(M.getFormatted("commands.admin"));
        commandsList.add(M.getFormatted("commands.ban"));
        commandsList.add(M.getFormatted("commands.pardon"));
        commandsList.add(M.getFormatted("commands.reload"));
        commandsList.add(M.getFormatted("commands.summon"));
        commandsList.add(M.getFormatted("commands.blacklist"));
        commandsList.add(M.getFormatted("commands.test"));
        return commandsList;
    }
}
