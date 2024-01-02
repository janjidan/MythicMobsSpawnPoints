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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

public class MobSpawnerManager implements Listener {


    private static int mdc;
    private static long ticksSinceLastKill;
    private static int lastKillTime;
    private static int counters;
    private static String group;
    private static Boolean fromMythicSpawner;
    private static MythicMobSpawnEvent MobSpawn;
    private static String mname;
    private static MythicMobsSpawnPoints plugin;

    private static int currentTime;

    @EventHandler
    public void OnMythicMobSpawnEvent(MythicMobSpawnEvent e) {
        if (e != null) {
            this.MobSpawn = e;
            fromMythicSpawner = MobSpawn.isFromMythicSpawner();
        }
    }

    @EventHandler
    public void onEntityDeath(MythicMobDeathEvent e) {
        //SpawnerTask s = new SpawnerTask((MythicMobsSpawnPoints) MythicMobsSpawnPoints.instance);
        com.janjidan.mythicmobsspawnpoints.command.ConfigManager configm = new ConfigManager((MythicMobsSpawnPoints) MythicMobsSpawnPoints.instance);
        //MythicSpawner mythicSpawner = new MythicSpawner(e.getMob().getName());
        Player killer;
        FileConfiguration config = MythicMobsSpawnPoints.instance.getConfig();
        String configgrouplist = String.valueOf(configm.configgrouplist());
        String spawnpoints = config.getString("options.spawn-list." + group + ".spawnpoints");
        if (e.getMob().getSpawner() == null) {
            return;
        } else {
            mname = e.getMob().getSpawner().getName();
        }

        if(e.getKiller() == null){
            return;
        }else {
            killer = (Player) e.getKiller();
        }
/*        killer.sendMessage("================");
        killer.sendMessage("判定是否含有" + configgrouplist.contains(mname));
        killer.sendMessage("判定是否含有" + configgrouplist);
        killer.sendMessage("判定是否含有" + MobSpawnerManager.mname);
        killer.sendMessage("判定是否含有" + mname );
        killer.sendMessage("判定是否含有:" + e.getMob().getSpawner().getName());
        killer.sendMessage("================");*/


        //判断"options.spawn-list"键里组的spawnpoints的刷怪点
        if (configgrouplist.contains(mname) && fromMythicSpawner) {
            group = configm.SearchGroup(mname);
            //MythicMobsSpawnPoints.instance.getLogger().info("击杀玩家："+ killer +"当前计数：" + mdc + "怪物类型："+e.getMobType());
            //MythicMobsSpawnPoints.instance.getLogger().info("返回值："+(mobType).contains("MythicMob"));
            //判断是否为MythicMobs，是则++
            mdc = config.getInt("options.spawn-list." + group + ".currentcounter");
            counters = config.getInt("options.spawn-list." + group + ".counters");
                /*killer.sendMessage("计数点：" + mdc);
                killer.sendMessage("计数：" + counters);
                killer.sendMessage("判定是否含有：" + configgrouplist.contains(MobSpawnerManager.mname));
                killer.sendMessage("mname：" + MobSpawnerManager.mname);
                killer.sendMessage("configgrouplist：" + configgrouplist);
                killer.sendMessage("================");*/
            if (mdc < counters) {
                mdc++;
                config.set("options.spawn-list." + group + ".currentcounter", mdc);
                ticksSinceLastKill = config.getInt("options.spawn-list." + group + ".ticksSinceLastKill");
                lastKillTime = (int) (System.currentTimeMillis() / 1000);
                config.set("options.spawn-list." + group + ".lastKillTime", lastKillTime);
                new MobSpawnerManager.MobDeathTask().runTaskTimerAsynchronously(MythicMobsSpawnPoints.instance, 0L, ticksSinceLastKill);
                //new MobSpawnerManager.MobDeathTask().runTaskAsynchronously(MythicMobsSpawnPoints.instance);
                //new MobSpawnerManager.MobDeathTask().runTaskAsynchronously(MythicMobsSpawnPoints.instance);
                killer.sendMessage("当前组" + group + "计数：" + mdc);
                //killer.sendMessage("当前组" + group + "刷怪点：" + config.getString("options.spawn-list." + group + ".spawnpoints"));
                //killer.sendMessage("当前冷却：" +MobSpawn.getMythicSpawner().getCooldownSeconds());
                //killer.sendMessage("当前生成组：" + MobSpawn.getMythicSpawner().getGroup());
                //killer.sendMessage("生成程序的id：" + MobSpawn.getMythicSpawner().getName() );
                saveConfig();
            }
            //当mdc是10时,在击杀者的位置召唤一个zombie，并重置mdc为0
            if (mdc == counters) {
                killer.sendMessage("刷怪点重置");
                //reloadgroup(spawnpoints);
                    //World w = killer.getLocation().getWorld();
                    //w.spawnEntity(killer.getLocation(), EntityType.ZOMBIE);
                    //int lastCooldownSeconds = MobSpawn.getMythicSpawner().getCooldownSeconds();
                    //MobSpawn.getMythicSpawner().setCooldownSeconds(0);

                    LinkedList<String> groupstrings = configm.allGroup(spawnpoints);

                    //遍历组里的刷怪点
                    for (int i = 0; i < groupstrings.size(); ) {
                        //gs是组里面的所有刷怪点名字
                        try {
                            MythicSpawner ms = MythicBukkit.inst()
                                    .getSpawnerManager()
                                    .getSpawnerByName(groupstrings.get(i));
                            ms.setRemainingCooldownSeconds(0);
                        } catch (Exception a) {
                            return;
                        }
                        i++;
                    }
                    e.getMob().getSpawner().setRemainingCooldownSeconds(0);
                //MobSpawn.getMythicSpawner().setRemainingCooldownSeconds(0);
                mdc = 0;
                config.set("options.spawn-list." + group + ".currentcounter", mdc);
                saveConfig();
            }
        }
    }

