package me.max.stgameshuur.objects;

import org.bukkit.entity.Player;

import java.util.UUID;

public class LaatBetaler {
    private Player pgemeente;
    private Player phuurder;
    private VerhuurdePlot verhuurdePlot;
    private double laatprijs;


    public LaatBetaler(Player pgemeente, Player phuurder, VerhuurdePlot verhuurdePlot, Double laatprijs) {
        this.pgemeente = pgemeente;
        this.phuurder = phuurder;
        this.verhuurdePlot = verhuurdePlot;
        this.laatprijs = laatprijs;
    }


    public Player getPhuurder() {
        return phuurder;
    }

    public Player getPgemeente() {
        return pgemeente;
    }

    public double getLaatprijs() {
        return laatprijs;
    }
}
