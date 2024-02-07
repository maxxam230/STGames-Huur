package me.max.stgameshuur.events;

import me.max.stgameshuur.Main;
import me.max.stgameshuur.menu.AddHuurPlotMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class SendChat  implements Listener {

    Main plugin;

    String prefix = Main.prefix;
    String errorprefix = Main.errorprefix;

    public SendChat(Main instance) {
        plugin = instance;
    }

    @EventHandler
    public void onChat(PlayerChatEvent e){
        Player p = (Player) e.getPlayer();
        if(plugin.setpriceforplot.containsKey(p.getUniqueId())){
            try {
                Double newprice = Double.valueOf(e.getMessage());
                plugin.setpriceforplot.get(p.getUniqueId()).setPrice(newprice);
                p.sendMessage(prefix + "§aNieuwe prijs ingesteld: §6€"+newprice+",-");
            }catch (Exception error){
                p.sendMessage(errorprefix+"§cDit is geen geldig bedrag!");
            }
            plugin.setpriceforplot.remove(p.getUniqueId());
            e.setCancelled(true);
        }


        if(plugin.addingtimeordays.containsKey(p.getUniqueId())){
            Integer timeorday = plugin.addingtimeordays.get(p.getUniqueId()); //1=days 2=prijs
            if(timeorday == 1){
                try {
                    plugin.addingplot.get(p.getUniqueId()).setDagen(Integer.valueOf(e.getMessage()));
                    new AddHuurPlotMenu(p, plugin.addingplot.get(p.getUniqueId()),plugin.categorien);
                    plugin.addingtimeordays.remove(p.getUniqueId());
                }catch (Exception e1){
                    p.sendMessage(errorprefix + "§cJe moet alleen een nummer invoeren!");
                }
            } else if(timeorday == 2){
                try {
                    plugin.addingplot.get(p.getUniqueId()).setPrijs(Integer.valueOf(e.getMessage()));
                    new AddHuurPlotMenu(p, plugin.addingplot.get(p.getUniqueId()),plugin.categorien);
                    plugin.addingtimeordays.remove(p.getUniqueId());
                }catch (Exception e2){
                    p.sendMessage(errorprefix + "§cJe moet alleen een nummer invoeren!");
                }
            }
            e.setCancelled(true);
        }
    }


}
