package com.janjidan.mythicmobsspawnpoints.command;

import com.janjidan.mythicmobsspawnpoints.MythicMobsSpawnPoints;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigManager {
    private MythicMobsSpawnPoints plugin;
    private FileConfiguration config;

    public ConfigManager(MythicMobsSpawnPoints plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void configManager(){
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        if(!OneGroup()){
            plugin.getLogger().log(Level.SEVERE, "请检查配置文件是否有重复添加刷怪点");
        }
        //FileConfiguration config = plugin.getConfig();
    }
    public void list(CommandSender sender){
        LinkedList<String> list = new LinkedList<>();
        sender.sendMessage("全部组: "+configlist());
        sender.sendMessage("全部组数: "+configlist().size());
        sender.sendMessage("全部组的刷怪点: "+ configgrouplist());
        sender.sendMessage("default组的刷怪点1: "+ configgrouplist());
        String configgrouplist = String.valueOf(configgrouplist());
        sender.sendMessage("default组的刷怪点2: "+ configgrouplist.contains("1") );
    }
    public LinkedList<String> configlist(){
        ConfigurationSection configlist = config.getConfigurationSection("options.spawn-list");
        LinkedList<String> list = new LinkedList<>();
        if (configlist != null) {
            for (String s : configlist.getKeys(false)){
                list.add(s);
            }
            return list;
        }
        return null;
    }

    public LinkedList<String> configgrouplist(){
        LinkedList<String> list = new LinkedList<>();
        for (int i = 0; i < configlist().size();){
            String o = config.getString("options.spawn-list."+configlist().get(i) +".spawnpoints" );
            list.add(o);
            i++;
        }
        return list;
    }



    //是否唯一
    //是true
    //否false
    public boolean OneGroup(){
        for (int i = 0; i < configgrouplist().size();){
            if(hasUniqueElements(configgrouplist())) {
                return true;
            }
            i++;
        }
        return false;
    }
    private static boolean hasUniqueElements(LinkedList<String> list) {
        Set<String> set = new HashSet<>();

        for (String value : list) {
            // 使用正则表达式提取数字
            String[] numbers = value.replaceAll("[^0-9,]", "").split(",");
            // 将提取的数字重新组合成一个字符串
            String reconstructedValue = "[" + String.join(",", numbers) + "]";
            // 将重新组合的字符串添加到Set中
            set.add(reconstructedValue);
        }

        return set.size() == list.size();
    }

    /*参数是mm刷怪点id
    * 返回值是刷怪点id所在插件的组
    * 用变量s依次判断是否在组中
    * 返回值是插件组名
    * */
    public String SearchGroup(String s){
        for (int i = 0; i < configlist().size();){
            String o = config.getString("options.spawn-list."+configlist().get(i) +".spawnpoints" );
            if(o.contains(s)){ return configlist().get(i); }
            i++;
        }
        return null;
    }

    public LinkedList <String> allGroup(String s){
        if(s == null){
            return null;
        }else {
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(s);
            LinkedList <String> list = new LinkedList<>();
            while (matcher.find()) {
                String number = matcher.group();
                list.add(number);
            }
            return list;
        }
    }


}
