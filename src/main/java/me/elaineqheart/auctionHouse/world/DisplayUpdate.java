package me.elaineqheart.auctionHouse.world;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.ah.ItemNote;
import me.elaineqheart.auctionHouse.ah.ItemNoteStorageUtil;
import me.elaineqheart.auctionHouse.ah.SettingManager;
import me.elaineqheart.auctionHouse.world.files.CustomConfigDisplayLocations;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DisplayUpdate implements Runnable{
    @Override
    public void run() {
        for(Integer display : displays.keySet()) {
            DisplayData data = displays.get(display);
            Location loc = data.location;
            int rank = data.glassBlock.getPersistentDataContainer().get(new NamespacedKey(AuctionHouse.getPlugin(), data.type), PersistentDataType.INTEGER);

            ItemNote note;
            if(data.type.equals("highest_price")) {
                int size = ItemNoteStorageUtil.sortedHighestPrice().size();
                if(rank > size) continue;
                Map.Entry<ItemNote, Integer> entry = ItemNoteStorageUtil.sortedHighestPrice().entrySet().stream().skip(size-rank).findFirst().orElse(null);
                if(entry == null) continue;
                note = entry.getKey();
            } else if (data.type.equals("ending_soon")) {
                Map.Entry<ItemNote, Long> entry = ItemNoteStorageUtil.sortedDateCreated().entrySet().stream().skip(rank-1).findFirst().orElse(null);
                if(entry == null) continue;
                note = entry.getKey();
            } else {continue;}
            String price = note.getPriceTrimmed();
            String time = note.getTimeLeftTrimmed(note.timeLeft());
            String playerName = note.getPlayerName();
            ItemStack item = note.getItem();


            //update the signs
            Sign east = (Sign) loc.add(1,0,0).getBlock().getState();
            Sign west = (Sign) loc.add(-2,0,0).getBlock().getState();
            Sign north = (Sign) loc.add(1,0,-1).getBlock().getState();
            Sign south = (Sign) loc.add(0,0,2).getBlock().getState();
            loc.add(0,0,-1);
            Sign[] signs = {east, west, north, south};
            for(Sign sign : signs) {
                sign.getSide(Side.FRONT).setLine(0, ChatColor.GOLD + price + SettingManager.currencySymbol);
                sign.getSide(Side.FRONT).setLine(1, ChatColor.YELLOW + time);
                sign.getSide(Side.FRONT).setLine(3, ChatColor.LIGHT_PURPLE + "[CLICK]");
                sign.update();
            }

            //update the item
            World world = loc.getWorld();
            assert world != null;
            if(data.itemEntity == null || data.itemStack == null || data.itemStack.getType() != item.getType()) {
                //if the item entity is null or the item is different, create a new item entity
                if(data.itemEntity != null) {
                    data.itemEntity.remove(); //remove the old item entity
                }
                data.itemEntity = (Item) world.spawnEntity(loc.clone().add(0.5,1,0.5), EntityType.ITEM);
                data.itemEntity.setItemStack(item);
                data.itemEntity.setPickupDelay(32767);
                data.itemEntity.setUnlimitedLifetime(true);
                data.itemEntity.setInvulnerable(true);
                data.itemEntity.setVelocity(new Vector(0,0,0)); //stop the motion of the item
                data.itemEntity.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getPlugin(), "display_item"), PersistentDataType.BOOLEAN,true);
                data.itemStack = item; //update the item stack in the display data
            }

            //update the text display
            String name = data.itemEntity.getName();
            if(item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) name = ChatColor.ITALIC + item.getItemMeta().getDisplayName();
            if(!data.reloaded) {
                if (data.text == null || !data.itemName.equals(name) || !data.playerName.equals(playerName)) {
                    data.text = (TextDisplay) world.spawnEntity(loc.clone().add(0.5, 1.9, 0.5), EntityType.TEXT_DISPLAY);
                    data.text.setVisibleByDefault(true);
                    data.text.setText(ChatColor.YELLOW + "#" + rank + " " + ChatColor.WHITE + name + ChatColor.GRAY + "\nby: " + playerName);
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

    public static final HashMap<Integer, DisplayData> displays = new HashMap<>();
    public static final HashMap<Location, Integer> locations = new HashMap<>();

    public static void init() {
        reload();
        TaskManager.addTaskID(UUID.randomUUID(),Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), new DisplayUpdate(), 0, 20).getTaskId());
    }
    public static void reload() {
        for(Integer display : CustomConfigDisplayLocations.get().getKeys(false).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toSet())) { //find the data for each display
            DisplayData data = new DisplayData();
            Location loc = CustomConfigDisplayLocations.get().getLocation(String.valueOf(display));
            if (loc != null && loc.getWorld() != null) {
                data.location = loc;
                locations.put(loc, display);
                //get the block display
                BlockDisplay entity = null;
                Item itemEntity = null;
                TextDisplay text = null;
                for(Entity test : loc.getWorld().getNearbyEntities(loc.clone().add(0.5,0.5,0.5),1,1,1)) {
                    if(isDisplayGlass(test)) entity = (BlockDisplay) test;
                    if(isDisplayItem(test)) itemEntity = (Item) test;
                }
                for(Entity test : loc.getWorld().getNearbyEntities(loc.clone().add(0.5,1.9,0.5),1,1,1)) {
                    if(isTextDisplay(test)) text = (TextDisplay) test;
                }
                if (entity != null) {
                    data.glassBlock = entity;
                    data.type = getType(entity);
                }
//                else {
//                    AuctionHouse.getPlugin().getLogger().warning("Display entity display glass with ID " + display + " not found in world.");
//                }
                if (itemEntity != null) {
                    data.itemEntity = itemEntity;
                    data.itemStack = itemEntity.getItemStack();
                }
//                else {
//                    AuctionHouse.getPlugin().getLogger().warning("Display item entity with ID " + display + " not found in world.");
//                }
                if(text != null) {
                    data.text = text;
                    data.itemName = "";
                    data.playerName = "";
                    data.reloaded = true;
                }
//                else {
//                    AuctionHouse.getPlugin().getLogger().warning("Display text entity with ID " + display + " not found in world.");
//                }
                displays.put(display, data);
            } else {
                AuctionHouse.getPlugin().getLogger().warning("Display location for ID " + display + " is null.");
            }
        }
    }

    private static boolean isDisplayGlass(Entity entity) {
        if(entity instanceof BlockDisplay display) {
            return display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "highest_price"), PersistentDataType.INTEGER) ||
                   display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "ending_soon"), PersistentDataType.INTEGER);
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

    public static void removeDisplay(Location loc) {
        Integer display = locations.get(loc);
        loc.add(1,0,0).getBlock().setType(Material.AIR);
        loc.add(-2,0,0).getBlock().setType(Material.AIR);
        loc.add(1,0,-1).getBlock().setType(Material.AIR);
        loc.add(0,0,2).getBlock().setType(Material.AIR);
        loc.add(0,0,-1).getBlock().setType(Material.AIR);
        if(display != null) {
            DisplayData data = displays.get(display);
            locations.remove(loc);
            if(data.glassBlock != null) data.glassBlock.remove();
            if(data.itemEntity != null) data.itemEntity.remove();
            if(data.text != null) data.text.remove();
            displays.remove(display);
            CustomConfigDisplayLocations.get().set(String.valueOf(display), null);
            CustomConfigDisplayLocations.save();
            reload();
        } else {
            AuctionHouse.getPlugin().getLogger().warning("Display at location " + loc + " not found.");
        }
    }

}
