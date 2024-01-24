package me.max.stgameshuur.menu;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.max.stgameshuur.menu.utils.PageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class VerhuurdePlotsMenu {


    public VerhuurdePlotsMenu(ArrayList<String> categorien, Player p, String categorie){
        if(categorien.isEmpty() && p.hasPermission("stgames.huur.categorie."+categorie+".add")){
            p.openInventory(emptyPage(categorie));
            return;
        }

        int page = 1;
        int spaces = 45;

        ArrayList<ItemStack> allcategorien = new ArrayList<>();

        for(String cat : categorien){
            if(p.hasPermission("stgames.huur.categorie." + cat)) {
                //allcategorien.add(nit(cat));
            }
        }

        Inventory inv = Bukkit.createInventory(null,54,"§6Verhuurdeplots §7§o- §r§7" + categorie);
        if(categorien.isEmpty()){
            p.openInventory(inv);
            return;
        }

        ItemStack left;
        ItemMeta leftMeta;
        if(PageUtil.isPageValid(allcategorien,page-1,spaces)){
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
        if(PageUtil.isPageValid(allcategorien,page+1,spaces)){
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

        for(ItemStack is : PageUtil.getPageItems(allcategorien,page,spaces)){
            inv.setItem(inv.firstEmpty(),is);
        }

        p.openInventory(inv);
    }


    private Inventory emptyPage(String categorie){
        Inventory inv = Bukkit.createInventory(null,54,"§6Verhuurdeplots §7§o- §r§7" + categorie);
        inv.setItem(45,nit(Material.BARRIER,"§c§LLeft", 0,null));
        inv.setItem(49,nit(Material.RABBIT_FOOT, "§a§lAdd Huur",173,categorie));
        inv.setItem(53,nit(Material.BARRIER,"§c§LLeft", 0,null));
        return inv;
    }

    private ItemStack nit(Material mat, String name, int modeldata, String localizedname){
        ItemStack item = new ItemStack(mat);
        ItemMeta itemm = item.getItemMeta();
        itemm.setDisplayName(name);
        if(!(modeldata == 0)){
            itemm.setCustomModelData(modeldata);
        }
        if(!(localizedname == null)) {
            itemm.setLocalizedName(localizedname);
        }
        item.setItemMeta(itemm);
        return item;
    }

}
