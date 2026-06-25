package me.elaineqheart.auctionHouse.world.displays;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.SettingManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.AuctionHouseStorage;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class UpdateDisplay implements Runnable {

    private static final AuctionHouse instance = AuctionHouse.getInstance();

    private static final HashMap<Integer, DisplayNote> displayItems = new HashMap<>();
    private static final Set<Location> locations = new HashSet<>();
    private static final Set<UUID> interactions = new HashSet<>();
    public static HashMap<Integer, DisplayNote> getDisplayItems() {return displayItems;}
    public static Set<Location> getLocations() {return locations;}
    public static Set<UUID> getInteractions() {return interactions;}

    public static void init() {
        reload(true);
        instance.getScheduler().globalRegionalScheduler().runAtFixedRate(new UpdateDisplay(), 10, SettingManager.displayUpdateTicks);
    }

    //TODO: split into entity and block modifications
    //https://github.com/papermc/folia
    //https://docs.papermc.io/paper/dev/folia-support/
    //https://docs.papermc.io/folia/reference/overview/
    @Override
    public void run() {
        for (Integer displayID : displayItems.keySet()) {

            DisplayNote data = displayItems.get(displayID);
            if (data == null) continue;

            AuctionHouse.getInstance().getScheduler().regionSpecificScheduler(data.location).run(() -> updateDisplay(displayID, data));
        }
    }

    private void updateDisplay(int displayID, DisplayNote data) {
        if (data.glassUUID == null) return;
        data.glassBlock = (BlockDisplay) Bukkit.getEntity(data.glassUUID);
        data.itemEntity = data.itemUUID == null ? null : (Item) Bukkit.getEntity(data.itemUUID);
        data.text = data.textUUID == null ? null : (TextDisplay) Bukkit.getEntity(data.textUUID);

        if (data.glassBlock == null || data.glassBlock.isDead()) return;

        ItemNote itemNote = getNote(data.sortType, data.rank);

        String playerName = null;
        ItemStack itemStack = null;
        String name = null;

        if (itemNote != null) {
            playerName = itemNote.getPlayerName();
            itemStack = itemNote.getItem();
            name = itemNote.getItemName();
            if (itemStack.getItemMeta() != null && itemStack.getItemMeta().hasDisplayName()) name = ChatColor.ITALIC + itemStack.getItemMeta().getDisplayName();
        }

        if (updateItemEntity(data, itemStack) | updateTextEntity(data, name, playerName)) {
            ConfigManager.displays.updateDisplay(displayID, data);
        }
        updateBlocks(data, itemNote);
    }

    private void updateBlocks(DisplayNote data, ItemNote note) {
        Sign[] signs = getSigns(data.location, data.rank, data.sortType);
        if (signs == null) {
            AuctionHouse.getInstance().getLogger().warning("Signs are null");
            return;
        }
        if (note == null) {
            for (Sign sign : signs) {
                sign.getSide(Side.FRONT).setLine(0, "");
                sign.getSide(Side.FRONT).setLine(1, "");
                sign.getSide(Side.FRONT).setLine(3, "");
                sign.update(true, false);

                // force = set block type to sign if it's not;
                // applyPhysics = make a block update to surrounding blocks
            }
            return;
        }

        String time = StringUtils.getTimeTrimmed(note.getTimeLeft());
        for (Sign sign : signs) {
            sign.getSide(Side.FRONT).setLine(0, M.getFormatted("world.displays.line-0", note.getPrice(), "%time%", time));
            sign.getSide(Side.FRONT).setLine(1, M.getFormatted("world.displays.line-1", note.getPrice(), "%time%", time));
            sign.getSide(Side.FRONT).setLine(2, M.getFormatted("world.displays.line-2", note.getPrice(), "%time%", time));
            sign.getSide(Side.FRONT).setLine(3, M.getFormatted("world.displays.line-3", note.getPrice(), "%time%", time));
            sign.update(true, false);
        }
    }

    private boolean updateItemEntity(DisplayNote data, ItemStack itemStack) { //true -> update DisplayNote
        if (itemStack == null) {
            if (data.itemEntity == null) return false;
            data.itemEntity.remove();
            data.itemUUID = null;
            return true;
        }

        boolean reload = false;
        if (data.itemEntity == null) {
            reload = true;
            if (data.location.getWorld() == null) return false;
            data.itemEntity = (Item) data.location.getWorld().spawnEntity(data.location.clone().add(0.5, 1, 0.5), EntityType.ITEM);
            data.itemEntity.setPickupDelay(32767);
            data.itemEntity.setUnlimitedLifetime(true); //will never decay
            data.itemEntity.setInvulnerable(true);
            data.itemEntity.setVelocity(new Vector(0,0,0)); //a spawned item always has a velocity
            data.itemEntity.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getInstance(), "display_item"),
                    PersistentDataType.BOOLEAN, true);
            data.itemUUID = data.itemEntity.getUniqueId();
        }

        data.itemEntity.setItemStack(itemStack);
        // if the item entity is too far away, teleport it to the correct location
        if (data.itemEntity.getLocation().distance(data.location.clone().add(0.5, 1, 0.5)) > 0.1) {
            data.itemEntity.teleport(data.location.clone().add(0.5, 1, 0.5));
        }
        return reload;
    }
    private boolean updateTextEntity(DisplayNote data, String itemName, String playerName) { // true -> update DisplayNote
        if (itemName == null) {
            if (data.text == null) return false;
            data.text.remove();
            data.textUUID = null;
            return true;
        }

        boolean reload = false;
        if (data.text == null) {
            reload = true;
            if (data.location.getWorld() == null) return false;
            data.text = (TextDisplay) data.location.getWorld().spawnEntity(data.location.clone().add(0.5, 1.9, 0.5), EntityType.TEXT_DISPLAY);
            data.text.setVisibleByDefault(true);
            data.text.getPersistentDataContainer().set(
                    new NamespacedKey(AuctionHouse.getInstance(), "display_text"), PersistentDataType.BOOLEAN, true);
            data.text.setAlignment(TextDisplay.TextAlignment.CENTER);
            data.text.setBillboard(Display.Billboard.CENTER);
            data.text.setBrightness(new Display.Brightness(15, 15));
            data.textUUID = data.text.getUniqueId();
        }

        if (data.sortType.equals("highest_price")) {
            data.text.setText(ChatColor.YELLOW + "#" + data.rank + " " + ChatColor.RESET + itemName + ChatColor.GRAY + "\n" +
                    M.getFormatted("world.displays.by-player", "%player%", playerName));
        } else if (data.sortType.equals("ending_soon")) {
            data.text.setText(ChatColor.GREEN + "#" + data.rank + " " + ChatColor.RESET + itemName + ChatColor.GRAY + "\n" +
                    M.getFormatted("world.displays.by-player", "%player%", playerName));
        }
        return reload;
    }

    private static Sign[] getSigns(Location loc, int rank, String sortType) {
        Location signLoc = loc.clone();
        Sign east, west, north, south;
        try {
            east = (Sign) signLoc.add(1, 0, 0).getBlock().getState();
            west = (Sign) signLoc.add(-2, 0, 0).getBlock().getState();
            north = (Sign) signLoc.add(1, 0, -1).getBlock().getState();
            south = (Sign) signLoc.add(0, 0, 2).getBlock().getState();
        } catch (ClassCastException e) {
            CreateDisplay.placeBlocks(loc, rank, sortType);
            return null;
        }
        return new Sign[] { east, west, north, south };
    }

    public static void reload(boolean justUpdateLists) {
        displayItems.clear();
        locations.clear();
        interactions.clear();
        displayItems.putAll(ConfigManager.displays.getNotes());
        locations.addAll(displayItems.values().stream().map(entry -> entry.location).collect(Collectors.toSet()));
        interactions.addAll(displayItems.values().stream().map(entry -> entry.interactionUUID).collect(Collectors.toSet()));
        if (justUpdateLists) return;
        for (DisplayNote data : displayItems.values()) {
            if (data == null) continue;
            if (!data.location.getBlock().getBlockData().matches(SettingManager.getDisplayBase(data.sortType, data.rank)))
                CreateDisplay.placeBlocks(data.location, data.rank, data.sortType);
        }
        new UpdateDisplay().run(); //reload
    }



    public static ItemNote getNote(String type, int rank) {
        if (type.equals("highest_price")) {
            return AuctionHouseStorage.getSortedList(ItemNoteStorage.SortMode.PRICE_DESC, new AhConfiguration())
                    .stream().skip(rank - 1).findFirst().orElse(null);
        } else if (type.equals("ending_soon")) {
            return AuctionHouseStorage.getSortedList(ItemNoteStorage.SortMode.DATE, new AhConfiguration()).stream()
                    .skip(rank - 1).findFirst().orElse(null);
        }
        return null;
    }

    public static void removeDisplay(Location loc, boolean removeBlocks) {
        Integer id = displayItems.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue().location, loc))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        if (id == null) {
            AuctionHouse.getInstance().getLogger().warning("Display at location " + loc + " not found. Failed to remove it.");
            return;
        }
        DisplayNote data = displayItems.remove(id);
        ConfigManager.displays.removeDisplay(id);

        if (data == null) {
            AuctionHouse.getInstance().getLogger().warning("Display at location " + loc + " is null.");
            return;
        }
        if (removeBlocks) removeBlocks(loc);
        if (data.itemUUID != null) {
            Item itemEntity = (Item) Bukkit.getEntity(data.itemUUID);
            if (itemEntity != null) itemEntity.remove();
        }
        if (data.glassUUID != null) {
            safeRemoveGlass((BlockDisplay) Bukkit.getEntity(data.glassUUID));
        }
        if (data.textUUID != null) {
            TextDisplay textDisplay = (TextDisplay) Bukkit.getEntity(data.textUUID);
            if (textDisplay != null) textDisplay.remove();
        }
        if (data.interactionUUID != null) {
            safeRemoveInteraction((Interaction) Bukkit.getEntity(data.interactionUUID));
        }
        reload(false);
    }

    private static void removeBlocks(Location loc) {
        Location cLoc = loc.clone();
        cLoc.getBlock().setType(Material.AIR);
        cLoc.add(1, 0, 0).getBlock().setType(Material.AIR);
        cLoc.add(-2, 0, 0).getBlock().setType(Material.AIR);
        cLoc.add(1, 0, -1).getBlock().setType(Material.AIR);
        cLoc.add(0, 0, 2).getBlock().setType(Material.AIR);
        cLoc.add(0, 0, -1).getBlock().setType(Material.AIR);
    }

    public static void safeRemoveInteraction(Interaction interaction) {
        if (interaction == null || interaction.isDead()) return;
        for (NamespacedKey key : interaction.getPersistentDataContainer().getKeys()) {
            interaction.getPersistentDataContainer().remove(key);
        }
        interaction.remove(); //entity remove event is called
    }

    public static void safeRemoveGlass(BlockDisplay glass) {
        if (glass == null || glass.isDead()) return;
        for (NamespacedKey key : glass.getPersistentDataContainer().getKeys()) {
            glass.getPersistentDataContainer().remove(key);
        }
        glass.remove(); //entity remove event is called
    }

}
