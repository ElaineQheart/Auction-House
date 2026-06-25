package me.elaineqheart.auctionHouse;

public class TaskManager {

    public static int GUIUpdateTick = 20;
    //private static final HashMap<UUID, Integer> taskID = new HashMap<>();
    //private static final HashMap<LocalTime, Integer> taskHistory = new HashMap<>();

//    public static void cancelTask(UUID key) {
//        assert key != null;
//        if (!taskID.containsKey(key)) return;
//        //taskHistory.put(LocalTime.now(), taskID.get(key));
//        Bukkit.getScheduler().cancelTask(taskID.get(key)); // Not folia supported
//        taskID.remove(key);
//    }
//
//    public static void addTaskID(UUID uniqueId, int taskId) {
//        taskID.put(uniqueId,taskId);
//    }

//    public static void printStackTrace() {
//        AuctionHouse.getPlugin().getLogger().info("Log size: " + taskHistory.size());
//        List<LocalTime> timeList = taskHistory.keySet().stream().toList();
//        for (LocalTime time : timeList.stream().sorted().toList()) {
//            AuctionHouse.getPlugin().getLogger().info(time.toString() + ": " + taskHistory.get(time));
//        }
//    }

    // *usage in another class:

    // TaskManager.addTaskID(uuid,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    // TaskManager.cancelTask(uuid);

}
