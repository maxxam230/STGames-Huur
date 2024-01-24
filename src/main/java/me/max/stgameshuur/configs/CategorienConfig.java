package me.max.stgameshuur.configs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CategorienConfig {

    private static File categorienconfig;
    private static FileConfiguration categorienfileconfig;

    public static void createCategorienConfig(){
        categorienconfig = new File(Bukkit.getServer().getPluginManager().getPlugin("STGames-Huur").getDataFolder(),"HuurCategorienConfig.yml");
        if(!categorienconfig.exists()){
            try {
                categorienconfig.createNewFile();
            }catch (IOException e){
                Bukkit.getConsoleSender().sendMessage("Couldn't create categorien level config");
            }
        }
        categorienfileconfig = YamlConfiguration.loadConfiguration(categorienconfig);
    }

    public static FileConfiguration getCategorienfileconfig(){
        return categorienfileconfig;
    }

    public static void save(){
        try{
            categorienfileconfig.save(categorienconfig);
        }catch(IOException e){
            Bukkit.getConsoleSender().sendMessage("Couldn't save fish level config");
        }
    }

}
