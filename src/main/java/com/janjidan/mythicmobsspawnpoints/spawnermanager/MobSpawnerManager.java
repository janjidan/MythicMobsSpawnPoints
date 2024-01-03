package com.janjidan.mythicmobsspawnpoints.spawnermanager;

import com.janjidan.mythicmobsspawnpoints.MythicMobsSpawnPoints;
import com.janjidan.mythicmobsspawnpoints.command.ConfigManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.spawning.spawners.SpawnerManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class MobSpawnerManager implements Listener {
    private int mdc;
    private long ticksSinceLastKill;
    private int lastKillTime;
    private int counters;
    private String group;
    private Set<UUID> isFromMythicSpawner=new HashSet<>();
    private int currentTime;

    @EventHandler
    public void OnMythicMobSpawnEvent(MythicMobSpawnEvent e) {
        isFromMythicSpawner.add(e.getEntity().getUniqueId());
    }

    @EventHandler
    public void onEntityDeath(MythicMobDeathEvent e) {
        ConfigManager configm = new ConfigManager((MythicMobsSpawnPoints) MythicMobsSpawnPoints.instance);
        Entity killer = e.getKiller();
        if (!(killer instanceof Player)) { // 原来的写法存在的问题是：如果不是玩家，而是，比如说，铁傀儡杀死的怪物，就会产生ClassCastException
            return;
        }
        FileConfiguration config = MythicMobsSpawnPoints.instance.getConfig();
        List<String> configGroupList = configm.configgrouplist(); // 此处为什么要转成String？List有contains方法，直接用就好了。
        String spawnPointsKeyName = config.getString("options.spawn-list." + group + ".spawnpoints");
        if (e.getMob().getSpawner() == null) {
            return;
        }
        String spawnerName = e.getMob().getSpawner().getName(); // spawnerName 显然是一个局部变量，不要写成全局变量。
        if (configGroupList.contains(spawnerName) && isFromMythicSpawner.contains(e.getEntity().getUniqueId())) {
            // 考虑以下情况：在一个tick中有多于一个的怪物死亡，那么方法的执行顺序是怎样的？一个boolean真的能解决这个问题吗？
            group = configm.SearchGroup(spawnerName);
            mdc = config.getInt("options.spawn-list." + group + ".currentcounter");
            counters = config.getInt("options.spawn-list." + group + ".counters");
            if (mdc < counters) {
                mdc++;
                config.set("options.spawn-list." + group + ".currentcounter", mdc);
                ticksSinceLastKill = config.getInt("options.spawn-list." + group + ".ticksSinceLastKill");
                lastKillTime = (int) (System.currentTimeMillis() / 1000);
                config.set("options.spawn-list." + group + ".lastKillTime", lastKillTime);
                new MobSpawnerManager.MobDeathTask().runTaskTimerAsynchronously(MythicMobsSpawnPoints.instance, 0L, ticksSinceLastKill);
                killer.sendMessage("当前组" + group + "计数：" + mdc);
                saveConfig();
            }
            if (mdc == counters) {
                killer.sendMessage("刷怪点重置");
                LinkedList<String> groupstrings = configm.allGroup(spawnPointsKeyName);
                for (String s : groupstrings) {
                    try {
                        MythicSpawner ms = MythicBukkit.inst()
                                .getSpawnerManager()
                                .getSpawnerByName(s);
                        // LinkedList指的是链表，按照下标查找元素的时间复杂度是O(n)，因此原来的方法遍历链表的时间复杂度是O(n^2)。
                        // 而用增强for循环遍历链表的时间复杂度是O(n)，因此这里的时间复杂度是O(n)，比原来的方法快一个数量级。
                        ms.setRemainingCooldownSeconds(0);
                    } catch (Exception a) {
                        return;
                    }
                }
                e.getMob().getSpawner().setRemainingCooldownSeconds(0);
                mdc = 0;
                config.set("options.spawn-list." + group + ".currentcounter", mdc);
                saveConfig();
            }
        }
    }

    public class MobDeathTask extends BukkitRunnable {
        @Override
        public void run() {
            com.janjidan.mythicmobsspawnpoints.command.ConfigManager configm = new ConfigManager((MythicMobsSpawnPoints) MythicMobsSpawnPoints.instance);
            FileConfiguration config = MythicMobsSpawnPoints.instance.getConfig();
            currentTime = (int) (System.currentTimeMillis() / 1000);
            config.set("options.spawn-list." + group + ".currentTime", currentTime);
            if (currentTime - lastKillTime >= ticksSinceLastKill && mdc > 0) {
                mdc--;
                lastKillTime = (int) (System.currentTimeMillis() / 1000);
                config.set("options.spawn-list." + group + ".currentcounter", mdc);
                Bukkit.broadcastMessage("点数衰减，当前组" + group + " 点数为：" + mdc);
            } else if (mdc == 0) {
                cancel();
            }
            saveConfig();
        }

    }

    private static void saveConfig() {
        MythicMobsSpawnPoints.instance.saveConfig();
    }
}