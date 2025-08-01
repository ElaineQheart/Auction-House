package me.elaineqheart.auctionHouse.data;

import org.bukkit.ChatColor;

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
            return (ChatColor.YELLOW+d+"d "+h+"h "+m+"m "+s+"s");
        } else {
            return (ChatColor.YELLOW+h+"h "+m+"m "+s+"s");
        }
    }

    public static String getTimeTrimmed(long seconds) { //output example: 4h
        if(seconds < 60) {
            return seconds + "s";
        } else if(seconds < 60*60) {
            return (int)(seconds/60) + "m";
        } else {
            return (int)(seconds/60/60) + "h";
        }
    }

    public static String formatNumber(double number, int decimalPlaces) {
        String formatted = String.format("%,.0" + decimalPlaces + "f", number);
        formatted = formatted.replace(",", "{COMMA}");
        formatted = formatted.replace(".", "{DOT}");
        formatted = formatted.replace("{COMMA}", SettingManager.formatNumbersComma);
        return ChatColor.GOLD + formatted.replace("{DOT}", SettingManager.formatNumbersDot);
    }
    public static String formatPrice(double price, int decimalPlaces) {
        return formatNumber(price,decimalPlaces) + ChatColor.YELLOW + SettingManager.currencySymbol;
    }

}
