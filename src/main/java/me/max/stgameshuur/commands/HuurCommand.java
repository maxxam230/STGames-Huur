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
import me.max.stgameshuur.menu.ConfirmContractMenu;
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

                    if (args[0].equalsIgnoreCase("list")) {
                        if (p.hasPermission("stgames.huur.add")) {
                            plugin.stuurLoadedVerhuurdePlots(p);
                        } else {
                            p.sendMessage(errorprefix + "§cJe hebt hier geen permissie voor!");
                        }
                    } else if (args[0].equalsIgnoreCase("timeremaining")) {
                        if (p.hasPermission("stgames.huur.timeremaining")) {
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
                    } else if(args[0].equalsIgnoreCase("bevestig")){
                        if(plugin.laatBetalerList.containsKey(p.getUniqueId())){
                            plugin.confirmedbetaalachterstand((Player) Bukkit.getOfflinePlayer(p.getUniqueId()));
                            return true;
                        }
                        if(plugin.confirmhuurcontract.containsKey(p.getUniqueId())){
                            new ConfirmContractMenu(p,plugin.confirmhuurcontract.get(p.getUniqueId()));
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


    public void helpmenu(Player p) {
        p.sendMessage(prefix);
        p.sendMessage("§b/huur npc add/remove [NPC-ID]");
        p.sendMessage("§b/huur info [plot-ID]");
        p.sendMessage("§b/huur timeremaining");
        p.sendMessage("§b/huur list");
        p.sendMessage("§b/huur debug §8§o(gebruik dit alleen om te testen)");
        p.sendMessage("§b/huur forcepay §8§o(gebruik dit alleen om te testen)");
        p.sendMessage("§b/huur bevestig §8§o(voor laatbetalingen en contract tekenen)");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            if(p.hasPermission("stgames.huur.npc")){
                list.add("npc");
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


        if(args[0].equalsIgnoreCase("info")){
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
}

