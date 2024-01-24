package me.max.stgameshuur.events;

import me.max.stgameshuur.Main;
import me.max.stgameshuur.menu.CategorienMenu;
import me.max.stgameshuur.menu.VerhuurdePlotsMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class InvClickEvent implements Listener {

    Main plugin;

    String prefix = Main.prefix;
    String errorprefix = Main.errorprefix;

    public InvClickEvent(Main instance) {
        plugin = instance;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("§6Huur Categorien §7§o- ")){
            int page = Integer.parseInt(e.getInventory().getItem(45).getItemMeta().getLocalizedName());
            if(e.getSlot() == 45 && !e.getCurrentItem().getType().equals(Material.BARRIER)){
                new CategorienMenu(plugin.categorien,(Player) e.getWhoClicked(),page-1);
            }else if(e.getSlot() == 53 && !e.getCurrentItem().getType().equals(Material.BARRIER)){
                new CategorienMenu(plugin.categorien,(Player) e.getWhoClicked(),page+1);
            }else if(e.getSlot() != 45 && e.getSlot() != 53){
                Player p = (Player) e.getWhoClicked();
                String catname = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                if(p.hasPermission("stgames.huur.categorie."+catname+".add")){
                    ArrayList<String> plotsondercat = new ArrayList<>();
                    new VerhuurdePlotsMenu(plotsondercat,(Player) e.getWhoClicked(), catname);
                } else {
                    p.playSound(p, Sound.ENTITY_VILLAGER_NO,1,1);
                    p.sendMessage(errorprefix + "§cJij mag deze categorie niet bekijken");
                }
            }
            e.setCancelled(true);
        }else if(e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("§6Verhuurdeplots §7§o- §r§7")){
            e.setCancelled(true);
        }
    }
}