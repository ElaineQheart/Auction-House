package me.elaineqheart.auctionHouse.data.items;

import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.SettingManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.Objects;

public class StringUtils {

    public static String getTime(Long seconds, boolean convertDays) { //output example: 4h 23m 59s
        String s;
        String m;
        String h;
        String d;
        int sec = (int) ((seconds)%60);
        if(String.valueOf(sec).length()==1) {
            s = '0' + String.valueOf(sec);
        }else{
            s = String.valueOf(sec);
        }
        int min = (int) ((seconds/60)%60);
        if(String.valueOf(min).length()==1) {
            m = '0' + String.valueOf(min);
        }else{
            m = String.valueOf(min);
        }
        int hours = convertDays ? (int) (seconds/60/60%24) : (int) (seconds/60/60);
        if(String.valueOf(hours).length()==1) {
            h = '0' + String.valueOf(hours);
        }else{
            h = String.valueOf(hours);
        }
        if(convertDays) {
            int days = (int) (seconds / 60 / 60 / 24);
            if (String.valueOf(days).length() == 1) {
                d = '0' + String.valueOf(days);
            } else {
                d = String.valueOf(days);
            }
            return (ChatColor.YELLOW+d+ SettingManager.formatTimeCharacters.charAt(0)+" "+
                    h+SettingManager.formatTimeCharacters.charAt(1)+" "+
                    m+SettingManager.formatTimeCharacters.charAt(2)+" "+
                    s+SettingManager.formatTimeCharacters.charAt(3));
        } else {
            return (ChatColor.YELLOW+h+SettingManager.formatTimeCharacters.charAt(1)+" "+
                    m+SettingManager.formatTimeCharacters.charAt(2)+" "+
                    s+SettingManager.formatTimeCharacters.charAt(3));
        }
    }
    //dhms

    public static String getTimeTrimmed(long seconds) { //output example: 4h
        if(seconds < 60) {
            return seconds + SettingManager.formatTimeCharacters.substring(3,4);
        } else if(seconds < 60*60) {
            return (int)(seconds/60) + SettingManager.formatTimeCharacters.substring(2,3);
        } else {
            return (int)(seconds/60/60) + SettingManager.formatTimeCharacters.substring(1,2);
        }
    }

    public static String formatNumber(double number) {
        // if the price is a whole number, format it without decimal places
        // fallback for async threads
        DecimalFormat fmt = Objects.requireNonNullElseGet(SettingManager.formatter, () -> new DecimalFormat(Messages.getFormatted("placeholders.format-numbers")));
        return Messages.getFormatted("placeholders.number", "%input%", fmt.format(number));
    }
    public static String formatNumber(String number) {
        return Messages.getFormatted("placeholders.number", "%input%", number);
    }

    public static String formatPrice(double price) {
        return Messages.getFormatted("placeholders.price",
                "%number%", formatNumber(price),
                "%currency-symbol%", Messages.getFormatted("placeholders.currency-symbol"));
    }
    public static String formatPrice(String price) {
        return Messages.getFormatted("placeholders.price",
                "%number%", formatNumber(price),
                "%currency-symbol%", Messages.getFormatted("placeholders.currency-symbol"));
    }

    public static String getItemName(ItemStack item, World world) {
        Item itemEntity = (Item) world.spawnEntity(new Location(world,0,0,0), EntityType.ITEM);
        itemEntity.setItemStack(item);
        String name = itemEntity.getName();
        itemEntity.remove();
        if(item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) name = ChatColor.ITALIC + item.getItemMeta().getDisplayName();
        return ChatColor.RESET + name;
    }

}
