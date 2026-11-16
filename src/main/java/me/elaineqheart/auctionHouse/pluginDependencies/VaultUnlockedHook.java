package me.elaineqheart.auctionHouse.pluginDependencies;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.SettingManager;
import net.milkbowl.vault2.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigDecimal;

public final class VaultUnlockedHook {

    private static Economy econ = null;
    private static net.milkbowl.vault.economy.Economy legacyEcon = null;
    private static boolean useLegacy = false;

    private VaultUnlockedHook(){
    }

    public static boolean setupEconomy() {
        if (AuctionHouse.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = AuctionHouse.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return setupLegacyEconomy();
        }
        econ = rsp.getProvider();
        AuctionHouse.getInstance().getLogger().info("Economy plugin found: " + econ.getName());
        return econ != null;
    }

    private static boolean setupLegacyEconomy() {
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> legacyRsp = AuctionHouse.getInstance().getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (legacyRsp == null) {
            return false;
        }
        legacyEcon = legacyRsp.getProvider();
        AuctionHouse.getInstance().getLogger().info("Legacy economy plugin found: " + legacyEcon.getName());
        useLegacy = true;
        return legacyRsp != null;
    }

    public static BigDecimal getBalance(Player player) {
        if (useLegacy) {
            return BigDecimal.valueOf(legacyEcon.getBalance(player));
        } else {
            return econ.getBalance(AuctionHouse.getPluginName(), player.getUniqueId(), player.getWorld().toString(),
                    SettingManager.vaultCurrency);
        }
    }

    public static MyEconomyResponse deposit(Player player, double amount) {
        if (useLegacy) {
            return new MyEconomyResponse(legacyEcon.depositPlayer(player, amount));
        } else {
            return new MyEconomyResponse(econ.deposit(AuctionHouse.getPluginName(), player.getUniqueId(), player.getWorld().toString(),
                    SettingManager.vaultCurrency, BigDecimal.valueOf(amount)));
        }
    }

    public static MyEconomyResponse withdraw(Player player, double amount) {
        if (useLegacy) {
            return new MyEconomyResponse(legacyEcon.withdrawPlayer(player, amount));
        } else {
            return new MyEconomyResponse(econ.withdraw(AuctionHouse.getPluginName(), player.getUniqueId(), player.getWorld().toString(),
                    SettingManager.vaultCurrency, BigDecimal.valueOf(amount)));
        }
    }

}