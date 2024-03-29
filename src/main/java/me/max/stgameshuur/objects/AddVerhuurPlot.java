package me.max.stgameshuur.objects;

import org.jetbrains.annotations.Nullable;

public class AddVerhuurPlot {

    private String huurder;
    private String categorie;
    private String plot;
    private Integer prijs;
    private Integer dagen;


    public AddVerhuurPlot(@Nullable String huurder,@Nullable String categorie,@Nullable String plot,@Nullable Integer prijs,@Nullable Integer dagen){
        if(!(huurder == null)){
            this.huurder = huurder;
        }
        if(!(categorie == null)) {
            this.categorie = categorie;
        }
        if(!(plot == null)){
            this.plot = plot;
        }
        if(!(prijs == null)){
            this.prijs = prijs;
        }
        if(!(dagen == null)){
            this.dagen = dagen;
        }
    }

    public String getHuurder() {
        if(huurder == null){return null;}
        return huurder;
    }

    public void setHuurder(String huurder) {
        this.huurder = huurder;
    }

    public String getCategorie() {
        if(categorie == null){return null;}
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getPlot() {
        if(plot == null){return null;}
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public Integer getPrijs() {
        if(prijs == null){return null;}
        return prijs;
    }

    public void setPrijs(Integer prijs) {
        this.prijs = prijs;
    }

    public Integer getDagen() {
        if(dagen == null){return null;}
        return dagen;
    }

    public void setDagen(Integer dagen) {
        this.dagen = dagen;
    }
}
