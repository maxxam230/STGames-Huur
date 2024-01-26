package me.max.stgameshuur.objects;

public class HuurCategory {

    private String category;
    private Integer bankid;

    public HuurCategory(String category, Integer bankid){
        this.category = category;
        this.bankid = bankid;
    }

    public String getCategory() {
        return category;
    }

    public Integer getBankid() {
        return bankid;
    }


}
