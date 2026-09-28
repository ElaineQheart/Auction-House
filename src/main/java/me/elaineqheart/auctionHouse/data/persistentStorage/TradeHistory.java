package me.elaineqheart.auctionHouse.data.persistentStorage;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.SettingManager;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Purchase / sale history of every player, shown in the history menu.
// Stored in SQLite (data/history.db; the driver ships with Spigot and Paper). Every database access runs on one
// background thread: trades never block the server thread and SQLite only ever sees a single connection.
// The database stays bounded: each player keeps the last <history-max-entries> trades, trades older than
// <history-expire-days> are purged at startup and every 6 hours, and freed space is returned to the disk (auto_vacuum).
public class TradeHistory {

    // bought = true: the owner of the history bought the item; otherName is then the seller, otherwise the buyer
    public record Trade(long time, boolean bought, String otherName, String itemName, String itemData,
                        int amount, double price, double taxRate, boolean bid) {}

    public record Page(List<Trade> trades, int total) {}

    // constant, fully parameterized statements only: never build SQL from values
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS trades ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, player TEXT NOT NULL, time INTEGER NOT NULL, "
            + "bought INTEGER NOT NULL, other_name TEXT, item_name TEXT, item_data TEXT NOT NULL, "
            + "amount INTEGER NOT NULL, price REAL NOT NULL, tax_rate REAL NOT NULL, bid INTEGER NOT NULL)";
    private static final String INSERT = "INSERT INTO trades (player, time, bought, other_name, item_name, item_data, amount, price, tax_rate, bid) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    // bought IN (?, ?): (0, 1) = all trades, (1, 1) = only purchases, (0, 0) = only sales
    private static final String COUNT = "SELECT COUNT(*) FROM trades WHERE player = ? AND bought IN (?, ?)";
    private static final String SELECT_PAGE = "SELECT time, bought, other_name, item_name, item_data, amount, price, tax_rate, bid "
            + "FROM trades WHERE player = ? AND bought IN (?, ?) ORDER BY id DESC LIMIT ? OFFSET ?";
    private static final String PURGE = "DELETE FROM trades WHERE time < ?";
    // removes everything older than the newest <history-max-entries> trades of the player
    private static final String PRUNE = "DELETE FROM trades WHERE player = ? AND id <= "
            + "(SELECT id FROM trades WHERE player = ? ORDER BY id DESC LIMIT 1 OFFSET ?)";

    private static volatile ScheduledExecutorService executor; // read from any thread (Folia region threads)
    private static Connection connection; // only used on the executor thread

