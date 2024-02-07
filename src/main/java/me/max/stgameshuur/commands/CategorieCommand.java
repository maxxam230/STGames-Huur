package me.max.stgameshuur.commands;

import me.max.stgameshuur.Main;
import me.max.stgameshuur.configs.CategorienConfig;
import me.max.stgameshuur.objects.HuurCategory;
import nl.minetopiasdb.api.banking.Bankaccount;
import nl.minetopiasdb.api.enums.BankAccountType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CategorieCommand implements TabExecutor {

    Main plugin;

    String prefix = Main.prefix;
    String errorprefix = Main.errorprefix;


    public CategorieCommand(Main instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (label.equalsIgnoreCase("categorie")) {
                if (p.hasPermission("stgames.categorie")) {
                    if (args.length < 1) {
                        helpmenu(p);
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("add")) {
                    if (!p.hasPermission("stgames.categorie.add")) {
                        noPerms(p);
                        return true;
                    }
                    if (args.length < 3) {
                        helpmenu(p);
                        return true;
                    }
                    if (isCategory(args[1])) {
                        p.sendMessage(errorprefix + "§cDeze categorie bestaat al");
                        return true;
                    }
                    int rekeningid = 0;
                    try {
                        rekeningid = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                        p.sendMessage(errorprefix + "§cDat is geen nummer!");
                        return true;
                    }
                    if(!isBankId(rekeningid)){
                        p.sendMessage(errorprefix + "§cDit is niet een geldig rekening-ID");
                        return true;
                    }
                    plugin.categorien.add(new HuurCategory(args[1], rekeningid));
                    plugin.saveCategorien();
                    p.sendMessage(prefix + "§aCategorie: §o" + args[1].toLowerCase() + " §aToegevoegd");
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (!p.hasPermission("stgames.categorie.remove")) {
                        noPerms(p);
                        return true;
                    }
                    if (args.length < 2) {
                        helpmenu(p);
                        return true;
                    }
                    if (!isCategory(args[1])) {
                        p.sendMessage(errorprefix + "§cDeze categorie bestaat niet");
                        return true;
                    }
                    removeCategory(args[1]);
                    plugin.saveCategorien();
                    p.sendMessage(prefix + "§aCategorie: §o" + args[1].toLowerCase() + " §aVerwijderd!");
                } else if (args[0].equalsIgnoreCase("list")) {
                    if (!p.hasPermission("stgames.categorie.remove")) {
                        noPerms(p);
                        return true;
                    }
                    if (plugin.categorien.isEmpty()) {
                        p.sendMessage(errorprefix + "§cEr zijn geen categorien geladen!");
                    } else {
                        p.sendMessage(prefix + "§9- Categorien:");
                        for (HuurCategory huurCategory : plugin.categorien) {
                            p.sendMessage("§b" + huurCategory.getCategory() + ": " + huurCategory.getBankid());
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isCategory(String category) {
        if (plugin.categorien.isEmpty()) {
            return false;
        }
        for (HuurCategory huurCategory : plugin.categorien) {
            return huurCategory.getCategory().equals(category);
        }
        return false;
    }

    private void removeCategory(String category) {
        if (plugin.categorien.isEmpty()) {
            return;
        }
        Iterator<HuurCategory> iterator = plugin.categorien.iterator();
        while (iterator.hasNext()) {
            HuurCategory obj = iterator.next();
            if (obj.getCategory().equals(category)) {
                iterator.remove();
                break;
            }
        }
    }

    private void noPerms(Player p) {
        p.sendMessage(errorprefix + "§cJe hebt hier geen permissie voor.");
    }

    private void helpmenu(Player p) {
        p.sendMessage(prefix);
        p.sendMessage("§b/categorie add [categorie] [rekening-ID]");
        p.sendMessage("§b/categorie remove [categorie]");
        p.sendMessage("§b/categorie list");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            if (p.hasPermission("stgames.categorie.add")) {
                list.add("add");
            }
            if (p.hasPermission("stgames.categorie.remove")) {
                list.add("remove");
            }
            if (p.hasPermission("stgames.categorie.list")) {
                list.add("list");
            }
            return list;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                FileConfiguration categorieconfig = CategorienConfig.getCategorienfileconfig();
                if (!categorieconfig.contains("categorien")) {
                    return null;
                }
                return new ArrayList<>(categorieconfig.getConfigurationSection("categorien").getKeys(false));
            }
            if (args[0].equalsIgnoreCase("add")) {
                return Collections.singletonList("categorie_naam");
            }
        }
        if (args.length > 2) {
            if (args[0].equalsIgnoreCase("add")) {
                List<String> accountNumbers = new ArrayList<>();
                List<Bankaccount> accounts = nl.minetopiasdb.api.banking.BankUtils.getInstance().getAccounts(BankAccountType.BUSINESS);
                accounts.addAll(nl.minetopiasdb.api.banking.BankUtils.getInstance().getAccounts(BankAccountType.GOVERNMENT));
                for (Bankaccount account : accounts) {
                    accountNumbers.add(String.valueOf(account.getId()));
                }
                return accountNumbers;
            }
        }
        return null;
    }

    private boolean isBankId(Integer id){
        List<Bankaccount> accounts = nl.minetopiasdb.api.banking.BankUtils.getInstance().getAccounts(BankAccountType.BUSINESS);
        accounts.addAll(nl.minetopiasdb.api.banking.BankUtils.getInstance().getAccounts(BankAccountType.GOVERNMENT));
        for (Bankaccount account : accounts) {
            if(account.getId() == id){
                return true;
            }
        }
        return false;
    }


}
