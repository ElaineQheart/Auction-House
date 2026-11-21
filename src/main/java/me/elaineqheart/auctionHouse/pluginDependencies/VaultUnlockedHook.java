package me.elaineqheart.auctionHouse.pluginDependencies;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.SettingManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import net.milkbowl.vault2.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigDecimal;
import java.util.Arrays;

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
        try {
            RegisteredServiceProvider<Economy> rsp = AuctionHouse.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) return setupLegacyEconomy();
            econ = rsp.getProvider();
        } catch (NoClassDefFoundError e) {
            return setupLegacyEconomy();
        }
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
        return legacyEcon != null;
    }

    public static BigDecimal getBalance(Player player) {
        if (useLegacy) return BigDecimal.valueOf(legacyEcon.getBalance(player));

        checkCurrencyAvailability(player);
        if (SettingManager.vaultCurrency.isEmpty()) {
            return econ.getBalance(AuctionHouse.getPluginName(), player.getUniqueId(), player.getWorld().toString());
        }
        return econ.getBalance(AuctionHouse.getPluginName(), player.getUniqueId(), player.getWorld().toString(),
                SettingManager.vaultCurrency);
        }


    public static MyEconomyResponse deposit(Player player, double amount) {
        if (useLegacy) return new MyEconomyResponse(legacyEcon.depositPlayer(player, amount));

        checkCurrencyAvailability(player);
        if (SettingManager.vaultCurrency.isEmpty()) {
            return new MyEconomyResponse(econ.deposit(AuctionHouse.getPluginName(), player.getUniqueId(), player.getWorld().toString(),
                    BigDecimal.valueOf(amount)));
        }
        return new MyEconomyResponse(econ.deposit(AuctionHouse.getPluginName(), player.getUniqueId(), player.getWorld().toString(),
                SettingManager.vaultCurrency, BigDecimal.valueOf(amount)));
    }

    public static MyEconomyResponse withdraw(Player player, double amount) {
        if (useLegacy) return new MyEconomyResponse(legacyEcon.withdrawPlayer(player, amount));

        checkCurrencyAvailability(player);
        if (SettingManager.vaultCurrency.isEmpty()) {
            return new MyEconomyResponse(econ.withdraw(AuctionHouse.getPluginName(), player.getUniqueId(), player.getWorld().toString(),
                    BigDecimal.valueOf(amount)));
        }
        return new MyEconomyResponse(econ.withdraw(AuctionHouse.getPluginName(), player.getUniqueId(), player.getWorld().toString(),
                SettingManager.vaultCurrency, BigDecimal.valueOf(amount)));
    }
    
    private static void checkCurrencyAvailability(Player player) {
        if (SettingManager.vaultCurrency.isEmpty()) return;
        if (!econ.currencies().contains(SettingManager.vaultCurrency)) {
            player.sendMessage(M.getFormatted("chat.no-currency-found",
                    "%currencies%", Arrays.toString(econ.currencies().toArray()),
                            "%currency%", SettingManager.vaultCurrency));
        }
    }

}