package me.elaineqheart.auctionHouse.data.persistentStorage.yml.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

public class PlayerPreferencesManager extends ConfigManager {

    private static final boolean defaultAnnounce = true;

    public static boolean hasAnnouncementsEnabled(UUID player) {
        return playerPreferences.get().getBoolean("players." + player.toString() + ".announcements", defaultAnnounce);
    }
    public static void setAnnouncementsEnabled(UUID player, boolean enabled) {
        if(defaultAnnounce != enabled) playerPreferences.get().set("players." + player + ".announcements", enabled);
        else playerPreferences.get().set("players." + player + ".announcements", null);
        playerPreferences.save();
    }
    public static boolean toggleAnnouncements(Player player) {
        boolean current = hasAnnouncementsEnabled(player.getUniqueId());
        setAnnouncementsEnabled(player.getUniqueId(), !current);
        return !current;
    }

    public static void saveInstance(UUID player, AhConfiguration c) {
        if(c == null || playerPreferences.get() == null) return;
        Gson gson = getGson();
        playerPreferences.get().set("players." + player + ".configuration", gson.toJson(c));
        playerPreferences.save();
    }
    public static void loadInstance(Player p) {
        Gson gson = getGson();
        AhConfiguration.loadInstance(p, gson.fromJson(playerPreferences.get().getString("players." + p.getUniqueId() + ".configuration"), AhConfiguration.class));
    }
    public static void setup() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            loadInstance(p);
        }
    }
    public static void disable() {
        if(playerPreferences.get() == null) return;
        for(Player p : Bukkit.getOnlinePlayers()) {
            saveInstance(p.getUniqueId(), AhConfiguration.getInstance(p));
        }
    }

    private static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new TypeAdapterFactory() {
                    @Override
                    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                        TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
                        TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

                        return new TypeAdapter<>() {
                            @Override
                            public void write(JsonWriter out, T value) throws IOException {
                                JsonElement tree = delegate.toJsonTree(value);

                                if (tree.isJsonObject()) {
                                    JsonObject obj = tree.getAsJsonObject();
                                    obj.entrySet().removeIf(entry -> {
                                        JsonElement e = entry.getValue();
                                        if (!e.isJsonPrimitive()) return false;
                                        JsonPrimitive p = e.getAsJsonPrimitive();
                                        if (p.isBoolean()) {return !p.getAsBoolean();}
                                        if (p.isNumber()) {return p.getAsNumber().doubleValue() == 0;}
                                        if (p.isString()) {return p.getAsString().isEmpty();}
                                        return false;
                                    });
                                }

                                elementAdapter.write(out, tree);
                            }


                            @Override
                            public T read(JsonReader in) throws IOException {
                                return delegate.read(in);
                            }
                        };
                    }
                })
                .create();
    }

}
