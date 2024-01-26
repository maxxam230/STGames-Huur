package me.max.stgameshuur.configs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SpelerverhuurplotsConfig {

    private static File verhuurplotsconfig;
    private static FileConfiguration verhuurplotsfileconfig;

    public static void createCategorienConfig(){
        verhuurplotsconfig = new File(Bukkit.getServer().getPluginManager().getPlugin("STGames-Huur").getDataFolder(),"SpelersVerhuurPlotsConfig.yml");
        if(!verhuurplotsconfig.exists()){
            try {
                verhuurplotsconfig.createNewFile();
            }catch (IOException e){
                Bukkit.getConsoleSender().sendMessage("Couldn't create spelersverhuurplots level config");
            }
        }
        verhuurplotsfileconfig = YamlConfiguration.loadConfiguration(verhuurplotsconfig);
    }

    public static FileConfiguration getVerhuurplotsfileconfig(){
        return verhuurplotsfileconfig;
    }

    public static void save(){
        try{
            verhuurplotsfileconfig.save(verhuurplotsconfig);
        }catch(IOException e){
            Bukkit.getConsoleSender().sendMessage("Couldn't save spelersverhuurplots level config");
        }
    }

}
