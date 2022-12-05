package edu.uga.cs.weliquid;

/**
 * A single item in a basket in the recently purchased list
 */
public class PurchaseItem {
    private String itemKey;
    private String purchaseItemName;

    public PurchaseItem() {
        this.itemKey = null;
        this.purchaseItemName = null;
    }

    public PurchaseItem(String itemName) { this.purchaseItemName = itemName; }

    public String getItemKey() { return itemKey; }

    public void setItemKey(String key) { this.itemKey = key; }

    public String getPurchaseItemName() { return purchaseItemName; }

    public void setPurchaseItemName(String nameItem) { this.purchaseItemName = nameItem; }
}
