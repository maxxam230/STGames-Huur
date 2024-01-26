package me.max.stgameshuur.events;

import me.max.stgameshuur.Main;
import me.max.stgameshuur.configs.VerhuurdePlotsConfig;
import me.max.stgameshuur.menu.CategorienMenu;
import me.max.stgameshuur.menu.VerhuurdePlotsMenu;
import me.max.stgameshuur.objects.HuurCategory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
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
        if(e.getClickedInventory() != null && !e.getClickedInventory().equals(e.getView().getTopInventory())){e.setCancelled(true); return;}
        if(e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("§6Huur Categorien §7§o- ") && e.getInventory().equals(e.getView().getTopInventory())){
            int page = Integer.parseInt(e.getInventory().getItem(45).getItemMeta().getLocalizedName());
            if(e.getSlot() == 45 && !e.getCurrentItem().getType().equals(Material.BARRIER)){
                new CategorienMenu(getAllCategoryNames(),(Player) e.getWhoClicked(),page-1);
            }else if(e.getSlot() == 53 && !e.getCurrentItem().getType().equals(Material.BARRIER)){
                new CategorienMenu(getAllCategoryNames(),(Player) e.getWhoClicked(),page+1);
            }else if(e.getSlot() != 45 && e.getSlot() != 53){
                Player p = (Player) e.getWhoClicked();
                String catname = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                if(p.hasPermission("stgames.categorie."+catname)){
                    new VerhuurdePlotsMenu(getAllPlotUnderCategory(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())),(Player) e.getWhoClicked(), catname);
                } else {
                    p.playSound(p, Sound.ENTITY_VILLAGER_NO,1,1);
                    p.sendMessage(errorprefix + "§cJij mag deze categorie niet bekijken");
                }
            }
            e.setCancelled(true);
        }else if(e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("§6Plots onder categorie §7§o- §r§7")&& e.getInventory().equals(e.getView().getTopInventory())){
            if(e.getSlot() == 2){

            }
            e.setCancelled(true);
        }
    }


    private ArrayList<String> getAllPlotUnderCategory(String categorie){
        ArrayList<String> allplotsundercategory =  new ArrayList<>();
        FileConfiguration huurplotscat = VerhuurdePlotsConfig.getVerhuurdecategorienfileconfig();
        if(!huurplotscat.contains(categorie)){return allplotsundercategory;}
        allplotsundercategory.addAll(huurplotscat.getConfigurationSection(categorie).getKeys(false));
        return allplotsundercategory;
    }

    private ArrayList<String> getAllCategoryNames(){
        ArrayList<String> allcategory =  new ArrayList<>();
        for(HuurCategory huurCategory : plugin.categorien){
            allcategory.add(huurCategory.getCategory());
            return allcategory;
        }
        return allcategory;
    }
}