    public void reloadgroup(String s) {
        ConfigManager configm = new ConfigManager(plugin);
        LinkedList<String> groupstrings = configm.allGroup(s);
        Set<String> set = groupstrings.stream().collect(Collectors.toSet());
        MythicBukkit
                .inst()
                .getSpawnerManager()
                .listSpawners
                .stream()
                .parallel()
                .filter(a -> set.contains(a.getName()))
                .filter(a -> a.getLocation().isLoaded())
                .forEach(a -> a.setCooldownSeconds(0));
    }

    public static class MobDeathTask extends BukkitRunnable {
        @Override
        public void run() {
            com.janjidan.mythicmobsspawnpoints.command.ConfigManager configm = new ConfigManager((MythicMobsSpawnPoints) MythicMobsSpawnPoints.instance);
            FileConfiguration config = MythicMobsSpawnPoints.instance.getConfig();
            currentTime = (int) (System.currentTimeMillis() / 1000);
            config.set("options.spawn-list." + group + ".currentTime", currentTime);
            //需要判断mdc是否大于0，如果是则--，还需要判断
/*            MythicMobsSpawnPoints.instance.getLogger().info(String.valueOf(currentTime - lastKillTime >= ticksSinceLastKill));
            MythicMobsSpawnPoints.instance.getLogger().info(String.valueOf(mdc > 0));
            MythicMobsSpawnPoints.instance.getLogger().info(String.valueOf(currentTime));
            Bukkit.broadcastMessage ("时间1：" + currentTime);
            Bukkit.broadcastMessage ("时间2：" + lastKillTime);*/
            if (currentTime - lastKillTime >= ticksSinceLastKill && mdc > 0) {
                //currentTime = (int) (currentTime + ticksSinceLastKill);
                mdc--;
                lastKillTime = (int) (System.currentTimeMillis() / 1000);
                config.set("options.spawn-list." + group + ".currentcounter", mdc);
                //config.set("options.spawn-list." + group + ".currentTime", currentTime);
                Bukkit.broadcastMessage("点数衰减，当前组" + group + " 点数为：" + mdc);
                /*
                Bukkit.broadcastMessage("当前任务组id" + getTaskId());
                Bukkit.broadcastMessage("所有激活的任务" + Bukkit.getScheduler().getActiveWorkers());*/
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

