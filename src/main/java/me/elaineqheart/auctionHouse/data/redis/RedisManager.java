package me.elaineqheart.auctionHouse.data.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {

    private static JedisPool pool;

    public static void connect() {
        // Configuration
        String host = "localhost";
        int port = 6379;
        String username = "USERNAME";  // Optional — only if Redis ACLs are enabled
        String password = "PASSWORD";  // Optional — can be null

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        config.setMaxIdle(5);
        config.setMinIdle(1);

        // Initialize connection pool
        pool = new JedisPool(config, host, port, 2000, username, password, 0);
    }

    public static Jedis getResource() {
        if (pool == null) {
            throw new IllegalStateException("Redis connection not initialized!");
        }
        return pool.getResource();
    }

    public static void disconnect() {
        if (pool != null) {
            pool.close();
        }
    }


}
