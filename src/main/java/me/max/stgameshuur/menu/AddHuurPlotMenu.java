package me.max.stgameshuur.menu;

import me.max.stgameshuur.objects.AddVerhuurPlot;
import me.max.stgameshuur.objects.HuurCategory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddHuurPlotMenu {

    public AddHuurPlotMenu(Player p, AddVerhuurPlot addVerhuurPlot, List<HuurCategory> huurCategories){

        Inventory inv = Bukkit.createInventory(null,54,"§6§lAdd huurcontract");

        inv.setItem(11,nit(Material.IRON_DOOR, "§bPlot:",Arrays.asList("§7Klik om een plot te selecteren")));
        inv.setItem(12,nit(Material.PLAYER_HEAD, "§bHuurder:",Arrays.asList("§7Klik om een speler te selecteren")));
        inv.setItem(13,nit(Material.NAME_TAG, "§bCategorie:",Arrays.asList("§7Deze kan je hier niet aanpassen!")));
        inv.setItem(14,nit(Material.IRON_INGOT, "§bPrijs:",Arrays.asList("§7Klik om prijs te instellen")));
        inv.setItem(15,nit(Material.CLOCK, "§bDagen:",Arrays.asList("§7Klik om de dagen te instellen")));

        if(addVerhuurPlot.getPlot() == null){
            inv.setItem(20,nit(Material.BARRIER, "Nog instellen!"));
        } else {
            inv.setItem(20,nit(Material.LIME_SHULKER_BOX, addVerhuurPlot.getPlot()));
        }

        if(addVerhuurPlot.getHuurder() == null){
            inv.setItem(21,nit(Material.BARRIER, "Nog instellen!"));
        } else {
            inv.setItem(21,nit(Material.LIME_SHULKER_BOX, addVerhuurPlot.getHuurder()));
        }

        if(addVerhuurPlot.getCategorie() == null){
            inv.setItem(22,nit(Material.BARRIER, "Nog instellen!"));
        } else {
            inv.setItem(22,nit(Material.LIME_SHULKER_BOX, addVerhuurPlot.getCategorie()));
        }

        if(addVerhuurPlot.getPrijs() == null){
            inv.setItem(23,nit(Material.BARRIER, "Nog instellen!"));
        } else {
            inv.setItem(23,nit(Material.LIME_SHULKER_BOX, addVerhuurPlot.getPrijs()+""));
        }

        if(addVerhuurPlot.getDagen() == null){
            inv.setItem(24,nit(Material.BARRIER, "Nog instellen!"));
        } else {
            inv.setItem(24,nit(Material.LIME_SHULKER_BOX, addVerhuurPlot.getDagen()+""));
        }

        inv.setItem(38, nit(Material.RED_CONCRETE,"§c§lAnnuleer"));
        inv.setItem(40, total(inv, getBankIDfromCategory(addVerhuurPlot.getCategorie(), huurCategories) + ""));
        inv.setItem(42, nit(Material.GREEN_CONCRETE,"§c§lKlaar", Arrays.asList("§7§oCheck alle waardes goed!")));


        p.openInventory(inv);
    }

    private Integer getBankIDfromCategory(String category, List<HuurCategory> huurCategories){
        for(HuurCategory huurCategory : huurCategories){
            if(huurCategory.getCategory().equals(category)){
                return huurCategory.getBankid();
            }
        }
        return null;
    }

    private ItemStack total(Inventory inv, String localizename){
        List<String> lore = new ArrayList<>();
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta itemm = item.getItemMeta();
        itemm.setDisplayName("Resultaat:");
        if(inv.getItem(20).getType().equals(Material.BARRIER)){
            lore.add("§c§lNiet ingesteld!");
        } else {
            lore.add("§bPlot: §6" + inv.getItem(20).getItemMeta().getDisplayName());
        }
        if(inv.getItem(21).getType().equals(Material.BARRIER)){
            lore.add("§c§lNiet ingesteld!");
        } else {
            lore.add("§bHuurder: §6" + inv.getItem(21).getItemMeta().getDisplayName());
        }
        if(inv.getItem(22).getType().equals(Material.BARRIER)){
            lore.add("§c§lNiet ingesteld!");
        } else {
            lore.add("§bCategorie: §6" + inv.getItem(22).getItemMeta().getDisplayName());
        }
        if(inv.getItem(23).getType().equals(Material.BARRIER)){
            lore.add("§c§lNiet ingesteld!");
        } else {
            lore.add("§bPrijs: §6€" + inv.getItem(23).getItemMeta().getDisplayName() + ".00,-");
        }
        if(inv.getItem(24).getType().equals(Material.BARRIER)){
            lore.add("§c§lNiet ingesteld!");
        } else {
            lore.add("§bDagen: §6" + inv.getItem(24).getItemMeta().getDisplayName());
        }
        lore.add("§bRekening-ID: §6" + localizename);
        itemm.setLocalizedName(localizename);
        itemm.setLore(lore);
        item.setItemMeta(itemm);
        return item;
    }

    private ItemStack nit(Material mat, String name){
        ItemStack item = new ItemStack(mat);
        ItemMeta itemm = item.getItemMeta();
        itemm.setDisplayName(name);
        item.setItemMeta(itemm);
        return item;
    }

    private ItemStack nit(Material mat, String name, List<String> lore){
        ItemStack item = new ItemStack(mat);
        ItemMeta itemm = item.getItemMeta();
        itemm.setLore(lore);
        itemm.setDisplayName(name);
        item.setItemMeta(itemm);
        return item;
    }

    private ItemStack nit(Material mat, String name, List<String> lore, String localizedname){
        ItemStack item = new ItemStack(mat);
        ItemMeta itemm = item.getItemMeta();
        itemm.setLore(lore);
        itemm.setLocalizedName(localizedname);
        itemm.setDisplayName(name);
        item.setItemMeta(itemm);
        return item;
    }

}
