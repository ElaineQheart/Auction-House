package me.elaineqheart.auctionHouse.data.messages;

import org.bukkit.ChatColor;

public class Messages {

    public static String test;

    static {
        loadData();
    }

    private static void loadData() {

    }


    //this is to replace placeholders like %player%
    public static String getFormatted(String key, String... replacements) {
        String message = MessagesConfig.getValue(key);
        if (replacements.length % 2 != 0) {
            return ChatColor.RED + "Invalid placeholder replacements for key: " + key;
        }
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return message;
    }

}
