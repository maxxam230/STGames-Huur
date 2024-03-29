package me.max.stgameshuur;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.max.stgameshuur.commands.CategorieCommand;
import me.max.stgameshuur.commands.HuurCommand;
import me.max.stgameshuur.commands.HuurplotCommand;
import me.max.stgameshuur.configs.CategorienConfig;
import me.max.stgameshuur.configs.SpelerverhuurplotsConfig;
import me.max.stgameshuur.configs.VerhuurdePlotsConfig;
import me.max.stgameshuur.events.InvClickEvent;
import me.max.stgameshuur.events.NPCRightClick;
import me.max.stgameshuur.events.SendChat;
import me.max.stgameshuur.objects.AddVerhuurPlot;
import me.max.stgameshuur.objects.HuurCategory;
import me.max.stgameshuur.objects.VerhuurdePlot;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import nl.minetopiasdb.api.banking.Bankaccount;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class Main extends JavaPlugin {

    public static final String debugprefix = "§7[§3§lDeb§bug§7] §8§o";
    public static final String prefix = "§7[§3§lSTG§bames§7] §r";
    public static final String errorprefix = "§7[§c§lSTG§cames§7] §r";

    public List<VerhuurdePlot> verhuurdePlotList = new ArrayList<>();
    public HashMap<UUID,UUID> laatBetalerList = new HashMap<>();

    public HashMap<UUID, VerhuurdePlot> confirmhuurcontract = new HashMap<>();
    public HashMap<UUID, AddVerhuurPlot> addingplot = new HashMap<>();
    public HashMap<UUID, VerhuurdePlot> setpriceforplot = new HashMap<>();
    public HashMap<UUID, Integer> addingtimeordays = new HashMap<>();

    public ArrayList<HuurCategory> categorien = new ArrayList<>();
    public ArrayList<Integer> npcIDs = new ArrayList<>();

    public Economy economy;


    @Override
    public void onEnable() {
        laadVerhuurdePlots();

        getServer().getScheduler().runTaskTimer(this, this::huurbetaal, 0, (20*60)*5);

        //Events
        Bukkit.getPluginManager().registerEvents(new InvClickEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new NPCRightClick(this), this);
        Bukkit.getPluginManager().registerEvents(new SendChat(this), this);

        //Commands
        getCommand("huur").setExecutor(new HuurCommand(this));
        getCommand("huurplot").setExecutor(new HuurplotCommand(this));
        getCommand("categorie").setExecutor(new CategorieCommand(this));


        //Config
        loadNpcIdsToList();
        SpelerverhuurplotsConfig.createCategorienConfig();
        CategorienConfig.createCategorienConfig();
        VerhuurdePlotsConfig.createCategorienConfig();
        getConfig().set("debug", false);
        saveConfig();

        //Categorien
        loadCategorien();

        //Economy Api
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> service = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (service != null)
                economy = service.getProvider();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveVerhuurdePlots();
        saveCategorien();
        saveNpcIdsToConfig();
    }

    public boolean debug() {
        return getConfig().getBoolean("debug");
    }

    private void loadNpcIdsToList(){
        if(getConfig().contains("npcIDS")){
            this.npcIDs = (ArrayList<Integer>) getConfig().getIntegerList("npcIDS");
        }
    }

    private void saveNpcIdsToConfig(){
        if(!npcIDs.isEmpty()) {
            getConfig().set("npcIDS", this.npcIDs);
            saveConfig();
        }
    }

    private void saveVerhuurdePlots() {
        Bukkit.getConsoleSender().sendMessage(prefix + "Saving verhuurde plots...");
        for (VerhuurdePlot verhuurdePlot : verhuurdePlotList) {
            String path = "verhuurdePlots." + verhuurdePlot.getPlotID(); // Use a unique identifier for each plot
            getConfig().set(path + ".playerUUID", verhuurdePlot.getPlayerUUID().toString());
            getConfig().set(path + ".lastPaymentDate", verhuurdePlot.getLastPaymentDate());
            getConfig().set(path + ".prijs", verhuurdePlot.getPrice());
            getConfig().set(path + ".rekeningID", verhuurdePlot.getBanknumber());
            getConfig().set(path + ".dagenTussenBetaling", verhuurdePlot.getDaysbetweenpayment());
            getConfig().set(path + ".heeftBetaald", verhuurdePlot.hasPayed());
            getConfig().set(path + ".gemisteBetalingen", verhuurdePlot.getDaysPaymentMissed());
            getConfig().set(path + ".categorie", verhuurdePlot.getCategory());
        }
        saveConfig();
        Bukkit.getConsoleSender().sendMessage(prefix + "§aVerhuurde plots opgeslagen");
    }


    private void laadVerhuurdePlots() {
        int i = 0;
        if (getConfig().contains("verhuurdePlots")) {
            ConfigurationSection plotsConfig = getConfig().getConfigurationSection("verhuurdePlots");
            for (String plotID : plotsConfig.getKeys(false)) {
                i = (i + 1);
                String path = "verhuurdePlots." + plotID;
                UUID playerUUID = UUID.fromString(getConfig().getString(path + ".playerUUID"));
                long lastPaymentDate = getConfig().getLong(path + ".lastPaymentDate");
                double price = getConfig().getDouble(path + ".prijs");
                int banknumber = getConfig().getInt(path + ".rekeningID");
                int daysBetweenPayment = getConfig().getInt(path + ".dagenTussenBetaling");
                boolean haspayed = getConfig().getBoolean(path + ".heeftBetaald");
                int missedpayments = getConfig().getInt(path + ".gemisteBetalingen");
                String categorie = getConfig().getString(path + ".categorie");
                // Create RentedPlot objects and add them to your collection
                VerhuurdePlot verhuurdeplot = new VerhuurdePlot(playerUUID, plotID, lastPaymentDate, price, banknumber, daysBetweenPayment, haspayed, missedpayments,categorie);
                verhuurdePlotList.add(verhuurdeplot);
            }
            Bukkit.getConsoleSender().sendMessage(prefix + i + " §aVerhuurde plots geladen");
        }
    }

    private void huurbetaal() {
        if (debug()) {
            Bukkit.getServer().broadcastMessage(debugprefix + "Huur betaling zijn gecontroleerd");
        }
        Bukkit.getConsoleSender().sendMessage(prefix + "§aHuur betaal check is uitgevoerd");
        long currentTimeMillis = System.currentTimeMillis();
        for (VerhuurdePlot verhuurdePlot : verhuurdePlotList) {
            long nextPaymentDate = verhuurdePlot.getLastPaymentDate() + TimeUnit.DAYS.toMillis(verhuurdePlot.getDaysbetweenpayment());
            if (currentTimeMillis >= nextPaymentDate) {
                if (economy.getBalance(Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID())) < verhuurdePlot.getPrice()) {
                    if (playerOnline(verhuurdePlot.getPlayerUUID())) {
                        Bukkit.getPlayer(verhuurdePlot.getPlayerUUID()).sendMessage(errorprefix + "§cJe had niet genoeg geld om de huur te betalen voor plot: " + verhuurdePlot.getPlotID());
                    }
                    if (debug()) {
                        String playername = Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID()).getName();
                        Bukkit.getServer().broadcastMessage(debugprefix + playername + " Kon de huur niet betalen en loopt nu " + (verhuurdePlot.getDaysPaymentMissed() + 1) + " dagen achter");
                    }
                    verhuurdePlot.setDaysPaymentMissed(verhuurdePlot.getDaysPaymentMissed() + 1);
                    verhuurdePlot.setPayed(false);
                    if (verhuurdePlot.getDaysPaymentMissed() >= 2) {
                        if (isPlotOwner(verhuurdePlot)) {
                            removePlotMember(verhuurdePlot);
                            if (playerOnline(verhuurdePlot.getPlayerUUID())) {
                                Bukkit.getPlayer(verhuurdePlot.getPlayerUUID()).sendMessage(errorprefix + "§cJe hebt 2 keer je huur niet betaald voor plot: " + verhuurdePlot.getPlotID());
                            }
                        }
                        if (debug()) {
                            String playername = Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID()).getName();
                            Bukkit.getServer().broadcastMessage(debugprefix + playername + " Heeft 2x niet de huur betaald en is verwijderd als plot eigenaar");
                        }
                    }
                } else {
                    verhuurdePlot.setPayed(true);
                    verhuurdePlot.setDaysPaymentMissed(0);
                    Bankaccount bankaccount = nl.minetopiasdb.api.banking.BankUtils.getInstance().getBankAccount(verhuurdePlot.getBanknumber());
                    bankaccount.setBalance(verhuurdePlot.getPrice() + bankaccount.getBalance());
                    economy.withdrawPlayer(Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID()), verhuurdePlot.getPrice());
                    if (!isPlotOwner(verhuurdePlot)) {
                        addPlotMember(verhuurdePlot);
                    }
                    if (debug()) {
                        String playername = Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID()).getName();
                        Bukkit.getServer().broadcastMessage(debugprefix + playername + " Heeft de huur betaald voor plot: " + verhuurdePlot.getPlotID());
                        Bukkit.getServer().broadcastMessage(debugprefix + "En er is " + verhuurdePlot.getPrice() + ",- toegevoeg op de rekeninnummer " + verhuurdePlot.getBanknumber());
                    }
                    if (playerOnline(verhuurdePlot.getPlayerUUID())) {
                        Bukkit.getPlayer(verhuurdePlot.getPlayerUUID()).sendMessage(prefix + "§aJe hebt " + verhuurdePlot.getPrice() + ",- huur betaald voor plot: " + verhuurdePlot.getPlotID());
                    }
                }
                verhuurdePlot.setLastPaymentDate(currentTimeMillis);
            }
        }
    }

    public void addPlotMember(VerhuurdePlot verhuurdePlot) {
        List<World> world = Bukkit.getServer().getWorlds();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        for (World world1 : world) {
            RegionManager regionManager = container.get(BukkitAdapter.adapt(world1));
            if (regionManager.getRegions().isEmpty()) {
                break;
            }
            if (regionManager.hasRegion(verhuurdePlot.getPlotID())) {
                ProtectedRegion region = regionManager.getRegion(verhuurdePlot.getPlotID());
                DefaultDomain owners = region.getOwners();
                owners.addPlayer(verhuurdePlot.getPlayerUUID());
                region.setOwners(owners);
                if (debug()) {
                    String playername = Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID()).getName();
                    Bukkit.getServer().broadcastMessage(debugprefix + playername + " Is nu toegevoegd als eigenaar aan plot: " + verhuurdePlot.getPlotID());
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(errorprefix + "§cProbeerde speler toe te voegen aan een plot dat niet bestaat");
            }
        }
    }

    public void removePlotMember(VerhuurdePlot verhuurdePlot) {
        List<World> world = Bukkit.getServer().getWorlds();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        for (World world1 : world) {
            RegionManager regionManager = container.get(BukkitAdapter.adapt(world1));
            if (regionManager.getRegions().isEmpty()) {
                break;
            }
            if (regionManager.hasRegion(verhuurdePlot.getPlotID())) {
                ProtectedRegion region = regionManager.getRegion(verhuurdePlot.getPlotID());
                DefaultDomain owners = region.getOwners();
                if (owners.contains(verhuurdePlot.getPlayerUUID())) {
                    owners.removePlayer(verhuurdePlot.getPlayerUUID());
                    region.setOwners(null);
                    if (debug()) {
                        String playername = Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID()).getName();
                        Bukkit.getServer().broadcastMessage(debugprefix + playername + " Is nu verwijderd als eigenaar aan plot: " + verhuurdePlot.getPlotID());
                    }
                }
            }
        }
    }

    public boolean isPlotOwner(VerhuurdePlot verhuurdePlot) {
        List<World> world = Bukkit.getServer().getWorlds();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        for (World world1 : world) {
            RegionManager regionManager = container.get(BukkitAdapter.adapt(world1));
            if (regionManager.getRegions().isEmpty()) {
                break;
            }
            if (regionManager.hasRegion(verhuurdePlot.getPlotID())) {
                ProtectedRegion region = regionManager.getRegion(verhuurdePlot.getPlotID());
                DefaultDomain owners = region.getOwners();
                if (owners.contains(verhuurdePlot.getPlayerUUID())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void showTimeRemaining(Player p) {
        long currentTimeMillis = System.currentTimeMillis();
        boolean foundRentedPlot = false;
        for (VerhuurdePlot verhuurdePlot : verhuurdePlotList) {
            UUID playerUUID = verhuurdePlot.getPlayerUUID();
            if (playerUUID.equals(p.getUniqueId())) {
                foundRentedPlot = true;
                long nextPaymentTimeMillis = verhuurdePlot.getLastPaymentDate() + TimeUnit.DAYS.toMillis(verhuurdePlot.getDaysbetweenpayment());
                long timeDifferenceMillis = (nextPaymentTimeMillis - currentTimeMillis);
                if (timeDifferenceMillis > 0) {
                    long daysRemaining = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis);
                    long hoursRemaining = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis) % 24;
                    long minutesRemaining = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis) % 60;
                    long secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceMillis) % 60;

                    p.sendMessage("§3Tijd tot volgende betaling: §a" + daysRemaining + " dagen, " + hoursRemaining + " uur, " + minutesRemaining + " minuten, " + secondsRemaining + " seconden");
                } else {
                    p.sendMessage(prefix + "§bDe betaling wordt binnen 5 minuten uitgevoerd");
                }
            }
        }
        if (!foundRentedPlot) {
            p.sendMessage(errorprefix + "§cJij hebt geen aankomende betalingen");
        }
    }


    public void stuurLoadedVerhuurdePlots(Player p) {
        if (verhuurdePlotList.isEmpty()) {
            p.sendMessage(errorprefix + "§cEr zijn geen verhuurde plots geladen");
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        p.sendMessage(prefix + "§b§oDit zijn alle verhuurde plots");
        for (VerhuurdePlot verhuurdePlot : verhuurdePlotList) {
            String betaald;
            String playernaam = Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID()).getName();
            long nextPaymentTimeMillis = verhuurdePlot.getLastPaymentDate() + TimeUnit.DAYS.toMillis(verhuurdePlot.getDaysbetweenpayment());
            long timeDifferenceMillis = (nextPaymentTimeMillis - currentTimeMillis);
            long days = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceMillis) % 60;
            String timeRemaining = String.format("%dD:%dH:%dM:%dS", days, hours, minutes, seconds);
            String category = verhuurdePlot.getCategory();
            if (verhuurdePlot.hasPayed()) {
                betaald = "Betaald";
                p.sendMessage("§b- §c" + playernaam + " §b|§c " + verhuurdePlot.getPlotID() + " §b|§c €" + verhuurdePlot.getPrice() + " om de " + verhuurdePlot.getDaysbetweenpayment() + " dagen §b|§c " + betaald + " §b|§c " + timeRemaining + " §b|§c " + category);
            } else {
                betaald = "Onbetaald";
                p.sendMessage("§b- §c" + playernaam + " §b|§c " + verhuurdePlot.getPlotID() + " §b|§c €" + verhuurdePlot.getPrice() + " om de " + verhuurdePlot.getDaysbetweenpayment() + " dagen §b|§c " + betaald + " §b|§c " + verhuurdePlot.getDaysPaymentMissed() + " §b|§c " + timeRemaining + " §b|§c " + category);
            }
        }
    }

    public void removeVerhuurdePlot(Player p, String plotID) {
        String path = "verhuurdePlots." + plotID;
        List<VerhuurdePlot> plotsToRemove = new ArrayList<>();
        for (VerhuurdePlot verhuurdePlot : verhuurdePlotList) {
            if (verhuurdePlot.getPlotID().equals(plotID)) {
                if (getConfig().contains(path)) {
                    getConfig().set(path, null); // Remove the entire section for the specified plot
                    saveConfig(); // Save changes to the configuration file
                    plotsToRemove.add(verhuurdePlot);
                    removePlotMember(verhuurdePlot);
                    p.sendMessage(prefix + "§6" + plotID + " §aHuur contract verwijderd");
                } else {
                    p.sendMessage(prefix + "§6" + plotID + " §aHuur contract niet gevonden");
                }
            }
        }
        verhuurdePlotList.removeAll(plotsToRemove);
    }


    public void sendPlotInfo(String plotID, Player p) {
        long currentTimeMillis = System.currentTimeMillis();
        for (VerhuurdePlot verhuurdePlot : verhuurdePlotList) {
            if (verhuurdePlot.getPlotID().equals(plotID)) {
                String betaald;
                if (verhuurdePlot.hasPayed()) {
                    betaald = "§aBetaald";
                } else {
                    betaald = "§cOnbetaald";
                }
                p.sendMessage("§bPlot info van plot: §a" + verhuurdePlot.getPlotID());
                p.sendMessage("§3Heeft betaald: §a" + betaald);
                p.sendMessage("§3Verhuurd aan: §a" + Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID()).getName());
                p.sendMessage("§3Prijs: §a" + verhuurdePlot.getPrice());
                p.sendMessage("§3Betaalt om de §a" + verhuurdePlot.getDaysbetweenpayment() + " §3dagen");
                p.sendMessage("§3Heeft §a" + verhuurdePlot.getDaysPaymentMissed() + " §3periodes niet betaald");
                p.sendMessage("§3Wordt op rekening §a" + verhuurdePlot.getBanknumber() + " §3overgeschreven");

                long nextPaymentTimeMillis = verhuurdePlot.getLastPaymentDate() + TimeUnit.DAYS.toMillis(verhuurdePlot.getDaysbetweenpayment());
                long timeDifferenceMillis = (nextPaymentTimeMillis - currentTimeMillis);
                long daysRemaining = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis);
                long hoursRemaining = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis) % 24;
                long minutesRemaining = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis) % 60;
                long secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceMillis) % 60;


                p.sendMessage("§3Tijd tot volgende betaling: §a" + daysRemaining + " dagen, " + hoursRemaining + " uur, " + minutesRemaining + " minuten, " + secondsRemaining + " seconden");
            }
        }
    }

    public List<String> returnLoadedVerhuurdePlots() {
        List<String> list = new ArrayList<>();
        for (VerhuurdePlot verhuurdePlot : verhuurdePlotList) {
            list.add(verhuurdePlot.getPlotID());
        }
        return list;
    }

    private boolean playerOnline(UUID playerUUID) {
        return Bukkit.getOfflinePlayer(playerUUID).isOnline();
    }

    public void huurbetaalforce() {
        if (debug()) {
            Bukkit.getServer().broadcastMessage(debugprefix + "Huur betaling zijn geforceert gecontroleert");
        }
        Bukkit.getServer().broadcastMessage(prefix + "§aHuur betaling zijn geforceert gecontroleert");
        long currentTimeMillis = System.currentTimeMillis();
        for (VerhuurdePlot verhuurdePlot : verhuurdePlotList) {
            if (economy.getBalance(Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID())) < verhuurdePlot.getPrice()) {
                if (playerOnline(verhuurdePlot.getPlayerUUID())) {
                    Bukkit.getPlayer(verhuurdePlot.getPlayerUUID()).sendMessage(errorprefix + "§cJe had niet genoeg geld om de huur te betalen voor plot: " + verhuurdePlot.getPlotID());
                }
                if (debug()) {
                    String playername = Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID()).getName();
                    Bukkit.getServer().broadcastMessage(debugprefix + playername + " Kon de huur niet betalen en loopt nu " + (verhuurdePlot.getDaysPaymentMissed() + 1) + " dagen achter");
                }
                verhuurdePlot.setDaysPaymentMissed(verhuurdePlot.getDaysPaymentMissed() + 1);
                verhuurdePlot.setPayed(false);
                if (verhuurdePlot.getDaysPaymentMissed() >= 2) {
                    removePlotMember(verhuurdePlot);
                    if (debug()) {
                        String playername = Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID()).getName();
                        Bukkit.getServer().broadcastMessage(debugprefix + playername + " Heeft 2x niet de huur betaald en is verwijderd als plot eigenaar");
                    }
                    if (playerOnline(verhuurdePlot.getPlayerUUID()) && isPlotOwner(verhuurdePlot)) {
                        removePlotMember(verhuurdePlot);
                        Bukkit.getPlayer(verhuurdePlot.getPlayerUUID()).sendMessage(errorprefix + "§cJe hebt 2 keer je huur niet betaald voor plot: " + verhuurdePlot.getPlotID());
                    }
                }
            } else {
                verhuurdePlot.setPayed(true);
                verhuurdePlot.setDaysPaymentMissed(0);
                Bankaccount bankaccount = nl.minetopiasdb.api.banking.BankUtils.getInstance().getBankAccount(verhuurdePlot.getBanknumber());
                bankaccount.setBalance(verhuurdePlot.getPrice() + bankaccount.getBalance());
                economy.withdrawPlayer(Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID()), verhuurdePlot.getPrice());
                if (!isPlotOwner(verhuurdePlot)) {
                    addPlotMember(verhuurdePlot);
                }
                if (debug()) {
                    String playername = Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID()).getName();
                    Bukkit.getServer().broadcastMessage(debugprefix + playername + " Heeft de huur betaald voor plot: " + verhuurdePlot.getPlotID());
                    Bukkit.getServer().broadcastMessage(debugprefix + "En er is " + verhuurdePlot.getPrice() + ",- toegevoeg op de rekeninnummer " + verhuurdePlot.getBanknumber());
                }
                if (playerOnline(verhuurdePlot.getPlayerUUID())) {
                    Bukkit.getPlayer(verhuurdePlot.getPlayerUUID()).sendMessage(prefix + "§aJe hebt " + verhuurdePlot.getPrice() + ",- huur betaald voor plot: " + verhuurdePlot.getPlotID());
                }
            }
            verhuurdePlot.setLastPaymentDate(currentTimeMillis);
        }
    }

    public void betaalachterstand(Player p, String playername) {
        Player huurder = Bukkit.getPlayer(playername);
        for (VerhuurdePlot verhuurdePlot : verhuurdePlotList) {
            if (verhuurdePlot.getPlayerUUID().equals(huurder.getUniqueId())) {
                double price = (((double) verhuurdePlot.getDaysPaymentMissed() /verhuurdePlot.getDaysbetweenpayment()) * verhuurdePlot.getPrice());
                if (economy.getBalance(Bukkit.getOfflinePlayer(playername)) >= price) {
                    if (verhuurdePlot.getDaysPaymentMissed() >= 1) {
                        laatBetalerList.put(huurder.getUniqueId(), p.getUniqueId());
                        p.sendMessage(prefix + "§aDe speler moet nu de achterstands betaling bevestigen");
                        p.sendMessage(prefix + "§aDe extra kosten zijn: §b" + price + ",-");
                        TextComponent message = new TextComponent(prefix + "§3Klik hier om de huur betaling te bevestigen");
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/huur bevestig"));
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aBevestig huur betaling prijs: §b" + price + ",-").create()));
                        huurder.spigot().sendMessage(message);
                    } else {
                        p.sendMessage(errorprefix + "§cDe huurder is nog steeds eigenaar en heeft geen achterstand");
                    }
                } else {
                    p.sendMessage(errorprefix + "§cDe huurder heeft niet genoeg geld om te betalen.");
                }
            }
        }
    }

    public void confirmedbetaalachterstand(Player huurder){
        long currentTimeMillis = System.currentTimeMillis();
        for (VerhuurdePlot verhuurdePlot : verhuurdePlotList) {
            if (verhuurdePlot.getPlayerUUID().equals(huurder.getUniqueId())) {
                if(laatBetalerList.isEmpty()){return;};
                    if(laatBetalerList.containsKey(huurder.getUniqueId())){
                        double price = (((double) verhuurdePlot.getDaysPaymentMissed() /verhuurdePlot.getDaysbetweenpayment()) * verhuurdePlot.getPrice());
                        verhuurdePlot.setPayed(true);
                        verhuurdePlot.setDaysPaymentMissed(0);
                        Bankaccount bankaccount = nl.minetopiasdb.api.banking.BankUtils.getInstance().getBankAccount(verhuurdePlot.getBanknumber());
                        bankaccount.setBalance(price + bankaccount.getBalance());
                        economy.withdrawPlayer(Bukkit.getOfflinePlayer(verhuurdePlot.getPlayerUUID()), price);
                        huurder.sendMessage(prefix + "§aJe hebt " + price + ",- huur betaald voor plot: " + verhuurdePlot.getPlotID());
                        verhuurdePlot.setLastPaymentDate(currentTimeMillis);
                        addPlotMember(verhuurdePlot);
                        Bukkit.getPlayer(laatBetalerList.get(huurder.getUniqueId())).sendMessage(prefix + "§a" + huurder.getName() + " Heeft zijn huur hersteld");
                        laatBetalerList.remove(huurder.getUniqueId());
                    } else {
                        huurder.sendMessage(errorprefix+"§cJe hebt niks om te bevestigen");
                }
            }
        }
    }

    private void loadCategorien(){
        FileConfiguration categorieconfig = CategorienConfig.getCategorienfileconfig();
        if(!categorieconfig.contains("categorien")){
            return;
        }
        for(String s : categorieconfig.getConfigurationSection("categorien").getKeys(false)){
            this.categorien.add(new HuurCategory(s,categorieconfig.getInt("categorien."+s)));
        }
    }

    public void saveCategorien(){
        FileConfiguration categorieconfig = CategorienConfig.getCategorienfileconfig();
        if(categorien.isEmpty()){categorieconfig.set("categorien",null); CategorienConfig.save(); return;}
        for(HuurCategory category : categorien){
            categorieconfig.set("categorien."+category.getCategory(), category.getBankid());
        }
        CategorienConfig.save();
    }

    public void initializeHuur(Player p, VerhuurdePlot verhuurdePlot){
        p.closeInventory();
        if(economy.getBalance(p) < verhuurdePlot.getPrice()){
            p.sendMessage(errorprefix + "§cJe hebt het geld nu niet om te betalen!");
            removeFromConfirmingMap(p);
            return;
        }

        Double price = verhuurdePlot.getPrice();
        verhuurdePlotList.add(verhuurdePlot);
        saveVerhuurdePlots();
        Bankaccount bankaccount = nl.minetopiasdb.api.banking.BankUtils.getInstance().getBankAccount(verhuurdePlot.getBanknumber());
        bankaccount.setBalance(verhuurdePlot.getPrice() + bankaccount.getBalance());
        addPlotMember(verhuurdePlot);
        p.sendMessage(prefix + "§bHuur contract toegevoegd met deze info: ");
        economy.withdrawPlayer(p, price);
        verhuurdePlot.setDaysPaymentMissed(0);
        p.sendMessage(prefix + "§bJe hebt " + price + ".- huur betaald voor plot: " + verhuurdePlot.getPlotID());
        verhuurdePlot.setPayed(true);
        verhuurdePlot.setLastPaymentDate(System.currentTimeMillis());
        sendPlotInfo(verhuurdePlot.getPlotID(), p);
        removeFromConfirmingMap(p);
    }

    private void removeFromConfirmingMap(Player p){
        Iterator<Map.Entry<UUID, VerhuurdePlot>> iterator = confirmhuurcontract.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, VerhuurdePlot> entry = iterator.next();
            if (entry.getKey().equals(p.getUniqueId())) {
                iterator.remove();
                break;
            }
        }
    }
}
