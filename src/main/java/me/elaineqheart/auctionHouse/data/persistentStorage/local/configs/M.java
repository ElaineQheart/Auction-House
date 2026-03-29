package me.elaineqheart.auctionHouse.data.persistentStorage.local.configs;

import me.clip.placeholderapi.PlaceholderAPI;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.Config;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class M extends Config {

    private static final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacySection();
    private static final MiniMessage mm = MiniMessage.miniMessage();

    // AuctionHouse.getPlugin().saveResource("messages.yml", false);

    public static FileConfiguration get() {
        return ConfigManager.messages.getCustomFile();
    }

    private static String getValue(String key, boolean convertNewLine) {
        String message = get().getString(key);
        if (message == null) {
            return ChatColor.RED + "Missing message key: " + key;
        }
        return convertNewLine ? message.replace("&n", "\n") : message;
    }

    //this is to replace placeholders like %player%
    public static String getFormatted(String key, String... replacements) {
        String message = getValue(key,true);
        message = replacePlaceholders(key, message, replacements);
        return adventureApi(message);
    }
    public static String getFormatted(String key, double price, String... replacements) {
        String message = getValue(key,true);
        message = replacePlaceholders(key, message, replacements);
        message = replace(message, price);
        return adventureApi(message);
    }

    public static List<String> getLoreList(String key, String... replacements) {
        String message = getValue(key,false);
        message = replacePlaceholders(key, message, replacements);
        List<String> list = Arrays.asList(message.split("&n"));
        list.replaceAll(M::adventureApi);
        return list;
    }
    public static List<String> getLoreList(String key, double price, String... replacements) {
        String message = getValue(key,false);
        message = replacePlaceholders(key, message, replacements);
        message = replace(message, price);
        List<String> list = Arrays.asList(message.split("&n"));
        list.replaceAll(M::adventureApi);
        return list;
    }

    private static String replacePlaceholders(String key, String message, String... replacements) {
        if (replacements.length % 2 != 0) {
            return ChatColor.RED + "Invalid placeholder replacements for key: " + key;
        }
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return message;
    }

    public static String replace(String message, double... prices) {
        message = message.replace("%price%", StringUtils.formatPrice(prices[0]));
        message = message.replace("%price-trim%", StringUtils.formatPrice(StringUtils.getPriceTrimmed(prices[0])));
        message = message.replace("%number%", StringUtils.formatNumber(prices[0]));
        for(int i = 2; i-1 < prices.length; i++) {
            message = message.replace("%price"+i+"%", StringUtils.formatPrice(prices[i-1]));
            message = message.replace("%price-trim"+i+"%", StringUtils.formatPrice(StringUtils.getPriceTrimmed(prices[i-1])));
            message = message.replace("%number"+i+"%", StringUtils.formatNumber(prices[i-1]));
        }
        return message;
    }

    public static String formatPlayer(String playerName, UUID playerID) {
        return resolveNameTemplate("placeholders.player", playerName, playerID);
    }
    public static String formatSeller(String playerName, UUID playerID) {
        return resolveNameTemplate("placeholders.seller", playerName,playerID);
    }
    public static String formatBuyer(String playerName, UUID playerID) {
        return resolveNameTemplate("placeholders.buyer", playerName, playerID);
    }


    // substitute %player_name% with PAPI when available
    private static String resolveNameTemplate(String templateKey, String playerName, UUID playerID) {
        if (playerName == null) return "";
        String template = get().getString(templateKey, "%player_name%");
        String result = template.replace("%player_name%", playerName);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(playerID);
            result = PlaceholderAPI.setPlaceholders(target, result);
        }

        result = ChatColor.translateAlternateColorCodes('&', result);
        return adventureApi(result);
    }

    private static String adventureApi(String input) {
        Component comp;
        try {
            comp = mm.deserialize(input);
        } catch (Exception e) {
            comp = legacy.deserialize(input);
        }
        return legacy.serialize(comp);
    }

}
