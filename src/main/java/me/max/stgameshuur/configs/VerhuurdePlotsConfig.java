package me.max.stgameshuur.configs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class VerhuurdePlotsConfig {

    private static File verhuurdecategorienconfig;
    private static FileConfiguration verhuurdecategorienfileconfig;

    public static void createCategorienConfig(){
        verhuurdecategorienconfig = new File(Bukkit.getServer().getPluginManager().getPlugin("STGames-Huur").getDataFolder(),"VerhuurdeHuurCategorienConfig.yml");
        if(!verhuurdecategorienconfig.exists()){
            try {
                verhuurdecategorienconfig.createNewFile();
            }catch (IOException e){
                Bukkit.getConsoleSender().sendMessage("Couldn't create categorien level config");
            }
        }
        verhuurdecategorienfileconfig = YamlConfiguration.loadConfiguration(verhuurdecategorienconfig);
    }

    public static FileConfiguration getVerhuurdecategorienfileconfig(){
        return verhuurdecategorienfileconfig;
    }

    public static void save(){
        try{
            verhuurdecategorienfileconfig.save(verhuurdecategorienconfig);
        }catch(IOException e){
            Bukkit.getConsoleSender().sendMessage("Couldn't save fish level config");
        }
    }

}
