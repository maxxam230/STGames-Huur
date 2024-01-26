package me.max.stgameshuur.events;

import me.max.stgameshuur.Main;
import me.max.stgameshuur.menu.CategorienMenu;
import me.max.stgameshuur.objects.HuurCategory;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class NPCRightClick implements Listener {

    Main plugin;

    String prefix = Main.prefix;
    String errorprefix = Main.errorprefix;

    public NPCRightClick(Main instance) {
        plugin = instance;
    }

    @EventHandler
    public void npcclick(NPCRightClickEvent event){
        if(plugin.npcIDs.contains(event.getNPC().getId())){
            new CategorienMenu(getAllCategoryNames(),event.getClicker(),1);
        }
    }

    private ArrayList<String> getAllCategoryNames(){
        ArrayList<String> allcategory =  new ArrayList<>();
        if(plugin.categorien == null){return null;}
        for(HuurCategory huurCategory : plugin.categorien){
            allcategory.add(huurCategory.getCategory());
            return allcategory;
        }
        return null;
    }

}
