package me.max.stgameshuur.menu;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.max.stgameshuur.Main;
import me.max.stgameshuur.menu.utils.PageUtil;
import me.max.stgameshuur.objects.VerhuurdePlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerSelectMenu {

    private final String prefix = Main.prefix;
    private final String errorprefix = Main.errorprefix;

    public PlayerSelectMenu(Player p, int page){

        int spaces = 45;

        ArrayList<ItemStack> allOnlinePlayers = new ArrayList<>();

        for(Player allp : Bukkit.getOnlinePlayers()){
            if(!allp.getName().equals(p.getName())) {
                allOnlinePlayers.add(nit(allp.getName()));
            }
        }

        Inventory inv = Bukkit.createInventory(null,54,"§6Kies speler voor huurcontract");

        ItemStack left;
        ItemMeta leftMeta;
        if(PageUtil.isPageValid(allOnlinePlayers,page-1,spaces)){
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
        if(PageUtil.isPageValid(allOnlinePlayers,page+1,spaces)){
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

        for(ItemStack is : PageUtil.getPageItems(allOnlinePlayers,page,spaces)){
            inv.setItem(inv.firstEmpty(),is);
        }

        p.openInventory(inv);
    }

    private ItemStack nit(String name){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta itemm = (SkullMeta) item.getItemMeta();
        itemm.setOwningPlayer(Bukkit.getOfflinePlayer(name));
        itemm.setDisplayName("§3"+name);
        item.setItemMeta(itemm);
        return item;
    }

}
