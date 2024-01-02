package com.janjidan.mythicmobsspawnpoints.command;

import com.janjidan.mythicmobsspawnpoints.MythicMobsSpawnPoints;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CommandHandler implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender Sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        ConfigManager configm = new ConfigManager((MythicMobsSpawnPoints) MythicMobsSpawnPoints.instance);
        if(Sender.isOp() && args.length == 1){
            switch (args[0].toLowerCase()) {
                case "help":
                    break;
                case "reload":
                    configm.configManager();
                    Sender.sendMessage("§a配置文件已重载");
                    break;
                case "create":
                    break;
                case "delete":
                    break;
                case "list":
                    configm.list(Sender);
                    break;
                case "moblist":
                    configm.configlist();
                    //config.getConfigurationSection("options.spawn-list");
                    break;
                default:
                    Sender.sendMessage("§c未知指令");
                    return true;
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        LinkedList<String> cmdlist = new LinkedList<>();
        if (args.length == 1){
            List<String> firstArgList = Arrays.asList("help", "reload", "create", "delete", "list");
            if (args[0].isEmpty()) {
                //添加所有信息
                cmdlist.addAll(firstArgList);
                return cmdlist;
            }else {
                //已经开始输入字符了，则遍历已有的信息，并将信息的小写toLowerCase()通过startsWith()检查该args[0]的小写是否匹配
                for(String firstArg : firstArgList){
                    if (firstArg.toLowerCase().startsWith(args[0].toLowerCase())) {
                        cmdlist.add(firstArg);
                    }
                }
                return cmdlist;
            }
        }
        return cmdlist;
    }


}
