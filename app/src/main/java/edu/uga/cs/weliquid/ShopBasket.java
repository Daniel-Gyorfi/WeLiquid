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

        public void add(ShoppingItem item) {
            items.add( item );
        }

        public void clear() {
            this.items.clear();
        }

        public boolean containsKey( String key ) {
            for ( ShoppingItem item : items ) {
                if ( key.equals(item.getKey()) ) return true;
            }
            return false;
        }

        public Boolean empty() {
            return items.isEmpty();
        }

        public ArrayList<String> getList() {
            ArrayList<String> list = new ArrayList<>();
            for ( ShoppingItem item : items) {
                list.add( item.getItemName() );
            }
            return list;
        }
}
