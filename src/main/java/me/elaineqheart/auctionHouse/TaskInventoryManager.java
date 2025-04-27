package me.elaineqheart.auctionHouse;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class TaskInventoryManager {

    private static final HashMap<String, Integer> taskID = new HashMap<>();

    public static void cancelTask(UUID invID) {
        String key = invID.toString();
        if(taskID.containsKey(key)) {
            Bukkit.getScheduler().cancelTask(taskID.get(key));
            taskID.remove(key);
        }
    }

    public static void addTaskID(UUID uniqueId, int taskId) {
        taskID.put(uniqueId.toString(),taskId);
    }

}
