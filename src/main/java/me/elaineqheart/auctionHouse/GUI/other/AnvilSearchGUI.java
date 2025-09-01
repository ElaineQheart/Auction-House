package me.elaineqheart.auctionHouse.GUI.other;

import me.elaineqheart.auctionHouse.data.yml.Messages;
import me.elaineqheart.auctionHouse.data.items.ItemManager;
import me.elaineqheart.auctionHouse.data.items.ItemNote;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.AnvilView;

import java.util.HashMap;

public class AnvilSearchGUI implements Listener {

    // This is used to pass the note to the next GUI (AnvilGUIListener)
    // an item Note linked to the player
    public static HashMap<Player, ItemNote> currentAdminNoteMap = new HashMap<>();

    public enum SearchType {
        AH,
        ADMIN_AH,
        ITEM_EXPIRE_MESSAGE,
        ITEM_DELETE_MESSAGE
    }

    public AnvilSearchGUI(Player player, SearchType type, ItemNote note) {
        currentAdminNoteMap.put(player, note);
        AnvilView view = null;
        switch (type) {
            case AH -> view = MenuType.ANVIL.create(player, Messages.getFormatted("inventory-titles.anvil-search"));
            case ADMIN_AH -> view = MenuType.ANVIL.create(player, Messages.getFormatted("inventory-titles.anvil-admin-search"));
            case ITEM_EXPIRE_MESSAGE -> view = MenuType.ANVIL.create(player, Messages.getFormatted("anvil-admin-expire-message"));
            case ITEM_DELETE_MESSAGE -> view = MenuType.ANVIL.create(player, Messages.getFormatted("anvil-admin-delete-message"));
        }
        view.setMaximumRepairCost(0);
        view.setItem(0, ItemManager.emptyPaper);
        player.openInventory(view);
    }

}
