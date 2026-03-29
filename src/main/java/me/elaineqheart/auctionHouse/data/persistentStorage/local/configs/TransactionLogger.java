package me.elaineqheart.auctionHouse.data.persistentStorage.local.configs;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.Config;
import org.bukkit.configuration.file.YamlConfiguration;

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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFile(), true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String logEntry = String.format("[%s] Buyer: %s | Seller: %s | Item: %s | Amount: %d | Price: %.2f | BID: %b",
                    timestamp, buyer, seller, item, amount, price, isBID);
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        Date date = new Date();
        var localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        String formattedDate = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        int number = 1;
        File folder = new File(AuctionHouse.getPlugin().getDataFolder() + "/logs");
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
