package me.elaineqheart.auctionHouse.data.persistentStorage.local;

import org.bukkit.Material;
import org.bukkit.Sound;
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
                "b o # p r n # # i"));
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

    String original = """
            ah-layout:
              - '# # # # # # # # #'
              - '# . . . . . . . #'
              - '# . . . . . . . #'
              - '# . . . . . . . #'
              - '# # # # # # # # #'
              - 's o # p r n # f m'
            my-ah-layout:
              - '# # # # # # # # #'
              - '# . . . . . . . #'
              - '# . . . . . . . #'
              - '# . . . . . . . #'
              - '# # # # # # # # #'
              - 'b o # p r n # d i'
            '#':
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: BLACK_STAINED_GLASS_PANE
              meta:
                ==: ItemMeta
                meta-type: UNSPECIFIC
                hide-tool-tip: true
            s:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:oak_sign
              count: 1
              schema_version: 1
            active-search:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:spruce_sign
              count: 1
              schema_version: 1
            o:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:hopper
              count: 1
              schema_version: 1
            p:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:arrow
              count: 1
              schema_version: 1
            r:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:nether_star
              count: 1
              schema_version: 1
            n:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:arrow
              count: 1
              schema_version: 1
            m:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:ender_chest
              count: 1
              schema_version: 1
            b:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:arrow
              count: 1
              schema_version: 1
            i:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:paper
              count: 1
              schema_version: 1
            f:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:powered_rail
              count: 1
              schema_version: 1
            d:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:golden_carrot
              count: 1
              schema_version: 1
            bin-filter-bin:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:gold_ingot
              count: 1
              schema_version: 1
            bin-filter-auctions:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:gold_block
              count: 1
              schema_version: 1
            locked-slot:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:barrier
              count: 1
              schema_version: 1
            back-to-my-auctions:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:arrow
              count: 1
              schema_version: 1
            anvil-search-paper:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:paper
              count: 1
              schema_version: 1
            cancel:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:red_banner
              count: 1
              schema_version: 1
            collect-expired-item:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:red_dye
              count: 1
              schema_version: 1
            cancel-auction:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:red_concrete
              count: 1
              schema_version: 1
            command-block-info:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:structure_block
              count: 1
              schema_version: 1
            admin-cancel-auction:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:red_concrete
              count: 1
              schema_version: 1
            admin-expire-auction:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:red_dye
              count: 1
              schema_version: 1
            confirm:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:green_banner
              count: 1
              schema_version: 1
            choose-item-buy-amount:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:oak_hanging_sign
              count: 1
              schema_version: 1
            dirt:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:dirt
              count: 1
              schema_version: 1
            turtle-scute-confirm:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:turtle_scute
              count: 1
              schema_version: 1
            cannot-afford:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:armadillo_scute
              count: 1
              schema_version: 1
            collect-sold-item:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:diamond
              count: 1
              schema_version: 1
            bid-history:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:filled_map
              count: 1
              schema_version: 1
            bid-explanation:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:gold_ingot
              count: 1
              schema_version: 1
            submit-bid:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:gold_nugget
              count: 1
              schema_version: 1
            cannot-afford-bid:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:armadillo_scute
              count: 1
              schema_version: 1
            top-bid:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:gold_block
              count: 1
              schema_version: 1
            collect-auction:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:gold_block
              count: 1
              schema_version: 1
            collect-coins:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:gold_nugget
              count: 1
              schema_version: 1
            own-bid:
              ==: org.bukkit.inventory.ItemStack
              DataVersion: 4325
              id: minecraft:poisonous_potato
              count: 1
              schema_version: 1""";

}
