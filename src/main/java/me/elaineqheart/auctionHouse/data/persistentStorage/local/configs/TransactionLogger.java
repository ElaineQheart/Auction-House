package me.elaineqheart.auctionHouse.data.persistentStorage.local.configs;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.Config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TransactionLogger extends Config {

    public void logTransaction(String buyer, String seller, String item, double price, int amount, boolean isBID) {
        logAuction(String.format("[%s] Buyer: %s | Seller: %s | Item: %s | Amount: %d | Price: %.2f | BID: %b",
                getTimeStamp(), buyer, seller, item, amount, price, isBID));
    }

    public void logSetUpAuction(String player, String item, double price, int amount, boolean isBID) {
        logAuction(String.format("[%s] Player set up an auction: %s | Item: %s | Amount: %d | Price: %.2f | BID: %b",
                getTimeStamp(), player, item, amount, price, isBID));
    }

    public void logCancelAuction(String player, String item, double price, int amount, boolean isBID) {
        logAuction(String.format("[%s] Player canceled an auction: %s | Item: %s | Amount: %d | Price: %.2f | BID: %b",
                getTimeStamp(), player, item, amount, price, isBID));
    }

    public void logExpiredAuction(String player, String item, double price, int amount, boolean isBID) {
        logAuction(String.format("[%s] Player collected an expired auction: %s | Item: %s | Amount: %d | Price: %.2f | BID: %b",
                getTimeStamp(), player, item, amount, price, isBID));
    }

    public void logAdminExpiredAuction(String player, String item, double price, int amount, boolean isBID) {
        logAuction(String.format("[%s] Player collected an admin expired auction: %s | Item: %s | Amount: %d | Price: %.2f | BID: %b",
                getTimeStamp(), player, item, amount, price, isBID));
    }

    public void logAdminDeletedAuction(String player, String item, double price, int amount, boolean isBID) {
        logAuction(String.format("[%s] Player collected an admin deleted auction: %s | Item: %s | Amount: %d | Price: %.2f | BID: %b",
                getTimeStamp(), player, item, amount, price, isBID));
    }

    public void logPurge(String player, String item, double price, int amount, boolean isBID) {
        logAuction(String.format("[%s] Purged by admin. | Player: %s | Item: %s | Amount: %d | Price: %.2f | BID: %b",
                getTimeStamp(), player, item, amount, price, isBID));
    }

    private void logAuction(String logEntry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFile(), true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public String getNewName() {
        Date date = new Date();
        var localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        String formattedDate = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        int number = 1;
        File folder = new File(AuctionHouse.getInstance().getDataFolder() + "/logs");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (!file.isFile()) continue;
                String fileDate = file.getName().substring(0,10);
                if(!fileDate.equals(formattedDate)) continue;
                int fileNumber = Integer.parseInt(file.getName().replaceFirst(".log", "").substring(11));
                if(fileNumber >= number) number = fileNumber+1;
            }
        }
        return formattedDate + "-" + number + ".log";
    }

    @Override
    public void reload(){}
}
