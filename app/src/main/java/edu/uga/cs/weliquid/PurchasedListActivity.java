package edu.uga.cs.weliquid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PurchasedListActivity extends AppCompatActivity {

    public static final String DEBUG_TAG = "ShoppingListActivity";

    private RecyclerView recyclerView;
    private PurchaseItemRecyclerAdapter recyclerAdapter;
    private List<PurchaseBasketItem> purchaseItemsList;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d( DEBUG_TAG, "onCreate()" );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased_list);
        setTitle("Purchased List");

        final ActionBar ab = getSupportActionBar();
        assert ab != null;

        recyclerView = findViewById( R.id.recyclePurchase );

        // initialize the shopping list
        purchaseItemsList = new ArrayList<PurchaseBasketItem>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with shopping list items is empty at first; it will be updated later
        recyclerAdapter = new PurchaseItemRecyclerAdapter( purchaseItemsList, PurchasedListActivity.this );
        recyclerView.setAdapter( recyclerAdapter );

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("purchaseItems");

        // Set up a listener (event handler) to receive a value for the database reference.
        // This type of listener is called by Firebase once by immediately executing its onDataChange method
        // and then each time the value at Firebase changes.
        //
        // This listener will be invoked asynchronously, hence no need for an AsyncTask class.
        myRef.addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our shopping list.
                purchaseItemsList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    PurchaseBasketItem shopItem = postSnapshot.getValue(PurchaseBasketItem.class);
                    shopItem.setKey( postSnapshot.getKey() );
                    purchaseItemsList.add( shopItem );
                    Log.d( DEBUG_TAG, "ValueEventListener: added: " + shopItem );
                    Log.d( DEBUG_TAG, "ValueEventListener: key: " + postSnapshot.getKey() );
                }

                Log.d( DEBUG_TAG, "ValueEventListener: notifying recyclerAdapter" );
                // notifying the recycler that the set of data has changed
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {
                System.out.println( "ValueEventListener: reading failed: " + databaseError.getMessage() );
            }
        } );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.purchased_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            DialogFragment logoutFrag = new LogoutDialogFragment();
            logoutFrag.show( getSupportFragmentManager(), null);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This is an adapter class for the RecyclerView to show all shopping list items.
     */
    private class PurchaseItemRecyclerAdapter extends RecyclerView.Adapter<PurchaseItemRecyclerAdapter.PurchaseItemHolder> {
        public static final String DEBUG_TAG = "ShopItemRecyclerAdapter";

        private List<PurchaseBasketItem> purchaseList;

        private Context context;

        public PurchaseItemRecyclerAdapter(List<PurchaseBasketItem> boughtList, Context context) {
            this.purchaseList = boughtList;
            this.context = context;
        }

        // The adapter must have a ViewHolder class to "hold" one item to show.
        public class PurchaseItemHolder extends RecyclerView.ViewHolder {

            int position = -1;
            TextView itemName;
            TextView rmName;
            TextView itemTime;
            CheckBox box;

            public PurchaseItemHolder(View itemView) {
                super(itemView);

                itemName = itemView.findViewById(R.id.itemName);
                rmName = itemView.findViewById(R.id.roommateName);
                itemTime = itemView.findViewById(R.id.userTime);
                box = itemView.findViewById(R.id.checkBox);
            }
        }

        @NonNull
        @Override
        public PurchaseItemRecyclerAdapter.PurchaseItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item, parent, false);
            return new PurchaseItemRecyclerAdapter.PurchaseItemHolder(view);
        }

        // This method fills in the values of the Views to show a ShoppingItem
        @Override
        public void onBindViewHolder(PurchaseItemRecyclerAdapter.PurchaseItemHolder holder, @SuppressLint("RecyclerView") int position) {
            PurchaseBasketItem shoppingItem = purchaseList.get(position);

            Log.d(DEBUG_TAG, "Bind: " + position);

            String key = shoppingItem.getKey();
            String itemName = String.valueOf(shoppingItem.getItemList());
            String userName = shoppingItem.getRmName();
            String userTime = shoppingItem.getItemTime();

            holder.position = position;
            holder.itemName.setText(shoppingItem.getItemList().toString());
            holder.rmName.setText(shoppingItem.getRmName());
            holder.itemTime.setText(shoppingItem.getItemTime());

            // We can attach an OnClickListener to the itemView of the holder;
            // itemView is a public field in the Holder class.
            // It will be called when the user taps/clicks on the whole item, i.e., one of
            // the shopping list items shown.
            // This will indicate that the user wishes to edit (modify or delete) this item.
            // We create and show an EditShoppingItemDialogFragment.
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holder.box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return purchaseList.size();
        }
    }
}