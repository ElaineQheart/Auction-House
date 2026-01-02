package me.elaineqheart.auctionHouse.vault;

//import net.milkbowl.vault.chat.Chat;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultHook {

    private static Economy econ = null;
//    private static Permission perms = null;
//    private static Chat chat = null;

    private VaultHook(){
    }

    private static void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null)
            econ = rsp.getProvider();
    }

//    private static void setupChat() {
//        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
//        if (rsp != null)
//            chat = rsp.getProvider();
//    }
//
//    private static void setupPermissions() {
//        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
//        if (rsp != null)
//            perms = rsp.getProvider();
//    }

    public static Economy getEconomy() {
        return econ;
    }

//    public static Permission getPermissions() {
//        return perms;
//    }
//
//    public static Chat getChat() {
//        return chat;
//    }

    static {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null){
            setupEconomy();
//            setupPermissions();
//            setupChat();
        }
    }


}
