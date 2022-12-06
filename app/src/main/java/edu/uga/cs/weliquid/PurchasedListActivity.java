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

    public static final String DEBUG_TAG = "PurchaseListActivity";

    private RecyclerView recyclerView;
    private PurchaseBasketRecyclerAdapter recyclerAdapter;
    private List<PurchaseBasket> purchaseBasketItemsList;
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
        purchaseBasketItemsList = new ArrayList<PurchaseBasket>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with shopping list items is empty at first; it will be updated later
        recyclerAdapter = new PurchaseBasketRecyclerAdapter( purchaseBasketItemsList, PurchasedListActivity.this );
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
                purchaseBasketItemsList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    PurchaseBasket shopItem = postSnapshot.getValue(PurchaseBasket.class);
                    shopItem.setKey( postSnapshot.getKey() );
                    purchaseBasketItemsList.add( shopItem );
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
    private class PurchaseBasketRecyclerAdapter extends RecyclerView.Adapter<PurchaseBasketRecyclerAdapter.PurchaseBasketItemHolder> {
        public static final String DEBUG_TAG = "PurchaseBasketRA";
//        private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        private List<PurchaseBasket> purchaseList;
        private Context context;

        public PurchaseBasketRecyclerAdapter(List<PurchaseBasket> boughtList, Context context) {
            this.purchaseList = boughtList;
            this.context = context;
        }

        // The adapter must have a ViewHolder class to "hold" one item to show.
        public class PurchaseBasketItemHolder extends RecyclerView.ViewHolder {

            int position = -1;
//            PurchaseBasket basket;
            RecyclerView listItems;
            PurchaseItemRecyclerAdapter adapter;
            TextView price;
            TextView rmName;
            TextView itemTime;
            CheckBox box;

            public PurchaseBasketItemHolder(View itemView) {
                super(itemView);

                listItems = itemView.findViewById(R.id.itemNames);
                price = itemView.findViewById(R.id.priceView);
                rmName = itemView.findViewById(R.id.roommateName);
                itemTime = itemView.findViewById(R.id.userTime);
                box = itemView.findViewById(R.id.checkBox);
            }
        }

        @NonNull
        @Override
        public PurchaseBasketRecyclerAdapter.PurchaseBasketItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_basket, parent, false);
            return new PurchaseBasketRecyclerAdapter.PurchaseBasketItemHolder(view);
        }

        // This method fills in the values of the Views to show a ShoppingItem
        @Override
        public void onBindViewHolder(PurchaseBasketRecyclerAdapter.PurchaseBasketItemHolder holder, @SuppressLint("RecyclerView") int position) {
            PurchaseBasket purchaseItem = purchaseList.get(position);

            Log.d(DEBUG_TAG, "Bind Basket: " + position);

            String key = purchaseItem.getKey();

            List<PurchaseItem> purchased = purchaseItem.getItemList();
            LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext());
            holder.listItems.setLayoutManager(layoutManager);
            holder.adapter = new PurchaseItemRecyclerAdapter(purchased, holder.itemView.getContext());
//            holder.adapter.setKey(purchaseList.get(holder.getAdapterPosition()).getKey());
//            holder.adapter.setPosition(holder.getAdapterPosition());
            holder.listItems.setAdapter(holder.adapter);

            holder.position = position;
            holder.price.setText(purchaseItem.getCost());
            holder.rmName.setText(purchaseItem.getRmName());
            holder.itemTime.setText(purchaseItem.getItemTime());

        }

        @Override
        public int getItemCount() {
            return purchaseList.size();
        }
    }
}