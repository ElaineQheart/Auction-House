package me.elaineqheart.auctionHouse.data.persistentStorage.local;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Objects;

public class LayoutGenerator {

    public static void generate(FileConfiguration c) {
        c.set("ah-layout", Arrays.asList(
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # # # # # # # #",
                "s o # p r n # f m"));
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
