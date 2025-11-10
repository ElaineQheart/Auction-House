package me.elaineqheart.auctionHouse.data.persistentStorage.redis;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.yml.SettingManager;
import redis.clients.jedis.*;

public class RedisManager {

    private static JedisPool pool;

    public static void connect() {
        String host = SettingManager.redisHost;
        int port = SettingManager.redisPort;
        String username = SettingManager.redisUsername;
        String password = SettingManager.redisPassword;

        pool = new JedisPool(host, port, username, password);
        try (Jedis jedis = pool.getResource()) {
            AuctionHouse.getPlugin().getLogger().info("Redis ping: " + jedis.ping());
        }
    }

    public static Jedis getResource() {
        if (pool == null) {
            throw new IllegalStateException("Redis connection not initialized!");
        }
        return pool.getResource();
    }

    public static void disconnect() {
        if (pool != null) pool.close();
    }


}
