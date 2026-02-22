package me.elaineqheart.auctionHouse.world.displays;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.TaskManager;
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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class UpdateDisplay implements Runnable {
    @Override
    public void run() {
        for (Integer display : displays.keySet()) {
            DisplayNote data = displays.get(display);
            Location loc = data.location;
            if (data.glassBlock == null) {
                retrieveData(loc, data);
                continue; // skip unloaded displays
            }
            if (data.glassBlock.isDead()) {
                continue;
            }
            int rank = data.glassBlock.getPersistentDataContainer()
                    .get(new NamespacedKey(AuctionHouse.getPlugin(), data.type), PersistentDataType.INTEGER);

            if (!loc.getBlock().getBlockData().matches(SettingManager.getDisplayBase(data.type, rank)))
                CreateDisplay.placeBlocks(loc, rank, data.type);
            Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getPlugin(), () -> {
                ItemNote note = getNote(data.type, rank);

                Bukkit.getScheduler().runTask(AuctionHouse.getPlugin(), () -> {
                    Sign[] signs = getSigns(loc, rank, data.type);
                    if (signs == null)
                        return;

                    if (note == null) {
                        for (Sign sign : signs) {
                            sign.getSide(Side.FRONT).setLine(0, "");
                            sign.getSide(Side.FRONT).setLine(1, "");
                            sign.getSide(Side.FRONT).setLine(3, "");
                            sign.update(true, false); // force = set block type to sign if it's not; applyPhysics = make
                                                      // a block update to surrounding blocks
                        }
                        if (data.text != null)
                            data.text.remove();
                        if (data.text != null)
                            data.text.remove();
                        if (data.itemDisplay != null) {
                            data.itemDisplay.remove();
                            data.itemStack = null;
                        }
                        return;
                    }

                    String time = StringUtils.getTimeTrimmed(note.getTimeLeft());
                    String playerName = note.getPlayerName();
                    ItemStack item = note.getItem();

                    // update the signs
                    for (Sign sign : signs) {
                        sign.getSide(Side.FRONT).setLine(0,
                                M.getFormatted("world.displays.line-0", note.getPrice(), "%time%", time));
                        sign.getSide(Side.FRONT).setLine(1,
                                M.getFormatted("world.displays.line-1", note.getPrice(), "%time%", time));
                        sign.getSide(Side.FRONT).setLine(2,
                                M.getFormatted("world.displays.line-2", note.getPrice(), "%time%", time));
                        sign.getSide(Side.FRONT).setLine(3,
                                M.getFormatted("world.displays.line-3", note.getPrice(), "%time%", time));
                        sign.update(true, false);
                    }

                    // update the item
                    World world = loc.getWorld();
                    assert world != null;
                    if (data.itemDisplay == null || data.itemDisplay.isDead() || data.itemStack == null
                            || data.itemStack.getType() != item.getType()
                            || data.itemStack.getAmount() != item.getAmount()
                            || !Objects.equals(data.itemStack.getItemMeta(), item.getItemMeta())) {
                        // if the item entity is null or the item is different, create a new item entity
                        if (data.itemDisplay != null) {
                            data.itemDisplay.remove(); // remove the old item entity
                        }
                        // For 1.21 ItemDisplay rendering
                        data.itemDisplay = (ItemDisplay) world.spawnEntity(loc.clone().add(0.5, 1, 0.5),
                                EntityType.ITEM_DISPLAY);
                        data.itemDisplay.setItemStack(item);
                        data.itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GROUND);

                        // We use the same rotation as the block so it aligns, and emit max light
                        data.itemDisplay.setBrightness(new Display.Brightness(15, 15));

                        data.itemDisplay.getPersistentDataContainer().set(
                                new NamespacedKey(AuctionHouse.getPlugin(), "display_item"), PersistentDataType.BOOLEAN,
                                true);
                        data.itemStack = item; // update the item stack in the display data
                    } else if (data.itemDisplay.getLocation().distance(loc.clone().add(0.5, 1, 0.5)) > 0.1) {
                        // if the item entity is too far away, teleport it to the correct location
                        data.itemDisplay.teleport(loc.clone().add(0.5, 1, 0.5));
                    }

                    data.glassBlock.setBrightness(new Display.Brightness(15, 15));

                    // get the item name
                    String name = item.getType().name();
                    if (item.getItemMeta() != null && item.getItemMeta().hasDisplayName())
                        name = ChatColor.ITALIC + item.getItemMeta().getDisplayName();
                    // update the text display
                    if (!data.reloaded) {
                        if (data.text == null || !data.itemName.equals(name) || !data.playerName.equals(playerName)) {
                            if (data.text != null) {
                                data.text.remove(); // remove the old text display
                            }
                            data.text = (TextDisplay) world.spawnEntity(loc.clone().add(0.5, 1.9, 0.5),
                                    EntityType.TEXT_DISPLAY);
                            data.text.setVisibleByDefault(true);
                            if (data.type.equals("highest_price")) {
                                data.text.setText(ChatColor.YELLOW + "#" + rank + " " + ChatColor.RESET + name
                                        + ChatColor.GRAY + "\n" +
                                        M.getFormatted("world.displays.by-player", "%player%", playerName));
                            } else if (data.type.equals("ending_soon")) {
                                data.text.setText(ChatColor.GREEN + "#" + rank + " " + ChatColor.RESET + name
                                        + ChatColor.GRAY + "\n" +
                                        M.getFormatted("world.displays.by-player", "%player%", playerName));
                            }

                            data.text.getPersistentDataContainer().set(
                                    new NamespacedKey(AuctionHouse.getPlugin(), "display_text"),
                                    PersistentDataType.BOOLEAN, true);
                            data.text.setAlignment(TextDisplay.TextAlignment.CENTER);
                            data.text.setBillboard(Display.Billboard.CENTER);
                            data.text.setBrightness(new Display.Brightness(15, 15));
                        }
                    } else {
                        data.reloaded = false; // reset the reloaded flag
                    }
                    data.itemName = name; // update
                    data.playerName = playerName; // update
                });
            });

        }
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

    public static final HashMap<Integer, DisplayNote> displays = new HashMap<>();
    public static final HashMap<Location, Integer> locations = new HashMap<>();
    private static final ConfigurationSection ymlData = ConfigManager.displays.getCustomFile()
            .getConfigurationSection("displays");

    public static ConfigurationSection getYmlData() {
        if (ymlData != null)
            return ymlData;
        ConfigManager.displays.getCustomFile().createSection("displays");
        ConfigManager.displays.save();
        return ConfigManager.displays.getCustomFile().getConfigurationSection("displays");
    }

    public static void init() {
        reload();
        TaskManager.addTaskID(UUID.randomUUID(), Bukkit.getScheduler()
                .runTaskTimer(AuctionHouse.getPlugin(), new UpdateDisplay(), 0, SettingManager.displayUpdateTicks)
                .getTaskId());
    }

    public static void reload() {
        for (String key : getYmlData().getKeys(false)) { // find the data for each display
            Location loc = getYmlData().getLocation(key);
            assert loc != null;
            DisplayNote data = new DisplayNote();
            data.location = loc;
            // get the block display
            retrieveData(loc, data);
            if (data.itemDisplay != null)
                data.itemDisplay.remove();
            locations.put(loc, Integer.parseInt(key));
            displays.put(Integer.parseInt(key), data);
        }
    }

    private static void retrieveData(Location loc, DisplayNote data) {
        BlockDisplay entity = null;
        ItemDisplay itemDisplay = null;
        TextDisplay text = null;
        assert loc.getWorld() != null;
        for (Entity test : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
            if (isDisplayGlass(test))
                entity = (BlockDisplay) test;
        }
        if (entity != null) {
            data.glassBlock = entity;
            data.type = getType(entity);
        } else {
            return;
        } // if no glass block display found, return
        for (Entity test : loc.getWorld().getNearbyEntities(loc.clone().add(0.5, 0.5, 0.5), 1, 1, 1)) {
            if (isDisplayItem(test))
                itemDisplay = (ItemDisplay) test;
        }
        if (itemDisplay != null) {
            data.itemDisplay = itemDisplay;
            data.itemStack = itemDisplay.getItemStack();
        }
        for (Entity test : loc.getWorld().getNearbyEntities(loc.clone().add(0.5, 1.9, 0.5), 1, 1, 1)) {
            if (isTextDisplay(test))
                text = (TextDisplay) test;
        }
        if (text != null) {
            data.text = text;
            data.itemName = "";
            data.playerName = "";
            data.reloaded = true;
        }
    }

    public static boolean isDisplayGlass(Entity entity) {
        if (entity instanceof BlockDisplay display) {
            return display.getPersistentDataContainer()
                    .has(new NamespacedKey(AuctionHouse.getPlugin(), "highest_price"), PersistentDataType.INTEGER) ||
                    display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "ending_soon"),
                            PersistentDataType.INTEGER);
        }
        return false;
    }

    public static boolean isDisplayInteraction(Entity entity) {
        if (entity instanceof Interaction interaction) {
            return interaction.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "rank"),
                    PersistentDataType.INTEGER) &&
                    interaction.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "type"),
                            PersistentDataType.STRING);
        }
        return false;
    }

    private static boolean isDisplayItem(Entity entity) {
        if (entity instanceof ItemDisplay item) {
            return item.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "display_item"),
                    PersistentDataType.BOOLEAN);
        }
        return false;
    }

    private static boolean isTextDisplay(Entity entity) {
        if (entity instanceof TextDisplay text) {
            return text.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "display_text"),
                    PersistentDataType.BOOLEAN);
        }
        return false;
    }

    private static String getType(BlockDisplay display) {
        if (display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "highest_price"),
                PersistentDataType.INTEGER)) {
            return "highest_price";
        } else if (display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "ending_soon"),
                PersistentDataType.INTEGER)) {
            return "ending_soon";
        }
        return null;
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
        Integer displayID = locations.get(loc);
        if (displayID == null) {
            AuctionHouse.getPlugin().getLogger()
                    .warning("Display at location " + loc + " not found. Failed to remove it.");
            return;
        }
        DisplayNote data = displays.remove(displayID);
        locations.remove(loc);

        if (data == null) {
            data = new DisplayNote();
            data.location = loc;
            retrieveData(loc, data);
        } else if (data.glassBlock == null) {
            retrieveData(loc, data);
        }
        if (removeBlocks)
            removeBlocks(loc);
        ItemDisplay itemDisplay = data.itemDisplay;
        Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () -> {
            if (removeBlocks)
                removeBlocks(loc); // safety cleanup tick later
            if (itemDisplay != null)
                itemDisplay.remove();
        }, 5);

        safeRemoveGlass(data.glassBlock);
        if (data.text != null)
            data.text.remove();
        safeRemoveInteraction(loc);
        getYmlData().set(String.valueOf(displayID), null);
        ConfigManager.displays.save();
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

    public static void safeRemoveInteraction(Location loc) {
        assert loc.getWorld() != null;
        for (Entity interaction : loc.getWorld().getNearbyEntities(loc.clone().add(0.2, 1, 0.2), 1, 1, 1)) {
            if (isDisplayInteraction(interaction)) {
                for (NamespacedKey key : interaction.getPersistentDataContainer().getKeys()) {
                    interaction.getPersistentDataContainer().remove(key);
                }
                interaction.remove();
            }
        }
    }

    public static void safeRemoveGlass(BlockDisplay glass) {
        if (glass != null) {
            for (NamespacedKey key : glass.getPersistentDataContainer().getKeys()) {
                glass.getPersistentDataContainer().remove(key);
            }
            glass.remove();
        }
    }

}
