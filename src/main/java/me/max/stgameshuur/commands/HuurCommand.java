package me.max.stgameshuur.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.max.stgameshuur.Main;
import me.max.stgameshuur.configs.CategorienConfig;
import me.max.stgameshuur.menu.CategorienMenu;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import nl.minetopiasdb.api.banking.Bankaccount;
import nl.minetopiasdb.api.enums.BankAccountType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class HuurCommand implements TabExecutor {

    Main plugin;

    String prefix = Main.prefix;
    String errorprefix = Main.errorprefix;


    public HuurCommand(Main instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (label.equalsIgnoreCase("huur")) {
                if (p.hasPermission("stgames.huur")) {
                    if (args.length < 1) {
                        if (!p.isOp()) {
                            plugin.showTimeRemaining(p);
                            return true;
                        }
                        helpmenu(p);
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("add")) {
                        if (p.hasPermission("stgames.huur.add")) {
                            if (args.length != 6) {
                                p.sendMessage("§b/huur add [speler] [plot] [bedrag] [dagen] [rekening-ID]");
                                return true;
                            }
                            if (Bukkit.getPlayer(args[1]) != null && Bukkit.getPlayer(args[1]).isOnline()) {
                                UUID playerUUID = Bukkit.getPlayer(args[1]).getUniqueId();
                                if (plugin.getConfig().contains(".verhuurdePlots." + args[2])) {
                                    p.sendMessage(errorprefix + "§cDit plot is al verhuurd!");
                                    return true;
                                }
                                if (Integer.parseInt(args[4]) == 0) {
                                    p.sendMessage(errorprefix + "§cDe dagen tussen betalingen kan niet 0 zijn!");
                                    return true;
                                }
                                plugin.addVerhuurdePlot(p, playerUUID, args[2], Double.parseDouble(args[3]), Integer.parseInt(args[5]), Integer.parseInt(args[4]));
                            } else {
                                p.sendMessage(errorprefix + "§cSpeler is niet online");
                            }
                        } else {
                            p.sendMessage(errorprefix + "§cJe hebt hier geen permissie voor!");
                        }
                    } else if (args[0].equalsIgnoreCase("list")) {
                        if (p.hasPermission("stgames.huur.add")) {
                            plugin.stuurLoadedVerhuurdePlots(p);
                        } else {
                            p.sendMessage(errorprefix + "§cJe hebt hier geen permissie voor!");
                        }
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        if (p.hasPermission("stgames.huur.add")) {
                            if (args.length != 2) {
                                p.sendMessage("§b/huur remove [plot-ID]");
                                return true;
                            }
                            plugin.removeVerhuurdePlot(p, args[1]);
                        } else {
                            p.sendMessage(errorprefix + "§cJe hebt hier geen permissie voor!");
                        }
                    } else if (args[0].equalsIgnoreCase("timeremaining")) {
                        if (p.hasPermission("stgames.huur.add")) {
                            plugin.showTimeRemaining(p);
                        } else {
                            p.sendMessage(errorprefix + "§cJe hebt hier geen permissie voor!");
                        }
                    } else if (args[0].equalsIgnoreCase("info")) {
                        if (p.hasPermission("stgames.huur.info")) {
                            if (args.length < 2) {
                                p.sendMessage(errorprefix + "§c/huur info [plot-ID]");
                                return true;
                            }
                            plugin.sendPlotInfo(args[1], p);
                        } else {
                            p.sendMessage(errorprefix + "§cJe hebt hier geen permissie voor!");
                        }
                    } else if (args[0].equalsIgnoreCase("debug")) {
                        if (p.isOp()) {
                            if (plugin.getConfig().getBoolean("debug")) {
                                plugin.getConfig().set("debug", false);
                                p.sendMessage(prefix + "§aDebug mode uitgezet");
                            } else {
                                plugin.getConfig().set("debug", true);
                                p.sendMessage(prefix + "§aDebug mode aangezet zet dit wel weer uit!");
                            }
                            plugin.saveConfig();
                        }
                    } else if (args[0].equalsIgnoreCase("forcepay")) {
                        if (p.isOp()) {
                            plugin.huurbetaalforce();
                        }
                    } else if (args[0].equalsIgnoreCase("vraaghuur")){
                        if(p.hasPermission("stgames.huur.vraaghuur")){
                            if(args.length < 2){
                                p.sendMessage(errorprefix+"§c/huur vraaghuur [speler]");
                                return true;
                            }
                            if(args[1].equalsIgnoreCase(p.getName())){
                                p.sendMessage(errorprefix + "§cJe kan niet jezelf huur laten betalen");
                                return true;
                            }
                            if (Bukkit.getPlayer(args[1]) != null && Bukkit.getPlayer(args[1]).isOnline()) {
                                plugin.betaalachterstand(p,args[1]);
                            } else {
                                p.sendMessage(errorprefix + "§cDeze speler bestaat niet of is niet online");
                            }
                        } else {
                            p.sendMessage(errorprefix+"§cJe hebt hiet geen permissie voor!");
                        }
                    } else if(args[0].equalsIgnoreCase("bevestig")){
                        if(moetconfirmen(p)){
                            plugin.confirmedbetaalachterstand(p);
                        } else {
                            p.sendMessage(errorprefix+"§cJe hebt niks om te bevestigen");
                        }
                    } else if (args[0].equalsIgnoreCase("npc")) {
                        if(args.length < 2){
                            p.sendMessage(errorprefix + "§c/huur npc add/remove [NPC-ID]");
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("add")){
                            if(args.length < 3){
                                p.sendMessage(errorprefix + "§c/huur npc add [NPC-ID]");
                                return true;
                            }
                            try {
                                Integer npcid = Integer.parseInt(args[2]);
                                if(plugin.npcIDs.contains(npcid)){p.sendMessage(errorprefix+"§cDeze npc is al toegevoegd"); return true;}
                                plugin.npcIDs.add(npcid);
                                p.sendMessage(prefix + "§anpc met id: " + npcid + " toegevoegd");
                            }catch (Exception e){
                                p.sendMessage(errorprefix + "§cSorry dat is geen nummer!");
                            }
                        } else if(args[1].equalsIgnoreCase("remove")){
                            if(args.length < 3){
                                p.sendMessage(errorprefix + "§c/huur npc remove [NPC-ID]");
                                return true;
                            }
                            try {
                                Integer npcid = Integer.parseInt(args[2]);
                                if(plugin.npcIDs.contains(npcid)){
                                    plugin.npcIDs.remove(npcid);
                                    p.sendMessage(prefix + "§anpc met id: " + npcid + " verwijderd");
                                } else {
                                    p.sendMessage(prefix + "§cnpc met id: " + npcid + " is niet een huur npc");
                                }
                            }catch (Exception e){
                                p.sendMessage(errorprefix + "§cSorry dat is geen nummer!");
                            }
                        }
                    }
                } else {
                    p.sendMessage(errorprefix + "§cJe hebt hier geen permissie voor");
                }
            }
        }
        return false;
    }

    public boolean moetconfirmen(Player p){
        return plugin.moetconfirmen(p);
    }


    public void helpmenu(Player p) {
        p.sendMessage(prefix);
        p.sendMessage("§b/huur npc add/remove [NPC-ID]");
        p.sendMessage("§b/huur add [speler] [plot] [bedrag] [dagen] [rekening-ID]");
        p.sendMessage("§b/huur remove [plot-ID]");
        p.sendMessage("§b/huur info [plot-ID]");
        p.sendMessage("§b/huur timeremaining");
        p.sendMessage("§b/huur list");
        p.sendMessage("§b/huur vraaghuur [speler]");
        p.sendMessage("§b/huur debug §8§o(gebruik dit alleen om te testen)");
        p.sendMessage("§b/huur forcepay §8§o(gebruik dit alleen om te testen)");
        p.sendMessage("§b/huur bevestig §8§o(kan alleen als je meer dan 2 dagen gemist hebt)");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            if(p.hasPermission("stgames.huur.npc")){
                list.add("npc");
            }
            if(p.hasPermission("stgames.huur.add")){
                list.add("add");
            }
            if(p.hasPermission("stgames.huur.info")){
                list.add("maakcategorie");
            }
            if(p.hasPermission("stgames.huur.remove")){
                list.add("remove");
            }
            if(p.hasPermission("stgames.huur.info")){
                list.add("info");
            }
            if(p.hasPermission("stgames.huur.timeremaining")){
                list.add("timeremaining");
            }
            if(p.hasPermission("stgames.huur.list")){
                list.add("list");
            }
            if(p.hasPermission("stgames.huur.vraaghuur")){
                list.add("vraaghuur");
            }
            return list;
        }
        if(args[0].equalsIgnoreCase("npc")) {
            if(args.length == 2) {
                List<String> list = new ArrayList<>();
                list.add("add");
                list.add("remove");
                return list;
            }
        }
        if(args[0].equalsIgnoreCase("add")) {
            if (args.length == 2) {
                List<String> list = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    list.add(player.getName());
                }
                return list;
            }
            if (args.length == 3) {
                return plotsinworld(p);
            }

            if (args.length == 4) {
                return Collections.singletonList("bedrag");
            }

            if (args.length == 5) {
                return Collections.singletonList("dagen");
            }

            if (args.length == 6) {
                List<Bankaccount> accounts = nl.minetopiasdb.api.banking.BankUtils.getInstance().getAccounts(BankAccountType.GOVERNMENT);
                List<String> accountnmbrs = new ArrayList<>();
                for (Bankaccount account : accounts) {
                    accountnmbrs.add(String.valueOf(account.getId()));
                }
                return accountnmbrs;
            }
        } else if(args[0].equalsIgnoreCase("remove")){
            return plugin.returnLoadedVerhuurdePlots();
        } else if(args[0].equalsIgnoreCase("info")){
            return plugin.returnLoadedVerhuurdePlots();
        } else if(args[0].equalsIgnoreCase("vraaghuur")){
            return onlinespeler();
        }

        return null;
    }

    public List<String> onlinespeler(){
        List<String> list = new ArrayList<>();
        for(Player p : Bukkit.getOnlinePlayers()){
            list.add(p.getName());
        }
        return list;
    }

    public List<String> plotsinworld(Player p){
        List<String> list = new ArrayList<>();
        World world = p.getWorld();
        Location loc = BukkitAdapter.adapt(p.getLocation());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        if (regionManager.getRegions().isEmpty()) {
            return null;
        }
        for (com.sk89q.worldguard.protection.regions.ProtectedRegion region : regionManager.getRegions().values()) {
            if(region.getOwners().contains(p.getUniqueId())){
                list.add(region.getId());
            }
        }
        return list;
    }

    public List<String> verhuurdeplots(Player p){
        List<String> list = new ArrayList<>();
        if(plugin.getConfig().getStringList(".verhuurdeplots").isEmpty()){
            return null;
        }
        list.addAll(plugin.getConfig().getConfigurationSection(".verhuurdePlots").getKeys(false));
        return list;
    }
}

