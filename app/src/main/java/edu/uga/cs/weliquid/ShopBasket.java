package edu.uga.cs.weliquid;

import java.util.ArrayList;

public class ShopBasket {
    ArrayList<ShoppingItem> items;
    static ShopBasket instance;

        private ShopBasket() {
            items = new ArrayList<ShoppingItem>();
        }

        public static ShopBasket getInstance() {
            if (instance == null) {
                instance = new ShopBasket();
            }
            return instance;
        }

        public Boolean empty() {
            return items.isEmpty();
        }
}
