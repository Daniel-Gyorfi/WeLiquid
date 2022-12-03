package edu.uga.cs.weliquid;

import java.util.ArrayList;

/**
 * A single item in the shopping list, including the
 * name of the item and the roommate who added the item to the list
 */
public class PurchaseBasketItem {
    private String key;
    private ArrayList<String> itemList;
    private String rmName;
    private String itemTime;

    public PurchaseBasketItem() {
        this.key = null;
        this.itemList = null;
        this.rmName = null;
        this.itemTime = null;
    }

    public PurchaseBasketItem(ArrayList<String> items) {
        this.itemList = items;
    }

    public PurchaseBasketItem(ArrayList<String> items, String rmName, String itemTime) {
        this.itemList = items;
        this.rmName = rmName;
        this.itemTime = itemTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<String> getItemList() { return itemList; }

    public void setItemList(ArrayList<String> items) { this.itemList = items; }

    public String getRmName() { return rmName; }

    public void setRmName(String rmName) { this.rmName = rmName; }

    public String getItemTime() { return itemTime; }

    public void setItemTime(String itemTime) { this.itemTime = itemTime; }
}
