package me.elaineqheart.auctionHouse;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class TaskManager {

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

    // *usage in another class:

    // TaskManager.addTaskID(uuid,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    // TaskManager.cancelTask(uuid);

}
