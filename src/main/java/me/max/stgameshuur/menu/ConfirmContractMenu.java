package me.max.stgameshuur.menu;

import me.max.stgameshuur.objects.VerhuurdePlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class ConfirmContractMenu {


    public ConfirmContractMenu(Player p, VerhuurdePlot verhuurdePlot){
        Inventory inv = Bukkit.createInventory(null,45,"§6Bevestig Contract");

        inv.setItem(12,nit(Material.NAME_TAG,"§b"+verhuurdePlot.getPlotID(), Arrays.asList("§7Plotnaam voor het huurcontract")));
        inv.setItem(13,nit(Material.CLOCK,"§b"+verhuurdePlot.getDaysbetweenpayment(), Arrays.asList("§7Dagen tussen betalingen!")));
        inv.setItem(14,nit(Material.IRON_INGOT,"§b€"+verhuurdePlot.getPrice() + ".-", Arrays.asList("§7Prijs die je elke "+verhuurdePlot.getDaysbetweenpayment()+" dagen betaald!")));

        inv.setItem(29,nit(Material.RED_CONCRETE,"§cAnnuleer"));
        inv.setItem(33,nit(Material.LIME_CONCRETE,"§aBevestig"));



        p.openInventory(inv);
    }

    private ItemStack nit(Material material, String name){
        ItemStack item = new ItemStack(material);
        ItemMeta itemm = item.getItemMeta();
        itemm.setDisplayName(name);
        item.setItemMeta(itemm);
        return item;
    }

    private ItemStack nit(Material material, String name, List<String> lore){
        ItemStack item = new ItemStack(material);
        ItemMeta itemm = item.getItemMeta();
        itemm.setLore(lore);
        itemm.setDisplayName(name);
        item.setItemMeta(itemm);
        return item;
    }


}
