package edu.uga.cs.weliquid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import edu.uga.cs.weliquid.R.id;

/**
 * The Basket Screen is shown here,
 */
public class BasketActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BasketRecyclerAdapter recyclerAdapter;
//    private List<ShoppingItem> shoppingItemsList;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        setTitle("Basket");

        final ActionBar ab = getSupportActionBar();
        assert ab != null;

        recyclerView = findViewById( id.recycleBasket );

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with shopping list items is empty at first; it will be updated later
        recyclerAdapter = new BasketRecyclerAdapter( ShopBasket.getInstance(), BasketActivity.this );
        recyclerView.setAdapter( recyclerAdapter );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.basket_menu, menu);

        // if the basket is empty, then do not show select/unselect all button
        MenuItem item = menu.findItem(id.selectBtn);
        if (recyclerAdapter.getItemCount() == 0) {
            item.setVisible(false);
            this.invalidateOptionsMenu();
        }
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    private class BasketRecyclerAdapter extends RecyclerView.Adapter<BasketRecyclerAdapter.BasketItemHolder> {
        public static final String DEBUG_TAG = "ShopItemRecyclerAdapter";

        private ShopBasket basket;
        private Context context;
        int numChecks = 0;

        public BasketRecyclerAdapter( ShopBasket shopBasket, Context context ) {
            this.basket = shopBasket;
            this.context = context;
        }

        class BasketItemHolder extends RecyclerView.ViewHolder {
            TextView itemName;
            TextView rmName;
            TextView itemTime;
            CheckBox checkHide;

            public BasketItemHolder(View itemView) {
                super(itemView);

                itemName = itemView.findViewById( R.id.itemName );
                rmName = itemView.findViewById( R.id.roommateName );
                itemTime = itemView.findViewById( R.id.userTime );
                checkHide = itemView.findViewById( id.checkBox );
            }
        }

        @NonNull
        @Override
        public BasketRecyclerAdapter.BasketItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.shopping_item, parent, false );
            return new BasketRecyclerAdapter.BasketItemHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull BasketRecyclerAdapter.BasketItemHolder holder, int position) {
            ShoppingItem item = basket.items.get( position );

            Log.d( DEBUG_TAG, "onBindViewHolder: " + item );

            holder.itemName.setText( item.getItemName());
            holder.rmName.setText( item.getRmName() );
            holder.itemTime.setText( item.getItemTime() );
            // no need for a checkbox in basket screen yet
            holder.checkHide.setVisibility( View.GONE );
        }

        @Override
        public int getItemCount() {
            return basket.items.size();
        }
    }
}