package me.max.stgameshuur.menu;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.max.stgameshuur.configs.SpelerverhuurplotsConfig;
import me.max.stgameshuur.menu.utils.PageUtil;
import me.max.stgameshuur.objects.VerhuurdePlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class VerhuurdePlotsMenu {

    public VerhuurdePlotsMenu(List<VerhuurdePlot> verhuurdePlotArrayList,Player p, String categorie){

        int page = 1;
        int spaces = 45;

        ArrayList<ItemStack> alleplots = new ArrayList<>();

        Inventory inv = Bukkit.createInventory(null,54,"§6Plots onder §7§o- §r§7" + categorie);

        if(categorie.isEmpty()){
            p.openInventory(inv);
            return;
        }

        for(VerhuurdePlot verhuurdePlot : verhuurdePlotArrayList){
            if(verhuurdePlot.getCategory().equals(categorie)){
                alleplots.add(huuritem(verhuurdePlot));
            }
        }

        if(p.hasPermission("stgames.categorie."+categorie+".add")){
            inv.setItem(49,nit(Material.RABBIT_FOOT, "§a§lAdd",161,categorie));
        }

        ItemStack left;
        ItemMeta leftMeta;
        if(PageUtil.isPageValid(alleplots,page-1,spaces)){
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
        if(PageUtil.isPageValid(alleplots,page+1,spaces)){
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

        for(ItemStack is : PageUtil.getPageItems(alleplots,page,spaces)){
            inv.setItem(inv.firstEmpty(),is);
        }

        p.openInventory(inv);
    }

    private ItemStack huuritem(VerhuurdePlot verhuurdePlot){
        List<String> lore = new ArrayList<>();
        ItemStack item = new ItemStack(Material.BARRIER);
        Integer daysPaymentMissed = verhuurdePlot.getDaysPaymentMissed();
        if(daysPaymentMissed == 0){
            item = new ItemStack(Material.LIME_SHULKER_BOX);
        } else if(daysPaymentMissed == 1){
            item = new ItemStack(Material.ORANGE_SHULKER_BOX);
        } else if(daysPaymentMissed >= 2){
            item = new ItemStack(Material.RED_SHULKER_BOX);
        }
        lore.add("§bHuurder: §6" + Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID()).getName());
        lore.add("§bPlot: §6" + verhuurdePlot.getPlotID());
        lore.add("§bPrijs: §6€" + verhuurdePlot.getPrice()+ ",-");
        lore.add("§bDagen: §6" + verhuurdePlot.getDaysbetweenpayment());
        lore.add("§bHeeft Betaald: §6" + verhuurdePlot.hasPayed());
        ItemMeta itemm = item.getItemMeta();
        itemm.setLore(lore);
        itemm.setDisplayName("§b"+verhuurdePlot.getPlotID());
        item.setItemMeta(itemm);
        return item;
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

    public List<String> plotsinworld(Player p){
        List<String> list = new ArrayList<>();
        World world = p.getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        if (regionManager.getRegions().isEmpty()) {
            return null;
        }
        for (ProtectedRegion region : regionManager.getRegions().values()) {
            list.add(region.getId());
        }
        return list;
    }

}
