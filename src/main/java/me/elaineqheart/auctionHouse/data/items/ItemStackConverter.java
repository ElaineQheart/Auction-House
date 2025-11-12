package me.elaineqheart.auctionHouse.data.items;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ItemStackConverter {

    private static final Type MAPTYPE = new TypeToken<Map<String, Object>>(){}.getType();

    public static String encode(ItemStack item) {
        try {
            Gson gson = new Gson();
            return gson.toJson(item.serialize());
        } catch (Exception e) {
            //ensuring backwards compatibility
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
                dataOutput.writeObject(item);
                return new String(Base64Coder.encode(outputStream.toByteArray()));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    public static ItemStack decode(String data) {
        try {
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(data, MAPTYPE);
            return ItemStack.deserialize(map);
        } catch (JsonSyntaxException e) {
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decode(data));
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                return (ItemStack) dataInput.readObject();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
