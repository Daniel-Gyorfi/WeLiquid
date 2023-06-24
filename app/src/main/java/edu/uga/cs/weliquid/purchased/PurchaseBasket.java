package edu.uga.cs.weliquid.purchased;

import java.util.List;

import edu.uga.cs.weliquid.item.PurchaseItem;

/**
 * A single basket item in the recently purchased list,
 * including all the items in the basket, the total cost
 * of these items, the time at which they were purchased,
 * and the person who purchased them
 */
public class PurchaseBasket {
    private String key;
    private List<PurchaseItem> itemList;
    private String rmName;
    private String itemTime;
    private String cost;

    public PurchaseBasket() {
        this.key = null;
        this.itemList = null;
        this.rmName = null;
        this.itemTime = null;
        this.cost = null;
    }

    public PurchaseBasket(List<PurchaseItem> items) {
        this.itemList = items;
    }

    public PurchaseBasket(List<PurchaseItem> items, String price, String rmName, String itemTime) {
        this.itemList = items;
        this.cost = price;
        this.rmName = rmName;
        this.itemTime = itemTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<PurchaseItem> getItemList() {
        if (itemList == null) {
            return null;
        }
        for (PurchaseItem item : itemList) {
            if (item != null) item.setItemKey(key);
        }
        return itemList;
    }

    public void setItemList(List<PurchaseItem> items) { this.itemList = items; }

    public String getRmName() { return rmName; }

    public void setRmName(String rmName) { this.rmName = rmName; }

    public String getItemTime() { return itemTime; }

    public void setItemTime(String itemTime) { this.itemTime = itemTime; }

    public String getCost() {
        return cost;
    }
}
