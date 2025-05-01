package me.elaineqheart.auctionHouse.GUI.other;

import me.elaineqheart.auctionHouse.ah.ItemManager;
import me.elaineqheart.auctionHouse.ah.ItemNote;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.AnvilView;

public class AnvilSearchGUI implements Listener {

    // This is used to pass the note to the next GUI
    // It will overwrite the previous note if the player opens a new AnvilSearchGUI
    // If two admins try to delete an item at the same time, it won't work
    public static ItemNote currentAdminNote;

    public enum SearchType {
        AH,
        ADMIN_AH,
        ITEM_EXPIRE_MESSAGE,
        ITEM_DELETE_MESSAGE,
        BAN_MESSAGE
    }

    public AnvilSearchGUI(Player player, SearchType type, ItemNote note) {
        currentAdminNote = note;
        AnvilView view = null;
        switch (type) {
            case AH -> view = MenuType.ANVIL.create(player,"Search Item");
            case ADMIN_AH -> view = MenuType.ANVIL.create(player,"Admin Search Item");
            case ITEM_EXPIRE_MESSAGE -> view = MenuType.ANVIL.create(player,"Expire Item Reason");
            case ITEM_DELETE_MESSAGE -> view = MenuType.ANVIL.create(player,"Delete Item Reason");
        }
        view.setMaximumRepairCost(0);
        view.setItem(0, ItemManager.emptyPaper);
        player.openInventory(view);
    }

}
