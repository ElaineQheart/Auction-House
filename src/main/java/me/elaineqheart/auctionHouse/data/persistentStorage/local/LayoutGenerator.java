package me.elaineqheart.auctionHouse.data.persistentStorage.local;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class LayoutGenerator {

    public static void generate(FileConfiguration l) {
        l.set("ah-layout", Arrays.asList(
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # # # # # # # #",
                "s o # p r n # # m"));
        l.set("my-ah-layout", Arrays.asList(
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
        l.set("#", fillerItem);
        l.set("s", new ItemStack(Material.OAK_SIGN));
        l.set("active-search", new ItemStack(Material.SPRUCE_SIGN));
        l.set("o", new ItemStack(Material.HOPPER));
        l.set("p", new ItemStack(Material.ARROW));
        l.set("r", new ItemStack(Material.NETHER_STAR));
        l.set("n", new ItemStack(Material.ARROW));
        l.set("m", new ItemStack(Material.ENDER_CHEST));
        l.set("b", new ItemStack(Material.ARROW));
        l.set("i", new ItemStack(Material.PAPER));
        l.set("f", new ItemStack(Material.POWERED_RAIL));
        l.set("d", new ItemStack(Material.GOLDEN_CARROT));
        l.set("bin-filter-bin", new ItemStack(Material.GOLD_INGOT));
        l.set("bin-filter-auctions", new ItemStack(Material.GOLD_BLOCK));
        l.set("locked-slot", new ItemStack(Material.BARRIER));
        l.set("back-to-my-auctions", new ItemStack(Material.ARROW));
        l.set("anvil-search-paper", new ItemStack(Material.PAPER));
        l.set("cancel", new ItemStack(Material.RED_BANNER));
        l.set("collect-expired-item", new ItemStack(Material.RED_DYE));
        l.set("cancel-auction", new ItemStack(Material.RED_CONCRETE));
        l.set("command-block-info", new ItemStack(Material.STRUCTURE_BLOCK));
        l.set("admin-cancel-auction", new ItemStack(Material.RED_CONCRETE));
        l.set("admin-expire-auction", new ItemStack(Material.RED_DYE));
        l.set("confirm", new ItemStack(Material.GREEN_BANNER));
        l.set("choose-item-buy-amount", new ItemStack(Material.OAK_HANGING_SIGN));
        l.set("dirt", new ItemStack(Material.DIRT));
        l.set("turtle-scute-confirm", new ItemStack(Material.TURTLE_SCUTE));
        l.set("cannot-afford", new ItemStack(Material.ARMADILLO_SCUTE));
        l.set("collect-sold-item", new ItemStack(Material.DIAMOND));
        l.set("bid-history", new ItemStack(Material.FILLED_MAP));
        l.set("bid-explanation", new ItemStack(Material.GOLD_INGOT));
        l.set("submit-bid", new ItemStack(Material.GOLD_NUGGET));
        l.set("cannot-afford-bid", new ItemStack(Material.ARMADILLO_SCUTE));
        l.set("top-bid", new ItemStack(Material.GOLD_BLOCK));
        l.set("collect-auction", new ItemStack(Material.GOLD_BLOCK));
        l.set("collect-coins", new ItemStack(Material.GOLD_NUGGET));
        l.set("own-bid", new ItemStack(Material.POISONOUS_POTATO));
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

}
