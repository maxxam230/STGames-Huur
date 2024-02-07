package me.max.stgameshuur.events;

import me.max.stgameshuur.Main;
import me.max.stgameshuur.configs.VerhuurdePlotsConfig;
import me.max.stgameshuur.menu.*;
import me.max.stgameshuur.objects.AddVerhuurPlot;
import me.max.stgameshuur.objects.HuurCategory;
import me.max.stgameshuur.objects.VerhuurdePlot;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import nl.minetopiasdb.api.banking.Bankaccount;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class InvClickEvent implements Listener {

    Main plugin;

    String prefix = Main.prefix;
    String errorprefix = Main.errorprefix;


    public InvClickEvent(Main instance) {
        plugin = instance;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        if(e.getClickedInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("§6Huur Categorien §7§o- ")){
            if(!e.getClickedInventory().equals(e.getView().getTopInventory())){e.setCancelled(true); return;}
            int page = Integer.parseInt(e.getInventory().getItem(45).getItemMeta().getLocalizedName());
            if(e.getSlot() == 45 && !e.getCurrentItem().getType().equals(Material.BARRIER)){
                new CategorienMenu(getAllCategoryNames(),(Player) e.getWhoClicked(),page-1);
            }else if(e.getSlot() == 53 && !e.getCurrentItem().getType().equals(Material.BARRIER)){
                new CategorienMenu(getAllCategoryNames(),(Player) e.getWhoClicked(),page+1);
            }else if(e.getSlot() != 45 && e.getSlot() != 53){
                String catname = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                if(p.hasPermission("stgames.categorie."+catname.toLowerCase())){
                    new VerhuurdePlotsMenu(plugin.verhuurdePlotList,(Player) e.getWhoClicked(), catname);
                } else {
                    p.playSound(p, Sound.ENTITY_VILLAGER_NO,1,1);
                    p.sendMessage(errorprefix + "§cJij mag deze categorie niet bekijken");
                }
            }
            e.setCancelled(true);
        }else if(e.getClickedInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("§6Plots onder §7§o- §r§7")){
            if(!e.getClickedInventory().equals(e.getView().getTopInventory())){e.setCancelled(true); return;}
            if(e.getSlot() == 49){
                plugin.addingplot.put(p.getUniqueId(), new AddVerhuurPlot(null,e.getCurrentItem().getItemMeta().getLocalizedName(), null,null,null));
                new AddHuurPlotMenu(p,plugin.addingplot.get(p.getUniqueId()), plugin.categorien);
            } else {
                if(e.getCurrentItem().getType().equals(Material.LIME_SHULKER_BOX) || e.getCurrentItem().getType().equals(Material.ORANGE_SHULKER_BOX) || e.getCurrentItem().getType().equals(Material.RED_SHULKER_BOX)){
                    new HuurInformatieMenu(plugin.verhuurdePlotList,p,ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                }
            }
            e.setCancelled(true);
        }else if(e.getClickedInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("§6§lAdd huurcontract")){
            if(!e.getClickedInventory().equals(e.getView().getTopInventory())){e.setCancelled(true); return;}
            if(e.getSlot() == 11){
                new VerhuurPlotsOnderSpeler(plugin.verhuurdePlotList,p,1);
            }else if(e.getSlot() == 12) {
                new PlayerSelectMenu(p, 1);
            }else if(e.getSlot() == 14) {
                p.closeInventory();
                p.sendTitle("§6Type het bedrag in chat", "§cGebruik geen punten of comma's");
                plugin.addingtimeordays.put(p.getUniqueId(),2);
            }else if(e.getSlot() == 15){
                p.closeInventory();
                p.sendTitle("§6Type aantal dagen in chat", "§cJe berricht wordt niet verstuurd!");
                plugin.addingtimeordays.put(p.getUniqueId(),1);
            }else if(e.getSlot() == 38) {
                p.closeInventory();
                removeFromAddingMap(p);
                p.sendMessage(prefix + "§aAanmaken van huurcontract geannuleerd!");
            }else if(e.getSlot() == 42){
                if(e.getView().getTopInventory().contains(Material.BARRIER)){
                    p.closeInventory();
                    p.sendMessage(errorprefix+"§cJe hebt niet alle informatie ingevuld!");
                    p.sendMessage(errorprefix+"§7Voeg alle informatie toe!");
                } else {
                    AddVerhuurPlot addVerhuurPlot = plugin.addingplot.get(p.getUniqueId());
                    Inventory inv = e.getView().getTopInventory();
                    UUID uuid = Bukkit.getOfflinePlayer(inv.getItem(21).getItemMeta().getDisplayName()).getUniqueId();
                    Integer rekeningID = Integer.parseInt(inv.getItem(40).getItemMeta().getLocalizedName());
                    ConfirmContract(p,new VerhuurdePlot(uuid, addVerhuurPlot.getPlot(),0, addVerhuurPlot.getPrijs(),rekeningID,addVerhuurPlot.getDagen(),false,0, addVerhuurPlot.getCategorie()));
                    p.closeInventory();
                    removeFromAddingMap(p);
                }
            }
            e.setCancelled(true);
        }else if(e.getClickedInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("§6Kies speler voor huurcontract")){
            if(!e.getClickedInventory().equals(e.getView().getTopInventory())){e.setCancelled(true); return;}
            if(e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)){
                String playername = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                if(playername.equalsIgnoreCase(p.getName())){p.playSound(p,Sound.ENTITY_VILLAGER_NO,1,1); e.setCancelled(true); return;}
                plugin.addingplot.get(p.getUniqueId()).setHuurder(playername);
                new AddHuurPlotMenu(p, plugin.addingplot.get(p.getUniqueId()),plugin.categorien);
            }
            e.setCancelled(true);
        }else if(e.getClickedInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("§6Plots die jij kan verhuren")) {
            if(!e.getClickedInventory().equals(e.getView().getTopInventory())){e.setCancelled(true); return;}
            if (e.getCurrentItem().getType().equals(Material.BOOK)) {
                plugin.addingplot.get(p.getUniqueId()).setPlot(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                new AddHuurPlotMenu(p, plugin.addingplot.get(p.getUniqueId()),plugin.categorien);
            }
            e.setCancelled(true);
        }else if(e.getClickedInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().equals("§6Bevestig Contract")) {
            if(!e.getClickedInventory().equals(e.getView().getTopInventory())){e.setCancelled(true); return;}
            if(e.getSlot() == 29){
                removeFromConfirmingMap(p);
                p.closeInventory();
            } else if(e.getSlot() == 33){
                plugin.initializeHuur(p,plugin.confirmhuurcontract.get(p.getUniqueId()));
            }
            e.setCancelled(true);
        }else if(e.getClickedInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("§6Huur Info: §b")) {
            if(!e.getClickedInventory().equals(e.getView().getTopInventory())){e.setCancelled(true); return;}
            if(e.getSlot() == 30) {
                p.closeInventory();
                plugin.removeVerhuurdePlot(p, e.getCurrentItem().getItemMeta().getLocalizedName());
            }else if(e.getSlot() == 31){
                plugin.setpriceforplot.put(p.getUniqueId(),getVerhuurdPlotByName(ChatColor.stripColor(e.getView().getTopInventory().getItem(12).getItemMeta().getDisplayName())));
                p.closeInventory();
                p.sendTitle("§6Type het bedrag in chat", "§cGebruik geen punten of comma's");
            }else if(e.getSlot() == 32){
                if(!e.getCurrentItem().getType().equals(Material.BARRIER)){
                    if(Bukkit.getOfflinePlayer(ChatColor.stripColor(e.getView().getTopInventory().getItem(10).getItemMeta().getDisplayName())).isOnline()){
                        p.closeInventory();
                        plugin.betaalachterstand(p,ChatColor.stripColor(e.getView().getTopInventory().getItem(10).getItemMeta().getDisplayName()));
                    }else{
                        p.sendMessage(errorprefix + "§cDe huurder is niet online");
                        p.playSound(p,Sound.ENTITY_VILLAGER_NO,1,1);
                    }
                } else {
                    p.playSound(p,Sound.ENTITY_VILLAGER_NO,1,1);
                }
            } else {
                new CategorienMenu(getAllCategoryNames(),(Player) e.getWhoClicked(),1);
            }
            e.setCancelled(true);
        }
    }

    private void removeFromAddingMap(Player p){
        Iterator<Map.Entry<UUID, AddVerhuurPlot>> iterator = plugin.addingplot.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, AddVerhuurPlot> entry = iterator.next();
            if (entry.getKey().equals(p.getUniqueId())) {
                iterator.remove();
                break;
            }
        }
    }

    private void removeFromConfirmingMap(Player p){
        Iterator<Map.Entry<UUID, VerhuurdePlot>> iterator = plugin.confirmhuurcontract.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, VerhuurdePlot> entry = iterator.next();
            if (entry.getKey().equals(p.getUniqueId())) {
                iterator.remove();
                break;
            }
        }
    }


    private VerhuurdePlot getVerhuurdPlotByName(String plotname){
        for(VerhuurdePlot verhuurdePlot : plugin.verhuurdePlotList){
            if(verhuurdePlot.getPlotID().equals(plotname)){
                return verhuurdePlot;
            }
        }
        return null;
    }


    private void ConfirmContract(Player p, VerhuurdePlot verhuurdePlot){
        plugin.confirmhuurcontract.put(verhuurdePlot.getPlayerUUID(),verhuurdePlot);
        p.sendMessage(prefix + "§aDe huurder moet het contract tekenen");
        Player huurder = (Player) Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID());
        TextComponent message = new TextComponent(prefix+"§3Klik hier om het huurcontract te tekenen");
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/huur bevestig"));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aBevestig huur betaling prijs: §b" + verhuurdePlot.getPrice() + ",-").create()));
        huurder.spigot().sendMessage(message);
    }

    private ArrayList<String> getAllCategoryNames(){
        ArrayList<String> allcategory =  new ArrayList<>();
        for(HuurCategory huurCategory : plugin.categorien){
            allcategory.add(huurCategory.getCategory());
        }
        return allcategory;
    }
}
