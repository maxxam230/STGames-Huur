package me.max.stgameshuur.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.event.platform.PlatformUnreadyEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.max.stgameshuur.Main;
import me.max.stgameshuur.configs.SpelerverhuurplotsConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HuurplotCommand implements TabExecutor {

    Main plugin;

    String prefix = Main.prefix;
    String errorprefix = Main.errorprefix;


    public HuurplotCommand(Main instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(label.equalsIgnoreCase("huurplot")){
                if(args.length < 2){
                    sendhelp(p);
                    return true;
                }
            }
            if(args[0].equalsIgnoreCase("add")){
                FileConfiguration spelerverhuurplots = SpelerverhuurplotsConfig.getVerhuurplotsfileconfig();
                if(!p.hasPermission("stgames.huurplot.add")){noPerms(p); return true;};
                if(args.length<2){sendhelp(p); return true;}
                if(!(getPlotAtLocation(p) == null)){
                    String plotnaam = getPlotAtLocation(p);
                    List<String> huurplotslist = new ArrayList<>();
                    if(spelerverhuurplots.contains(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString())) {
                        huurplotslist = spelerverhuurplots.getStringList(String.valueOf(p.getUniqueId()));
                    }
                    if(!huurplotslist.contains(getPlotAtLocation(p))){
                        p.sendMessage(prefix + "§aPlot: §7" + plotnaam + " §atoegevoegd aan speler: " + args[1].toLowerCase());
                        huurplotslist.add(plotnaam);
                        spelerverhuurplots.set(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString(), huurplotslist);
                        SpelerverhuurplotsConfig.save();
                    } else {
                        p.sendMessage(errorprefix + "§cDeze speler kan dit plot al verhuren!");
                    }
                } else {
                    p.sendMessage(errorprefix + "§cJe staat niet op een plot");
                }
            } else if(args[0].equalsIgnoreCase("remove")){
                FileConfiguration spelerverhuurplots = SpelerverhuurplotsConfig.getVerhuurplotsfileconfig();
                if(!p.hasPermission("stgames.huurplot.remove")){noPerms(p); return true;};
                if(args.length<3){sendhelp(p); return true;}
                String plotnaam = args[2];
                if(spelerverhuurplots.contains(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString())) {
                    List<String> huurplotslist = spelerverhuurplots.getStringList(String.valueOf(p.getUniqueId()));
                    if (huurplotslist.contains(plotnaam)) {
                        p.sendMessage(prefix + "§aPlot: §7" + plotnaam + " §averwijderd van speler: " + args[1].toLowerCase());
                        huurplotslist.remove(plotnaam);
                        spelerverhuurplots.set(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString(), huurplotslist);
                        SpelerverhuurplotsConfig.save();
                    } else {
                        p.sendMessage(errorprefix + "§cDeze speler mag dit plot al niet verhuren");
                    }
                }
            } else if (args[0].equalsIgnoreCase("list")){
                FileConfiguration spelerverhuurplots = SpelerverhuurplotsConfig.getVerhuurplotsfileconfig();
                if(!p.hasPermission("stgames.huurplot.list")){noPerms(p); return true;};
                if(args.length<2){sendhelp(p); return true;}
                if(spelerverhuurplots.contains(String.valueOf(Bukkit.getOfflinePlayer(args[1]).getUniqueId()))){
                    List<String> huurplotslist = spelerverhuurplots.getStringList(String.valueOf(Bukkit.getOfflinePlayer(args[1]).getUniqueId()));
                    if(!huurplotslist.isEmpty()){
                        p.sendMessage(prefix + "§a" + args[1] + " mag deze plots verhuren!");
                        for(String s : huurplotslist){
                            p.sendMessage("§b"+s);
                        }
                    } else {
                        p.sendMessage(errorprefix + "§cDeze spelers heeft geen plots die hij kan verhuren!");
                    }
                } else {
                    p.sendMessage(errorprefix + "§cDeze spelers heeft geen plots die hij kan verhuren!");
                }
            }
        }

    return false;
    }

    private void noPerms(Player p) {
        p.sendMessage(errorprefix + "§cJe hebt hier geen permissie voor!");
    }


    private String getPlotAtLocation(Player p){
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(p.getLocation());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(loc);
        for (ProtectedRegion region : set) {
            return region.getId();
        }
        return null;
    }

    private void sendhelp(Player p){
        p.sendMessage(prefix);
        p.sendMessage("§b/huurplot add [speler]");
        p.sendMessage("§b/huurplot remove [speler] [plot]");
        p.sendMessage("§b/huurplot list [speler]");
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            if (p.hasPermission("stgames.huurplot.add")) {
                list.add("add");
            }
            if (p.hasPermission("stgames.huurplot.remove")) {
                list.add("remove");
            }
            if (p.hasPermission("stgames.huurplot.list")) {
                list.add("list");
            }
            return list;
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            List<String> playerNames = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                playerNames.add(onlinePlayer.getName());
            }
            return playerNames;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            FileConfiguration spelerverhuurplots = SpelerverhuurplotsConfig.getVerhuurplotsfileconfig();
            if(spelerverhuurplots.contains(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString())){
                List<String> huurplotslist = spelerverhuurplots.getStringList(String.valueOf(Bukkit.getOfflinePlayer(args[1]).getUniqueId()));
                if(huurplotslist.isEmpty()){
                    return null;
                }
                return huurplotslist;
            }
        }
        return null;
    }

}
