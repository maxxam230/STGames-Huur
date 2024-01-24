package me.max.stgameshuur.menu;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.max.stgameshuur.menu.utils.PageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class CategorienMenu {


    public CategorienMenu(ArrayList<String> categorien, Player p, int page, int spaces){
        if(categorien.isEmpty()){
            return;
        }

        ArrayList<ItemStack> allcategorien = new ArrayList<>();

        for(String cat : categorien){
            if(p.hasPermission("stgames.huur.categorie." + cat)) {
                allcategorien.add(nit(cat));
            }
        }

        Inventory inv = Bukkit.createInventory(null,54,"§6Huur Categorien §7§o- " + page);

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

    private ItemStack nit(String name){
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta itemm = item.getItemMeta();
        itemm.setDisplayName("§b§l"+name);
        item.setItemMeta(itemm);
        return item;
    }

}
