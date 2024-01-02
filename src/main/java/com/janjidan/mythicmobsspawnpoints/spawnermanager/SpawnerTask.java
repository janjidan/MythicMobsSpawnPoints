package com.janjidan.mythicmobsspawnpoints.spawnermanager;

import com.janjidan.mythicmobsspawnpoints.MythicMobsSpawnPoints;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SpawnerTask implements Listener {
    private final MythicMobsSpawnPoints plugin;


    public SpawnerTask(MythicMobsSpawnPoints plugin){
        this.plugin = plugin;
    }


/*    @EventHandler
    public void MobDeathEvent(MythicMobDeathEvent e) {
        SpawnerManager s = new SpawnerManager();
        int ticksSinceLastKill = s.ticksSinceLastKill;
        new BukkitRunnable() {
            @Override
            public void run() {
                int ticksSinceLastKill = s.ticksSinceLastKill;
                if(s.mdc > 0 &&  ticksSinceLastKill == 0){
                    s.mdc--;
                    e.getKiller().sendMessage("点数衰减，当前点数为："+s.mdc);
                }else {
                    e.getKiller().sendMessage("计数器已重置当前数字：" + s.ticksSinceLastKill);
                    this.cancel();
                }

            }
        };
    }*/


}
