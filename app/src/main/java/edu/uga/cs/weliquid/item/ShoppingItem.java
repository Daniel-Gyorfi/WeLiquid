package edu.uga.cs.weliquid.item;

import android.util.Log;

import androidx.annotation.NonNull;

/**
 * This class represents a single item in the shopping list, including the
 * name of the item and the roommate who added the item to the list.
 */
public class ShoppingItem {
    private String key;
    private String itemName;
    private String rmName;
    private String itemTime;

    public ShoppingItem() {
        this.key = null;
        this.itemName = null;
        this.rmName = null;
        this.itemTime = null;
    }

    public ShoppingItem(String itemName, String rmName, String itemTime) {
        this.itemName = itemName;
        this.rmName = rmName;
        this.itemTime = itemTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getItemName() { return itemName; }

    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getRmName() { return rmName; }

    public void setRmName(String rmName) { this.rmName = rmName; }

    public String getItemTime() { return itemTime; }

    public void setItemTime(String itemTime) { this.itemTime = itemTime; }

//    public boolean equals(@NonNull ShoppingItem item) {
//        Log.d("Shopping Item", "key: " + this.key + " " +
//                item.getKey() + " name " + this.itemName + " " + item.getItemName());
//        return item.getKey().equals(this.key)
//                && item.getItemName().equals(this.itemName)
//                && item.getRmName().equals(this.rmName)
//                && item.getItemName().equals(this.rmName);
//    }
}
