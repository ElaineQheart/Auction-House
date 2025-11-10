package me.elaineqheart.auctionHouse.world.displays;

import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRemoveEvent;
public class KillListener implements Listener {

    public static void register() {
        if (!EntityRemoveEvent.class.isAnnotationPresent(Deprecated.class)) {
            Bukkit.getPluginManager().registerEvents(new KillListener(), AuctionHouse.getPlugin());
        }
    }

    @EventHandler
    public void onRemove(EntityRemoveEvent event) { //this event can cause an infinite loop when the display is removed in the code again
        Entity entity = event.getEntity();
        if(!entity.isValid()) return;
        if(entity.isDead()) return;
        if(UpdateDisplay.isDisplayGlass(entity)) {
            Location loc = entity.getLocation();
            UpdateDisplay.safeRemoveInteraction(loc); // safety measurement, in case both entities are removed at the same time
            UpdateDisplay.removeDisplay(loc,false);
        }
        if(UpdateDisplay.isDisplayInteraction(entity)) {
            Location loc = entity.getLocation().add(-0.5,-1,-0.5);
            UpdateDisplay.removeDisplay(loc,false);
        }
    }

}