    public static void enable() {
        executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "AuctionHouse-TradeHistory");
            thread.setDaemon(true);
            return thread;
        });
        executor.execute(() -> {
            File file = new File(AuctionHouse.getInstance().getDataFolder(), "data/history.db");
            file.getParentFile().mkdirs();
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
                try (Statement statement = connection.createStatement()) {
                    // deleted rows give their space back to the disk (only applies when the database is created)
                    statement.execute("PRAGMA auto_vacuum=FULL");
                    statement.execute("PRAGMA journal_mode=WAL");
                    statement.execute("PRAGMA journal_size_limit=4194304"); // the WAL file is truncated to 4 MB after checkpoints
                    statement.execute(CREATE_TABLE);
                    statement.execute("CREATE INDEX IF NOT EXISTS trades_player_id ON trades (player, id)");
                    statement.execute("CREATE INDEX IF NOT EXISTS trades_time ON trades (time)");
                }
            } catch (SQLException e) {
                connection = null;
                AuctionHouse.getInstance().getLogger().severe("Could not open the trade history database: " + e.getMessage());
            }
        });
        executor.scheduleWithFixedDelay(TradeHistory::purgeExpired, 0, 6, TimeUnit.HOURS);
    }

    // runs on the executor thread
    private static void purgeExpired() {
        int days = SettingManager.historyExpireDays;
        if (connection == null || days <= 0) return;
        try (PreparedStatement delete = connection.prepareStatement(PURGE)) {
            delete.setLong(1, System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days));
            delete.executeUpdate();
        } catch (SQLException e) {
            AuctionHouse.getInstance().getLogger().warning("Could not purge the trade history: " + e.getMessage());
        }
    }

    // waits for the pending writes, then closes the database
    public static void disable() {
        if (executor == null) return;
        executor.execute(() -> {
            try {
                if (connection != null) connection.close();
            } catch (SQLException ignored) {}
            connection = null;
        });
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS))
                AuctionHouse.getInstance().getLogger().warning("Some trades could not be saved to the history in time.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor = null;
    }

    public static void record(UUID buyerID, String buyerName, UUID sellerID, String sellerName,
                              ItemStack item, String itemName, int amount, double price, double taxRate, boolean bid) {
        // ItemStacks aren't thread-safe: encode on the calling thread
        ItemStack copy = item.clone();
        copy.setAmount(Math.max(1, Math.min(amount, copy.getMaxStackSize())));
        String data = ItemStackConverter.encode(copy);
        long now = System.currentTimeMillis();
        int max = SettingManager.historyMaxEntries;
        run(() -> {
            if (connection == null) return;
            try {
                connection.setAutoCommit(false);
                insert(buyerID, new Trade(now, true, sellerName, itemName, data, amount, price, taxRate, bid), max);
                if (sellerID != null) insert(sellerID, new Trade(now, false, buyerName, itemName, data, amount, price, taxRate, bid), max);
                connection.commit();
            } catch (SQLException e) {
                try { connection.rollback(); } catch (SQLException ignored) {}
                AuctionHouse.getInstance().getLogger().warning("Could not save a trade to the history: " + e.getMessage());
            } finally {
                try { connection.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        });
    }

    private static void insert(UUID player, Trade t, int max) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement(INSERT);
             PreparedStatement prune = connection.prepareStatement(PRUNE)) {
            insert.setString(1, player.toString());
            insert.setLong(2, t.time());
            insert.setBoolean(3, t.bought());
            insert.setString(4, t.otherName());
            insert.setString(5, t.itemName());
            insert.setString(6, t.itemData());
            insert.setInt(7, t.amount());
            insert.setDouble(8, t.price());
            insert.setDouble(9, t.taxRate());
            insert.setBoolean(10, t.bid());
            insert.executeUpdate();
            prune.setString(1, player.toString());
            prune.setString(2, player.toString());
            prune.setInt(3, max);
            prune.executeUpdate();
        }
    }

    // newest first. bought: null = all trades, true = only purchases, false = only sales
    public static CompletableFuture<Page> page(UUID player, Boolean bought, int offset, int limit) {
        CompletableFuture<Page> future = new CompletableFuture<>();
        boolean queued = run(() -> {
            if (connection == null) {
                future.complete(new Page(List.of(), 0));
                return;
            }
            try (PreparedStatement count = connection.prepareStatement(COUNT);
                 PreparedStatement select = connection.prepareStatement(SELECT_PAGE)) {
                bindFilter(count, player, bought);
                bindFilter(select, player, bought);
                select.setInt(4, limit);
                select.setInt(5, offset);
                int total;
                try (ResultSet rs = count.executeQuery()) {
                    total = rs.next() ? rs.getInt(1) : 0;
                }
                List<Trade> trades = new ArrayList<>();
                try (ResultSet rs = select.executeQuery()) {
                    while (rs.next()) {
                        trades.add(new Trade(rs.getLong(1), rs.getBoolean(2), rs.getString(3), rs.getString(4),
                                rs.getString(5), rs.getInt(6), rs.getDouble(7), rs.getDouble(8), rs.getBoolean(9)));
                    }
                }
                future.complete(new Page(trades, total));
            } catch (SQLException e) {
                AuctionHouse.getInstance().getLogger().warning("Could not read the trade history: " + e.getMessage());
                future.complete(new Page(List.of(), 0));
            }
        });
        if (!queued) future.complete(new Page(List.of(), 0));
        return future;
    }

    private static void bindFilter(PreparedStatement statement, UUID player, Boolean bought) throws SQLException {
        statement.setString(1, player.toString());
        statement.setBoolean(2, bought != null && bought);
        statement.setBoolean(3, bought == null || bought);
    }

    private static boolean run(Runnable task) {
        ExecutorService current = executor;
        if (current == null) return false;
        try {
            current.execute(task);
            return true;
        } catch (RejectedExecutionException e) { // the plugin is shutting down
            return false;
        }
    }

}
