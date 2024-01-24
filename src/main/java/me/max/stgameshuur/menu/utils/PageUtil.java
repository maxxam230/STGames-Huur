package me.max.stgameshuur.menu.utils;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PageUtil {

    public static List<ItemStack> getPageItems(List<ItemStack> itemStacks, int page, int spaces){
        int upperBound = page * spaces;
        int lowerBound = upperBound - spaces;


        List<ItemStack> newPage = new ArrayList<>();
        for(int i = lowerBound; i<upperBound; i++){
            try {
                newPage.add(itemStacks.get(i));
            } catch (IndexOutOfBoundsException exception){
                break;
            }
        }
        return newPage;
    }



    public static boolean isPageValid(List<ItemStack> itemStacks, int page, int spaces) {
        if (page <= 0) {
            return false;
        }

        int upperBound = page * spaces;
        int lowerBound = upperBound - spaces;

        return itemStacks.size() > lowerBound;
    }




}
