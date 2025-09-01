package me.elaineqheart.auctionHouse.world.displays;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.Messages;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.items.ItemNote;
import me.elaineqheart.auctionHouse.data.items.ItemNoteStorageUtil;
import me.elaineqheart.auctionHouse.data.SettingManager;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateDisplay implements Runnable{
    @Override
    public void run() {
        for(Integer display : displays.keySet()) {
            DisplayNote data = displays.get(display);
            Location loc = data.location;
            if(data.glassBlock == null){
                retrieveData(loc, data);
                continue; //skip unloaded displays
            }
            if(data.glassBlock.isDead()) {
                continue;
            }
            int rank = data.glassBlock.getPersistentDataContainer().get(new NamespacedKey(AuctionHouse.getPlugin(), data.type), PersistentDataType.INTEGER);

            ItemNote note = getNote(data.type, rank); //need to check if note == null

            if(!loc.getBlock().getType().equals(Material.CHISELED_TUFF_BRICKS)) CreateDisplay.placeBlocks(loc);

            //Signs
            Location signLoc = loc.clone();
            Sign east, west, north, south;
            try {
                east = (Sign) signLoc.add(1,0,0).getBlock().getState();
                west = (Sign) signLoc.add(-2,0,0).getBlock().getState();
                north = (Sign) signLoc.add(1,0,-1).getBlock().getState();
                south = (Sign) signLoc.add(0,0,2).getBlock().getState();
            } catch (ClassCastException e) {
                CreateDisplay.placeBlocks(loc);
                continue; //skip if signs are not signs
            }
            Sign[] signs = {east, west, north, south};

            if(note == null) {
                for(Sign sign : signs) {
                    sign.getSide(Side.FRONT).setLine(0, "");
                    sign.getSide(Side.FRONT).setLine(1, "");
                    sign.getSide(Side.FRONT).setLine(3, "");
                    sign.update(true, false); //force = set block type to sign if it's not; applyPhysics = make a block update to surrounding blocks
                }
                if(data.text != null) data.text.remove();
                if(data.itemEntity != null) {
                    data.itemEntity.remove();
                    data.itemStack = null;
                }
                continue;
            }

            String price = note.getPriceTrimmed();
            String time = StringUtils.getTimeTrimmed(note.timeLeft());
            String playerName = note.getPlayerName();
            ItemStack item = note.getItem();

            //update the signs

            for(Sign sign : signs) {
                sign.getSide(Side.FRONT).setLine(0, ChatColor.GOLD + price + SettingManager.currencySymbol);
                sign.getSide(Side.FRONT).setLine(1, ChatColor.YELLOW + time);
                sign.getSide(Side.FRONT).setLine(3, Messages.getFormatted("world.displays.sign-interaction"));
                sign.update(true, false);
            }

            //update the item
            World world = loc.getWorld();
            assert world != null;
            if(data.itemEntity == null || data.itemEntity.isDead() || data.itemStack == null || data.itemStack.getType() != item.getType()) {
                //if the item entity is null or the item is different, create a new item entity
                if(data.itemEntity != null) {
                    data.itemEntity.remove(); //remove the old item entity
                }
                data.itemEntity = (Item) world.spawnEntity(loc.clone().add(0.5,1,0.5), EntityType.ITEM);
                data.itemEntity.setItemStack(item);
                data.itemEntity.setPickupDelay(32767); //will never decay
                data.itemEntity.setUnlimitedLifetime(true);
                data.itemEntity.setInvulnerable(true);
                data.itemEntity.setVelocity(new Vector(0,0,0)); //stop the motion of the item
                data.itemEntity.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getPlugin(), "display_item"), PersistentDataType.BOOLEAN,true);
                data.itemStack = item; //update the item stack in the display data
            } else if (data.itemEntity.getLocation().distance(loc.clone().add(0.5,1,0.5)) > 0.1) {
                //if the item entity is too far away, teleport it to the correct location
                data.itemEntity.teleport(loc.clone().add(0.5,1,0.5));
            }

            //get the item name
            String name = data.itemEntity.getName();
            if(item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) name = ChatColor.ITALIC + item.getItemMeta().getDisplayName();
            //update the text display
            if(!data.reloaded) {
                if (data.text == null || !data.itemName.equals(name) || !data.playerName.equals(playerName)) {
                    if(data.text != null) {
                        data.text.remove(); //remove the old text display
                    }
                    data.text = (TextDisplay) world.spawnEntity(loc.clone().add(0.5, 1.9, 0.5), EntityType.TEXT_DISPLAY);
                    data.text.setVisibleByDefault(true);
                    if(data.type.equals("highest_price")) {
                        data.text.setText(ChatColor.YELLOW + "#" + rank + " " + ChatColor.RESET + name + ChatColor.GRAY + "\n" +
                                Messages.getFormatted("world.displays.by-player") + playerName);
                    } else if(data.type.equals("ending_soon")) {
                        data.text.setText(ChatColor.GREEN + "#" + rank + " " + ChatColor.RESET + name + ChatColor.GRAY + "\n" +
                                Messages.getFormatted("world.displays.by-player")+ playerName);
                    }

                    data.text.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getPlugin(), "display_text"), PersistentDataType.BOOLEAN, true);
                    data.text.setAlignment(TextDisplay.TextAlignment.CENTER);
                    data.text.setBillboard(Display.Billboard.CENTER);
                }
            } else {
                data.reloaded = false; //reset the reloaded flag
            }
            data.itemName = name; //update
            data.playerName = playerName; //update

        }
    }

    public static final HashMap<Integer, DisplayNote> displays = new HashMap<>();
    public static final HashMap<Location, Integer> locations = new HashMap<>();
    public static final ConfigurationSection ymlData = ConfigManager.displays.get().getConfigurationSection("displays");

    public static void init() {
        reload();
        TaskManager.addTaskID(UUID.randomUUID(),Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), new UpdateDisplay(), 0, 20).getTaskId());
    }
    public static void reload() {
        for(String key : ymlData.getKeys(false)) { //find the data for each display
            Location loc = ymlData.getLocation(key);
            assert loc != null;
            DisplayNote data = new DisplayNote();
            data.location = loc;
            //get the block display
            retrieveData(loc,data);
            locations.put(loc, Integer.parseInt(key));
            displays.put(Integer.parseInt(key), data);
        }
    }

    private static void retrieveData(Location loc, DisplayNote data) {
        BlockDisplay entity = null;
        Item itemEntity = null;
        TextDisplay text = null;
        assert loc.getWorld() != null;
        for(Entity test : loc.getWorld().getNearbyEntities(loc,1,1,1)) {
            if(isDisplayGlass(test)) entity = (BlockDisplay) test;
        }
        if (entity != null) {
            data.glassBlock = entity;
            data.type = getType(entity);
        } else { return; } //if no glass block display found, return
        for(Entity test : loc.getWorld().getNearbyEntities(loc.clone().add(0.5,0.5,0.5),1,1,1)) {
            if(isDisplayItem(test)) itemEntity = (Item) test;
        }
        if (itemEntity != null) {
            data.itemEntity = itemEntity;
            data.itemStack = itemEntity.getItemStack();
        }
        for(Entity test : loc.getWorld().getNearbyEntities(loc.clone().add(0.5,1.9,0.5),1,1,1)) {
            if(isTextDisplay(test)) text = (TextDisplay) test;
        }
        if(text != null) {
            data.text = text;
            data.itemName = "";
            data.playerName = "";
            data.reloaded = true;
        }
    }

    public static boolean isDisplayGlass(Entity entity) {
        if(entity instanceof BlockDisplay display) {
            return display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "highest_price"), PersistentDataType.INTEGER) ||
                   display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "ending_soon"), PersistentDataType.INTEGER);
        }
        return false;
    }
    public static boolean isDisplayInteraction(Entity entity) {
        if(entity instanceof Interaction interaction) {
            return interaction.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "rank"), PersistentDataType.INTEGER) &&
                   interaction.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "type"), PersistentDataType.STRING);
        }
        return false;
    }
    private static boolean isDisplayItem(Entity entity) {
        if(entity instanceof Item item) {
            return item.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "display_item"), PersistentDataType.BOOLEAN);
        }
        return false;
    }
    private static boolean isTextDisplay(Entity entity) {
        if(entity instanceof TextDisplay text) {
            return text.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "display_text"), PersistentDataType.BOOLEAN);
        }
        return false;
    }

    private static String getType(BlockDisplay display) {
        if(display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "highest_price"), PersistentDataType.INTEGER)) {
            return "highest_price";
        } else if(display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "ending_soon"), PersistentDataType.INTEGER)) {
            return "ending_soon";
        }
        return null;
    }

    public static ItemNote getNote(String type, int rank) {
        if(type.equals("highest_price")) {
            int size = ItemNoteStorageUtil.sortedHighestPrice().size();
            if(rank > size) return null;
            Map.Entry<ItemNote, Integer> entry = ItemNoteStorageUtil.sortedHighestPrice().entrySet().stream().skip(size-rank).findFirst().orElse(null);
            if(entry == null) return null;
            return entry.getKey();
        } else if (type.equals("ending_soon")) {
            Map.Entry<ItemNote, Long> entry = ItemNoteStorageUtil.sortedDateCreated().entrySet().stream().skip(rank-1).findFirst().orElse(null);
            if(entry == null) return null;
            return entry.getKey();
        }
        return null;
    }

    public static void removeDisplay(Location loc, boolean removeBlocks) {
        Integer displayID = locations.get(loc);
        if(removeBlocks) {
            loc.add(1, 0, 0).getBlock().setType(Material.AIR);
            loc.add(-2, 0, 0).getBlock().setType(Material.AIR);
            loc.add(1, 0, -1).getBlock().setType(Material.AIR);
            loc.add(0, 0, 2).getBlock().setType(Material.AIR);
            loc.add(0, 0, -1).getBlock().setType(Material.AIR);
        }
        if(displayID != null) {
            DisplayNote data = displays.get(displayID);
            retrieveData(loc,data);
            safeRemoveGlass(data.glassBlock);
            if(data.itemEntity != null) data.itemEntity.remove();
            if(data.text != null) data.text.remove();
            safeRemoveInteraction(loc);
            locations.remove(loc);
            displays.remove(displayID);
            ymlData.set(String.valueOf(displayID), null);
            ConfigManager.displays.save();
            reload();
        } else {
            AuctionHouse.getPlugin().getLogger().warning("Display at location " + loc + " not found. Failed to remove it.");
        }
    }

    public static void safeRemoveInteraction(Location loc) {
        assert loc.getWorld() != null;
        for(Entity interaction : loc.getWorld().getNearbyEntities(loc.clone().add(0.2,1,0.2),1,1,1)) {
            if(isDisplayInteraction(interaction)) {
                for(NamespacedKey key : interaction.getPersistentDataContainer().getKeys()) {
                    interaction.getPersistentDataContainer().remove(key);
                }
                interaction.remove();
            }
        }
    }

    public static void safeRemoveGlass(BlockDisplay glass) {
        if(glass != null) {
            for(NamespacedKey key : glass.getPersistentDataContainer().getKeys()) {
                glass.getPersistentDataContainer().remove(key);
            }
            glass.remove();
        }
    }

}
