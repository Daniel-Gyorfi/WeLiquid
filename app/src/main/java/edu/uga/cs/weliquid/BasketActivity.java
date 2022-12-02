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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    public static FloatingActionButton actionBtn;
    public static boolean isHelpButton = true;
    private static Menu basketMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        setTitle("Basket");

        final ActionBar ab = getSupportActionBar();
        assert ab != null;

        recyclerView = findViewById( id.recycleBasket );

        actionBtn = findViewById(id.circleButton);

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with shopping list items is empty at first; it will be updated later
        recyclerAdapter = new BasketRecyclerAdapter( ShopBasket.getInstance(), BasketActivity.this );
        recyclerView.setAdapter( recyclerAdapter );

        actionBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHelpButton) {
                    DialogFragment helpFragment = new HelpDialogFragment();
                    helpFragment.show(getSupportFragmentManager(), null);
                } else {
                    DialogFragment optionsFragment = new OptionsDialogFragment();
                    optionsFragment.show(getSupportFragmentManager(), null);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.basket_menu, menu);
        this.basketMenu = menu;

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
        } else if (id == R.id.selectBtn) {
            if (item.getTitle().equals("SELECT ALL")) {
                recyclerAdapter.setSelectAll();
                unselectTitle();
            } else if (item.getTitle().equals("UNSELECT")) {
                recyclerAdapter.setUnselectAll();
                selectTitle();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void setHelpButton() {
        actionBtn.setImageResource(R.drawable.ic_baseline_question_mark_24);
        isHelpButton = true;
    }

    public void setOptionsButton() {
        actionBtn.setImageResource(R.drawable.ic_baseline_menu_24);
        isHelpButton = false;
    }

    public static void unselectTitle() {
        MenuItem item = basketMenu.findItem(R.id.selectBtn);
        item.setTitle("UNSELECT");
    }

    public static void selectTitle() {
        MenuItem item = basketMenu.findItem(R.id.selectBtn);
        item.setTitle("SELECT ALL");
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
        boolean isSelectAll = false;
        boolean addPurchased = false;

        public BasketRecyclerAdapter( ShopBasket shopBasket, Context context ) {
            this.basket = shopBasket;
            this.context = context;
        }

        class BasketItemHolder extends RecyclerView.ViewHolder {
            TextView itemName;
            TextView rmName;
            TextView itemTime;
            CheckBox checkBox;

            public BasketItemHolder(View itemView) {
                super(itemView);

                itemName = itemView.findViewById( R.id.itemName );
                rmName = itemView.findViewById( R.id.roommateName );
                itemTime = itemView.findViewById( R.id.userTime );
                checkBox = itemView.findViewById( id.checkBox );
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

            if (!isSelectAll) {
                holder.checkBox.setChecked(false);
            } else {
                holder.checkBox.setChecked(true);
            }

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.checkBox.isChecked()) {
                        numChecks++;
                        Log.d(DEBUG_TAG, "num of selected checkbox: " + numChecks);
                    } else {
                        numChecks--;
                        Log.d(DEBUG_TAG, "num of selected checkbox: " + numChecks);
                    }
                    if (numChecks > 0) {
                        setOptionsButton();
                        unselectTitle();
                    } else {
                        setHelpButton();
                        selectTitle();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return basket.items.size();
        }

        public void setSelectAll() {
            isSelectAll = true;
            numChecks = getItemCount();
            setOptionsButton();
            notifyDataSetChanged();
        }

        public void setUnselectAll() {
            isSelectAll = false;
            numChecks = 0;
            setHelpButton();
            notifyDataSetChanged();
        }
    }
}