package me.elaineqheart.auctionHouse.data.persistentStorage;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ItemStackConverter {

    //private static final Type MAPTYPE = new TypeToken<Map<String, Object>>(){}.getType();

    public static String encode(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            return new String(Base64Coder.encode(outputStream.toByteArray()));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
//        try {
//            Gson gson = new Gson();
//            return gson.toJson(item.serialize());
//        } catch (Exception e) {
//
//        }

    }

    public static ItemStack decode(String data) {
        if(data == null) return null;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            return (ItemStack) dataInput.readObject();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
//        try {
//            Gson gson = new Gson();
//            Map<String, Object> map = gson.fromJson(data, MAPTYPE);
//            ItemStack item = ItemStack.deserialize(map);
//        } catch (JsonSyntaxException e) {
//
//        }
    }

}
