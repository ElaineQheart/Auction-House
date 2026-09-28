package me.elaineqheart.auctionHouse.data.persistentStorage.local;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LayoutGenerator {

    // bumped whenever a default layout changes, so the migration in Layout.setup() only runs once per server
    public static final int LAYOUT_VERSION = 2;

    // default ah-layout: category sidebar on the left (A = all, W = weapons & tools, R = armor, C = consumables, B = blocks)
    private static List<String> sidebarAhLayout() {
        return Arrays.asList(
                "A # # # # # # # #",
                "W . . . . . . . #",
                "R . . . . . . . #",
                "C . . . . . . . #",
                "B # # # # # # # #",
                "s o h p r n # f m");
    }
    private static List<String> oldAhLayout(String bottomRow) {
        return Arrays.asList(
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # # # # # # # #",
                bottomRow);
    }

    public static void generate(FileConfiguration c) {
        c.set("ah-layout", sidebarAhLayout());
        c.set("layout-version", LAYOUT_VERSION);
        c.set("my-ah-layout", Arrays.asList(
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # # # # # # # #",
                "b o # p r n # d i"));
        ItemStack fillerItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = fillerItem.getItemMeta();
        assert meta != null;
        meta.setHideTooltip(true);
        fillerItem.setItemMeta(meta);
        c.set("#", fillerItem);
        c.set("s", new ItemStack(Material.OAK_SIGN));
        c.set("active-search", new ItemStack(Material.SPRUCE_SIGN));
        c.set("o", new ItemStack(Material.HOPPER));
        c.set("p", new ItemStack(Material.ARROW));
        c.set("r", new ItemStack(Material.NETHER_STAR));
        c.set("n", new ItemStack(Material.ARROW));
        c.set("m", new ItemStack(Material.ENDER_CHEST));
        c.set("b", new ItemStack(Material.ARROW));
        c.set("i", new ItemStack(Material.PAPER));
        c.set("f", new ItemStack(Material.POWERED_RAIL));
        c.set("d", new ItemStack(Material.GOLDEN_CARROT));
        c.set("bin-filter-bin", new ItemStack(Material.GOLD_INGOT));
        c.set("bin-filter-auctions", new ItemStack(Material.GOLD_BLOCK));
        c.set("locked-slot", new ItemStack(Material.BARRIER));
        c.set("back-to-my-auctions", new ItemStack(Material.ARROW));
        c.set("anvil-search-paper", new ItemStack(Material.PAPER));
        c.set("cancel", new ItemStack(Material.RED_BANNER));
        c.set("collect-expired-item", new ItemStack(Material.RED_DYE));
        c.set("cancel-auction", new ItemStack(Material.RED_CONCRETE));
        c.set("command-block-info", new ItemStack(Material.STRUCTURE_BLOCK));
        c.set("admin-cancel-auction", new ItemStack(Material.RED_CONCRETE));
        c.set("admin-expire-auction", new ItemStack(Material.RED_DYE));
        c.set("confirm", new ItemStack(Material.GREEN_BANNER));
        c.set("choose-item-buy-amount", new ItemStack(Material.OAK_HANGING_SIGN));
        c.set("dirt", new ItemStack(Material.DIRT));
        c.set("turtle-scute-confirm", new ItemStack(Material.TURTLE_SCUTE));
        c.set("cannot-afford", new ItemStack(Material.ARMADILLO_SCUTE));
        c.set("collect-sold-item", new ItemStack(Material.DIAMOND));
        c.set("bid-history", new ItemStack(Material.FILLED_MAP));
        c.set("bid-explanation", new ItemStack(Material.GOLD_INGOT));
        c.set("submit-bid", new ItemStack(Material.GOLD_NUGGET));
        c.set("cannot-afford-bid", new ItemStack(Material.ARMADILLO_SCUTE));
        c.set("top-bid", new ItemStack(Material.GOLD_BLOCK));
        c.set("collect-auction", new ItemStack(Material.GOLD_BLOCK));
        c.set("collect-coins", new ItemStack(Material.GOLD_NUGGET));
        c.set("own-bid", new ItemStack(Material.POISONOUS_POTATO));
        addMissing(c);
//        l.set("sounds.click", Sound.UI_STONECUTTER_SELECT_RECIPE.toString());
//        l.set("sounds.open-enderchest", Sound.BLOCK_ENDER_CHEST_OPEN.toString());
//        l.set("sounds.close-enderchest", Sound.BLOCK_ENDER_CHEST_CLOSE.toString());
//        l.set("sounds.break-wood", Sound.BLOCK_WOOD_BREAK.toString());
//        l.set("sounds.experience", Sound.ENTITY_EXPERIENCE_ORB_PICKUP.toString());
//        l.set("sounds.villager-deny", Sound.ENTITY_VILLAGER_NO.toString());
//        l.set("sounds.open-shulker", Sound.BLOCK_SHULKER_BOX_OPEN.toString());
//        l.set("sounds.close-shulker", Sound.BLOCK_SHULKER_BOX_CLOSE.toString());
//        l.set("sounds.npc-click", Sound.UI_STONECUTTER_SELECT_RECIPE.toString());
//        l.set("sounds.open-bundle", Sound.ITEM_BUNDLE_DROP_CONTENTS.toString());
//        l.set("sounds.close-bundle", Sound.ITEM_BUNDLE_REMOVE_ONE.toString());
    }

    // Adds the layouts and icons of the hub, sell and history menus and of the category sidebar when they are
    // missing, so servers updating from an older version get them without losing their customized layout.yml.
    // Returns true if something was added.
    public static boolean addMissing(FileConfiguration c) {
        boolean changed = false;
        // hub menu: S = shop, H = history, V = sell
        changed |= setDefault(c, "hub-layout", Arrays.asList(
                "# # # # # # # # #",
                "# # S # H # V # #",
                "# # # # # # # # #"));
        changed |= setDefault(c, "S", new ItemStack(Material.EMERALD));
        changed |= setDefault(c, "H", new ItemStack(Material.CLOCK));
        changed |= setDefault(c, "V", new ItemStack(Material.ANVIL));
        // back to the hub, usable in ah-layout, sell-layout and history-layout
        changed |= setDefault(c, "h", new ItemStack(Material.COMPASS));
        // sell menu: D = item, P = price, T = BIN/auction, L = duration, K = confirm
        changed |= setDefault(c, "sell-layout", Arrays.asList(
                "# # # # # # # # #",
                "# D # P # T # L #",
                "# # # # # # # # #",
                "# # # # K # # # #",
                "h # # # # # # # #"));
        changed |= setDefault(c, "sell-deposit-empty", new ItemStack(Material.HOPPER));
        changed |= setDefault(c, "sell-price", new ItemStack(Material.SUNFLOWER));
        changed |= setDefault(c, "sell-duration", new ItemStack(Material.CLOCK));
        changed |= setDefault(c, "sell-confirm", new ItemStack(Material.LIME_CONCRETE));
        changed |= setDefault(c, "sell-confirm-disabled", new ItemStack(Material.RED_CONCRETE));
        // history menu: '.' = a trade, F = filter (all / bought / sold), p / n = pages
        changed |= setDefault(c, "history-layout", Arrays.asList(
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # # # # # # # #",
                "h # F # p # n # #"));
        changed |= setDefault(c, "history-filter", new ItemStack(Material.BOOK));
        // category sidebar: A = all items, the other letters are defined in categories.yml
        changed |= setDefault(c, "A", categoryIcon(Material.CHEST));
        changed |= setDefault(c, "W", categoryIcon(Material.DIAMOND_SWORD));
        changed |= setDefault(c, "R", categoryIcon(Material.DIAMOND_CHESTPLATE));
        changed |= setDefault(c, "C", categoryIcon(Material.GOLDEN_APPLE));
        changed |= setDefault(c, "B", categoryIcon(Material.GRASS_BLOCK));
        return changed;
    }

    private static boolean setDefault(FileConfiguration c, String path, Object value) {
        if (c.contains(path)) return false;
        c.set(path, value);
        return true;
    }

    private static ItemStack categoryIcon(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    // one-time migration of servers that still use one of the previous default ah-layouts (customized layouts are kept)
    public static void migrateLayout(FileConfiguration c) {
        List<?> current = c.getList("ah-layout");
        if (Objects.equals(current, oldAhLayout("s o # p r n # # m"))
                || Objects.equals(current, oldAhLayout("s o # p r n # f m"))) {
            c.set("ah-layout", sidebarAhLayout());
        }
    }

    private static Map<String, Object> rule(String type, String key) {
        Map<String, Object> m = new HashMap<>();
        m.put("type", type);
        m.put("key", key);
        return m;
    }

    // default categories.yml content. The rules use the same format as blacklist.yml (any matching rule = the item belongs to the category).
    // Only adds categories that don't exist yet, so customized ones are never touched.
    public static boolean generateCategories(FileConfiguration c) {
        boolean changed = false;
        if (!c.contains("W")) {
            List<Map<String, Object>> l = new ArrayList<>();
            for (String s : List.of("_SWORD", "_AXE", "_PICKAXE", "_SHOVEL", "_HOE")) l.add(rule("material_contains", s));
            for (String s : List.of("BOW", "CROSSBOW", "TRIDENT", "MACE", "FISHING_ROD", "SHEARS", "FLINT_AND_STEEL")) l.add(rule("material", s));
            c.set("W", l);
            changed = true;
        }
        if (!c.contains("R")) {
            List<Map<String, Object>> l = new ArrayList<>();
            for (String s : List.of("_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS", "_HORSE_ARMOR", "WOLF_ARMOR")) l.add(rule("material_contains", s));
            for (String s : List.of("ELYTRA", "SHIELD")) l.add(rule("material", s));
            c.set("R", l);
            changed = true;
        }
        if (!c.contains("C")) {
            List<Map<String, Object>> l = new ArrayList<>();
            l.add(rule("edible", "true"));
            l.add(rule("material_contains", "POTION"));
            c.set("C", l);
            changed = true;
        }
        if (!c.contains("B")) {
            c.set("B", List.of(rule("block", "true")));
            changed = true;
        }
        return changed;
    }

    public static void backWardsCompatibility(FileConfiguration c) {
        if (Objects.equals(c.getList("ah-layout"), Arrays.asList(
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # # # # # # # #",
                "s o # p r n # # m"))) {
            c.set("ah-layout", Arrays.asList(
                    "# # # # # # # # #",
                    "# . . . . . . . #",
                    "# . . . . . . . #",
                    "# . . . . . . . #",
                    "# # # # # # # # #",
                    "s o # p r n # f m"));
        }

        if (Objects.equals(c.getList("my-ah-layout"), Arrays.asList(
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # # # # # # # #",
                "b o # p r n # # i"))) {
            c.set("my-ah-layout", Arrays.asList(
                    "# # # # # # # # #",
                    "# . . . . . . . #",
                    "# . . . . . . . #",
                    "# . . . . . . . #",
                    "# # # # # # # # #",
                    "b o # p r n # d i"));
        }

    }

}
