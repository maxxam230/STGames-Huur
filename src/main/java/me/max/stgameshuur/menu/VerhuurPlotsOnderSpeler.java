package me.max.stgameshuur.menu;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.max.stgameshuur.Main;
import me.max.stgameshuur.configs.SpelerverhuurplotsConfig;
import me.max.stgameshuur.menu.utils.PageUtil;
import me.max.stgameshuur.objects.VerhuurdePlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class VerhuurPlotsOnderSpeler {

    private final String prefix = Main.prefix;
    private final String errorprefix = Main.errorprefix;

    public VerhuurPlotsOnderSpeler(List<VerhuurdePlot> verhuurdePlotList, Player p, int page){
        FileConfiguration verhuurplotsonderspeler = SpelerverhuurplotsConfig.getVerhuurplotsfileconfig();
        if(!verhuurplotsonderspeler.contains(p.getUniqueId().toString())){
            p.sendMessage(errorprefix);
            p.sendMessage("§cJij hebt geen plots die jij kan verhuren!");
            p.sendMessage("§7§oDit moet je regelen bij de gemeente");
            p.closeInventory();
            return;
        }

        int spaces = 45;

        ArrayList<ItemStack> plotonderspeleritems = new ArrayList<>();

        for(String plotnaamonderspeler : verhuurplotsonderspeler.getStringList(p.getUniqueId().toString())) {
            plotonderspeleritems.add(nit(plotnaamonderspeler, isVerhuurd(verhuurdePlotList, plotnaamonderspeler)));
        }

        Inventory inv = Bukkit.createInventory(null,54,"§6Plots die jij kan verhuren");

        ItemStack left;
        ItemMeta leftMeta;
        if(PageUtil.isPageValid(plotonderspeleritems,page-1,spaces)){
            left = new HeadDatabaseAPI().getItemHead("60738");
            leftMeta = left.getItemMeta();
            leftMeta.setDisplayName("§a§lLeft");
        } else {
            left = new ItemStack(Material.BARRIER);
            leftMeta = left.getItemMeta();
            leftMeta.setDisplayName("§c§lLeft");
        }
        leftMeta.setLocalizedName(page + "");
        left.setItemMeta(leftMeta);
        inv.setItem(45,left);

        ItemStack right;
        ItemMeta rightMeta;
        if(PageUtil.isPageValid(plotonderspeleritems,page+1,spaces)){
            right = new HeadDatabaseAPI().getItemHead("60740");
            rightMeta = right.getItemMeta();
            rightMeta.setDisplayName("§a§lRight");
        } else {
            right = new ItemStack(Material.BARRIER);
            rightMeta = right.getItemMeta();
            rightMeta.setDisplayName("§c§lRight");
        }
        right.setItemMeta(rightMeta);
        inv.setItem(53,right);

        for(ItemStack is : PageUtil.getPageItems(plotonderspeleritems,page,spaces)){
            inv.setItem(inv.firstEmpty(),is);
        }

        p.openInventory(inv);
    }

    private boolean isVerhuurd(List<VerhuurdePlot> verhuurdePlotList,String plotnaam){
        for(VerhuurdePlot verhuurdePlot : verhuurdePlotList){
            if(verhuurdePlot.getPlotID().equals(plotnaam)){
                return true;
            }
        }
        return false;
    }

    private ItemStack nit(String name){
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta itemm = item.getItemMeta();
        itemm.setDisplayName("§b§l"+name);
        item.setItemMeta(itemm);
        return item;
    }

    private ItemStack nit(String name, boolean verhuurd){
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta itemm = item.getItemMeta();
        itemm.setDisplayName("§b§l"+name);
        if(verhuurd){
            item.setType(Material.BARRIER);
            itemm.setDisplayName("§c§l"+name);
        }
        item.setItemMeta(itemm);
        return item;
    }


}
