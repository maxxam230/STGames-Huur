package me.max.stgameshuur.objects;

import java.util.UUID;

public class VerhuurdePlot {
        private UUID playerUUID;
        private String plotID;
        private long lastPaymentDate;
        private double price;
        private int banknumber;
        private int daysbetweenpayment;
        private int daysPaymentMissed;
        private boolean payed;
        private String category;


        public VerhuurdePlot(UUID playerUUID, String plotID, long lastPaymentDate, double price, int banknumber, int daysbetweenpayment, boolean payed, int dayspaymentmissed, String category) {
            this.playerUUID = playerUUID;
            this.plotID = plotID;
            this.lastPaymentDate = lastPaymentDate;
            this.price = price;
            this.banknumber = banknumber;
            this.daysbetweenpayment = daysbetweenpayment;
            this.payed = payed;
            this.daysPaymentMissed = dayspaymentmissed;
            this.category = category;
        }

    public void setLastPaymentDate(long lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public String getPlotID() {
            return plotID;
    }

    public UUID getPlayerUUID() {
            return playerUUID;
    }

    public Long getLastPaymentDate() {
            return lastPaymentDate;
    }

    public Double getPrice() {
            return price;
    }

    public int getBanknumber() {
        return banknumber;
    }

    public int getDaysbetweenpayment(){
            return  daysbetweenpayment;
        }

    public void setDaysbetweenpayment(int daysbetweenpayment) {
        this.daysbetweenpayment = daysbetweenpayment;
    }

    public boolean hasPayed() { return payed; }

    public void setPayed(boolean hasPayed){
            this.payed = hasPayed;
    }

    public int getDaysPaymentMissed() {
        return daysPaymentMissed;
    }

    public void setDaysPaymentMissed(int daysPaymentMissed) {
        this.daysPaymentMissed = daysPaymentMissed;
    }

    public String getCategory() {
        return category;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
