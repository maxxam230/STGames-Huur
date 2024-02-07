package me.max.stgameshuur.menu;

import me.max.stgameshuur.objects.VerhuurdePlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HuurInformatieMenu {

    VerhuurdePlot verhuurdePlot;

    public HuurInformatieMenu(List<VerhuurdePlot> verhuurdePlotList, Player p, String plotnaam){

        for(VerhuurdePlot verhuurdePlot1 : verhuurdePlotList){
            if(verhuurdePlot1.getPlotID().equals(plotnaam)){
                this.verhuurdePlot = verhuurdePlot1;
            }
        }

        Inventory inv;

        if(p.hasPermission("stgames.categorie."+verhuurdePlot.getCategory()+".add")){
            inv = Bukkit.createInventory(null,45,"§6Huur Info: §b" + verhuurdePlot.getPlotID());
            inv.setItem(30, nit(Material.TNT, "§cVerwijder", Arrays.asList("§7Dit verwijdert het huurcontract"),verhuurdePlot.getPlotID()));

            if(verhuurdePlot.getDaysPaymentMissed() >= 1){
                inv.setItem(32,nit(Material.NAME_TAG,"§6Vraaghuur", Arrays.asList("§7Dit vraagt de speler om nu te betalen")));
            } else {
                inv.setItem(32,nit(Material.BARRIER, "§cNiet mogelijk"));
            }

            if(p.hasPermission("stgames.huur.wijzigprijs")) {
                inv.setItem(31, nit(Material.IRON_INGOT, "§bPrijs", Arrays.asList("§7Klik om de prijs te wijzigen")));
            }
        } else {
            inv = Bukkit.createInventory(null,27,"§6Huur Info: §b" + verhuurdePlot.getPlotID());
        }
        inv.setItem(10,nithead(Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID()).getName()));
        inv.setItem(12,nit(Material.NAME_TAG,"§6"+verhuurdePlot.getPlotID(), Arrays.asList("§7Plotnaam voor het huurcontract")));
        inv.setItem(13,nit(Material.CLOCK,"§6"+verhuurdePlot.getDaysbetweenpayment(), timeLore(verhuurdePlot)));
        String dagofdagen;
        if(verhuurdePlot.getDaysbetweenpayment() <= 1){
            dagofdagen = "dag";
        } else {
            dagofdagen = "dagen";
        }
        inv.setItem(14,nit(Material.IRON_INGOT,"§6€"+verhuurdePlot.getPrice(), Arrays.asList("§7Prijs die je elke "+verhuurdePlot.getDaysbetweenpayment()+" "+dagofdagen+" betaald!")));
        inv.setItem(16,nit(Material.BOOK,"§6"+verhuurdePlot.getDaysPaymentMissed(), Arrays.asList("§7Gemiste betalingen")));
        p.openInventory(inv);
    }

    private ItemStack nithead(String name){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta itemm = (SkullMeta) item.getItemMeta();
        itemm.setOwningPlayer(Bukkit.getOfflinePlayer(name));
        itemm.setDisplayName("§6"+name);
        itemm.setLore(Arrays.asList("§7Huurder van dit contract"));
        item.setItemMeta(itemm);
        return item;
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

    private ItemStack nit(Material material, String name, List<String> lore, String localizedname){
        ItemStack item = new ItemStack(material);
        ItemMeta itemm = item.getItemMeta();
        itemm.setLore(lore);
        itemm.setLocalizedName(localizedname);
        itemm.setDisplayName(name);
        item.setItemMeta(itemm);
        return item;
    }

    private List<String> timeLore(VerhuurdePlot verhuurdePlot){
        long currentTimeMillis = System.currentTimeMillis();
        List<String> lore = new ArrayList<>();
        long nextPaymentTimeMillis = verhuurdePlot.getLastPaymentDate() + TimeUnit.DAYS.toMillis(verhuurdePlot.getDaysbetweenpayment());
        long timeDifferenceMillis = (nextPaymentTimeMillis - currentTimeMillis);
        if (timeDifferenceMillis > 0) {
            long daysRemaining = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis);
            long hoursRemaining = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis) % 24;
            long minutesRemaining = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis) % 60;
            String dagofdagen;
            if(daysRemaining <= 1){
                dagofdagen = "dag";
            } else {
                dagofdagen = "dagen";
            }

            lore.add("§3Volgende betaling over:");
            lore.add("§b"+daysRemaining+" §3"+dagofdagen);
            lore.add("§b"+hoursRemaining+" §3Uur");
            lore.add("§b"+minutesRemaining+" §3Minuten");
        } else {
            lore.add("§7Volgende de betaling binnen 5 minuten");
        }
        return lore;
    }

}
