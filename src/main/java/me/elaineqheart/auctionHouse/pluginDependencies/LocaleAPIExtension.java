package me.elaineqheart.auctionHouse.pluginDependencies;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class LocaleAPIExtension {


    public static boolean enabled;

    public static void setup() {
        Plugin localeAPIPlugin = Bukkit.getPluginManager().getPlugin("Locale-API");
        if(localeAPIPlugin != null && localeAPIPlugin.isEnabled()) enabled = true;
    }

}
