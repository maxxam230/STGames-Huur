package me.max.stgameshuur.events;

import me.max.stgameshuur.Main;
import me.max.stgameshuur.menu.CategorienMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
            new CategorienMenu(plugin.categorien,event.getClicker(),1);
        }
    }

}
