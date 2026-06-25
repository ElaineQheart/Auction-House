package me.elaineqheart.auctionHouse.data.persistentStorage.local.configs;

import com.google.gson.Gson;
import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.Config;
import me.elaineqheart.auctionHouse.world.displays.DisplayListener;
import me.elaineqheart.auctionHouse.world.displays.DisplayNote;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Displays extends Config {

    public void addDisplay(int id, DisplayNote note) {
        getYmlData().set(id + ".location", note.location);
        getYmlData().set(id + ".rank", note.rank);
        getYmlData().set(id + ".sortType", note.sortType);
        getYmlData().set(id + ".glassUUID", note.glassUUID.toString());
        getYmlData().set(id + ".interactionUUID", note.interactionUUID.toString());
        getYmlData().set(id + ".itemUUID", note.itemUUID == null ? null : note.itemUUID.toString());
        getYmlData().set(id + ".textUUID", note.textUUID == null ? null : note.textUUID.toString());
        save();
    }

    public void removeDisplay(int id) {
        getYmlData().set(String.valueOf(id), null);
        save();
    }

    public void updateDisplay(int id, DisplayNote note) {
        addDisplay(id, note);
    }

    private ConfigurationSection getYmlData() {
        ConfigurationSection ymlData = getCustomFile().getConfigurationSection("displays");
        if (ymlData != null) return ymlData;
        getCustomFile().createSection("displays");
        save();
        return getCustomFile().getConfigurationSection("displays");
    }

    public HashMap<Integer, DisplayNote> getNotes() {
        HashMap<Integer, DisplayNote> notes = new HashMap<>();
        for (String key : getYmlData().getKeys(false)) {
            int id = Integer.parseInt(key);
            DisplayNote note = getNote(key);
            if (note != null && note.location.getWorld() == null) {
                removeDisplay(id);
                save();
                continue;
            }
            notes.put(id, note);
        }
        return notes;
    }

    public DisplayNote getNote(String id) {
        if (getYmlData().contains(id + ".location")) {
            DisplayNote note = new DisplayNote();
            note.location = getYmlData().getLocation(id + ".location");
            note.rank = getYmlData().getInt(id + ".rank");
            note.sortType = getYmlData().getString(id + ".sortType");
            note.glassUUID = UUID.fromString(Objects.requireNonNull(getYmlData().getString(id + ".glassUUID")));
            note.interactionUUID = UUID.fromString(Objects.requireNonNull(getYmlData().getString(id + ".interactionUUID")));
            if (getYmlData().contains(id + ".itemUUID")) {
                String itemUUID = getYmlData().getString(id + ".itemUUID");
                note.itemUUID = itemUUID == null ? null : UUID.fromString(itemUUID);
            }
            if (getYmlData().contains(id + ".textUUID")) {
                String textUUID = getYmlData().getString(id + ".textUUID");
                note.textUUID = textUUID == null ? null : UUID.fromString(textUUID);
            }
            return note;
        }
        if (AuctionHouse.isFolia()) return null;

        Location loc = getYmlData().getLocation(id);
        if (loc != null) {
            DisplayNote note = retrieveDataBackwardsCompatibility(loc, Integer.parseInt(id));
            if (note != null) return note;
            note = new DisplayNote();
            note.location = loc;
            return note;
        }

        return null;
    }

    public void backwardsCompatibility() {
        Set<Integer> oldSet = null;
        FileConfiguration customFile = getCustomFile();
        try {
            // This method is for backwards compatibility
            oldSet = customFile.getKeys(false).stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
        } catch (NumberFormatException ignored) {}

        //This section of code is needed, even without backwards compatibility
        if (customFile.getConfigurationSection("displays") == null) {
            customFile.createSection("displays");
        }

        if(oldSet != null) {
            for (Integer displayID : oldSet) {
                Objects.requireNonNull(customFile.getConfigurationSection("displays")).set(String.valueOf(displayID), customFile.get(String.valueOf(displayID)));
                customFile.set(String.valueOf(displayID), null); // Remove the old key
            }
        }
        save();
    }

    private DisplayNote retrieveDataBackwardsCompatibility(Location loc, int id) {
        DisplayNote data = new DisplayNote();
        data.location = loc;
        BlockDisplay glass = null;
        Interaction interaction = null;
        Item itemEntity = null;
        TextDisplay text = null;
        assert loc.getWorld() != null;

        for (Entity glassTest : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
            if (isDisplayGlass(glassTest)) glass = (BlockDisplay) glassTest;
        }
        if (glass == null) return null;
        data.glassUUID = glass.getUniqueId();
        data.sortType = getType(glass);
        data.rank = getRank(glass, data.sortType);

        for (Entity interactionTest : loc.getWorld().getNearbyEntities(loc.clone().add(0.2, 1, 0.2), 1, 1, 1)) {
            if (DisplayListener.isDisplayInteraction(interactionTest)) interaction = (Interaction) interactionTest;
        }
        if (interaction == null) return null;
        data.interactionUUID = interaction.getUniqueId();

        for (Entity itemTest : loc.getWorld().getNearbyEntities(loc.clone().add(0.5, 0.5, 0.5), 1, 1, 1)) {
            if (isDisplayItem(itemTest)) itemEntity = (Item) itemTest;
        }
        if (itemEntity != null) {
            data.itemUUID = itemEntity.getUniqueId();
        }

        for (Entity TextTest : loc.getWorld().getNearbyEntities(loc.clone().add(0.5, 1.9, 0.5), 1, 1, 1)) {
            if (isTextDisplay(TextTest)) text = (TextDisplay) TextTest;
        }
        if (text != null) {
            data.textUUID = text.getUniqueId();
        }
        addDisplay(id, data);
        AuctionHouse.getInstance().getLogger().info("Successfully transferred old display data to the new system at location: " + loc);
        return data;
    }

    private static boolean isDisplayItem(Entity entity) {
        if (entity instanceof Item item) {
            return item.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getInstance(), "display_item"), PersistentDataType.BOOLEAN);
        }
        return false;
    }

    private static boolean isTextDisplay(Entity entity) {
        if (entity instanceof TextDisplay text) {
            return text.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getInstance(), "display_text"), PersistentDataType.BOOLEAN);
        }
        return false;
    }

    private static String getType(BlockDisplay entity) {
        if (entity.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getInstance(), "highest_price"), PersistentDataType.INTEGER)) return "highest_price";
        else if (entity.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getInstance(), "ending_soon"), PersistentDataType.INTEGER)) return "ending_soon";
        return null;
    }
    private static int getRank(BlockDisplay entity, String sortType) {
        return entity.getPersistentDataContainer()
                .get(new NamespacedKey(AuctionHouse.getInstance(), sortType), PersistentDataType.INTEGER);
    }

    public static boolean isDisplayGlass(Entity entity) {
        if (entity instanceof BlockDisplay display) {
            return display.getPersistentDataContainer()
                    .has(new NamespacedKey(AuctionHouse.getInstance(), "highest_price"), PersistentDataType.INTEGER) ||
                    display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getInstance(), "ending_soon"), PersistentDataType.INTEGER);
        }
        return false;
    }

}
