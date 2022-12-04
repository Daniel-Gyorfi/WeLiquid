package edu.uga.cs.weliquid;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShopBasket {
    private static final String DEBUG_TAG = "ShopBasket";
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

        public void remove(ShoppingItem item) { items.remove( item ); }

        public void removeKey(String key) {
            for ( ShoppingItem item : items ) {
                if ( key.equals(item.getKey()) )
                    items.remove(item);
                    break;
            }
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

        public void removeFromShoppingList(Context context) {
            //remove basket items from shopping list
            DatabaseReference fire = FirebaseDatabase.getInstance()
                    .getReference("shoppingItems");
            for (ShoppingItem item : items) {
                DatabaseReference ref = fire.child( item.getKey() );

                // This listener will be invoked asynchronously, hence no need for an AsyncTask class.
                ref.addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                        dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d( DEBUG_TAG, "deleted shopping list item: (" + item.getItemName() + ")" );
                                Toast.makeText(context, "Shopping list card deleted for " + item.getItemName(),
                                        Toast.LENGTH_SHORT).show();                        }
                        });
                    }

                    @Override
                    public void onCancelled( @NonNull DatabaseError databaseError ) {
                        Log.d( DEBUG_TAG, "failed to delete shopping list item: (" + item.getItemName() + ")" );
                        Toast.makeText(context, "Failed to delete " + item.getItemName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
}
