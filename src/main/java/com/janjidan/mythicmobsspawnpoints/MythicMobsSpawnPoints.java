package com.janjidan.mythicmobsspawnpoints;

import com.janjidan.mythicmobsspawnpoints.command.CommandHandler;
import com.janjidan.mythicmobsspawnpoints.spawnermanager.MobSpawnerManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class MythicMobsSpawnPoints extends JavaPlugin {

    /*击杀怪物开始计时_s后刷新该怪物，并单独为该场景刷怪点加击怪点数1点，
       每_s后减1点点数，至0点为止，累计10点后，直接刷新全部怪物，并清空点数
       补充说明：所有玩家在当前场景击杀都为当前该场景加点数，
       目的为计算场景玩家击杀怪物速度，智能调节刷新效率，
       慢时正常刷新，多人清场时则快速刷新 .*/

    public static JavaPlugin instance;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        getServer().getPluginManager().registerEvents(new MobSpawnerManager(),this);
        PluginCommand cmd = getCommand("mythicmobsspawnpoints");
        cmd.setExecutor(new CommandHandler());
        //Bukkit.getPluginManager().registerEvents(new SpawnerManager(),this);
        //Bukkit.getPluginCommand("mythicmobsspawnpoints").setExecutor(new CommandHandler());
    }
    @Override
    public void onLoad(){
        saveDefaultConfig();
        // 如果配置文件不存在，Bukkit 会保存默认的配置
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic

        // 保存配置
        saveConfig();
    }
}
