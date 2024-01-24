package me.max.stgameshuur.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CategorieHurenMenu {


    public CategorieHurenMenu(Player p, String categorie){
        Inventory inv = Bukkit.createInventory(null,54,categorie);

        inv.setItem(2,new ItemStack(Material.DRAGON_EGG));

        p.openInventory(inv);
    }

}
