package me.elaineqheart.auctionHouse.data.persistentStorage.yml;

import me.elaineqheart.auctionHouse.data.persistentStorage.yml.data.ConfigManager;

import java.io.FileWriter;
import java.io.IOException;

public class OldLayout {

    private static final String layout = """
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
              v: 3955
              type: OAK_SIGN
            active-search:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: SPRUCE_SIGN
            o:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: HOPPER
            p:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: ARROW
            r:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: NETHER_STAR
            n:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: ARROW
            m:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: ENDER_CHEST
            b:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: ARROW
            i:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: PAPER
            f:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: POWERED_RAIL
            d:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: GOLDEN_CARROT
            bin-filter-bin:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: GOLD_INGOT
            bin-filter-auctions:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: GOLD_BLOCK
            locked-slot:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: BARRIER
            back-to-my-auctions:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: ARROW
            anvil-search-paper:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: PAPER
            cancel:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: RED_BANNER
            collect-expired-item:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: RED_DYE
            cancel-auction:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: RED_CONCRETE
            command-block-info:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: STRUCTURE_BLOCK
            admin-cancel-auction:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: RED_CONCRETE
            admin-expire-auction:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: RED_DYE
            confirm:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: GREEN_BANNER
            choose-item-buy-amount:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: OAK_HANGING_SIGN
            dirt:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: DIRT
            turtle-scute-confirm:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: TURTLE_SCUTE
            cannot-afford:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: ARMADILLO_SCUTE
            collect-sold-item:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: DIAMOND
            bid-history:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: FILLED_MAP
            bid-explanation:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: GOLD_INGOT
            submit-bid:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: GOLD_NUGGET
            cannot-afford-bid:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: ARMADILLO_SCUTE
            top-bid:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: GOLD_BLOCK
            collect-auction:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: GOLD_BLOCK
            collect-coins:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: GOLD_NUGGET
            own-bid:
              ==: org.bukkit.inventory.ItemStack
              v: 3955
              type: POISONOUS_POTATO
            old-layout: true
            """;

    public static void saveOldLayout() {
        try {
            FileWriter writer = new FileWriter(ConfigManager.layout.getFile());
            writer.write(layout);
            ConfigManager.layout.save();
            writer.flush();
            writer.close();
            ConfigManager.layout.reload();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save old layout for minecraft versions prior to 1.21.5. Error message: " + e);
        }
    }

}
