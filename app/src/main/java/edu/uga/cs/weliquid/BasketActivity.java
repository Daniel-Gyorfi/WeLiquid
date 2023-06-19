package edu.uga.cs.weliquid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.uga.cs.weliquid.R.id;
import edu.uga.cs.weliquid.dialog.EnterPriceDialogFragment;
import edu.uga.cs.weliquid.dialog.HelpDialogFragment;
import edu.uga.cs.weliquid.dialog.LogoutDialogFragment;

/**
 * The Basket Screen is shown here,
 */
public class BasketActivity extends AppCompatActivity
    implements EnterPriceDialogFragment.EnterPriceDialogListener {

    public static final String DEBUG_TAG = "BasketActivity";
    private RecyclerView recyclerView;
    private static BasketRecyclerAdapter recyclerAdapter;
//    private List<ShoppingItem> shoppingItemsList;
    private FirebaseDatabase database;
    public static FloatingActionButton actionBtn;
    public static int buttonChoice = 1;
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

        if (recyclerAdapter.getItemCount() > 0) {
            setPurchaseButton();
        } else {
            setHelpButton();
        }

        actionBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonChoice == 1) { // Show Help Dialog
                    DialogFragment helpFragment = new HelpDialogFragment();
                    helpFragment.show(getSupportFragmentManager(), null);
                } else if (buttonChoice == 2) { // "Purchase" basket
                    Log.d(DEBUG_TAG, "purchase button clicked");

                    DialogFragment newFragment = new EnterPriceDialogFragment();
                    newFragment.show( getSupportFragmentManager(), null);


                } else if (buttonChoice == 3){
                    itemsRemoved();
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
        buttonChoice = 1;
    }

    public void setPurchaseButton() {
        actionBtn.setImageResource(R.drawable.ic_baseline_attach_money_24);
        buttonChoice = 2;
    }

    public void setRemoveButton() {
        actionBtn.setImageResource(R.drawable.ic_baseline_cancel_24);
        buttonChoice = 3;
    }

    public void unselectTitle() {
        MenuItem item = basketMenu.findItem(R.id.selectBtn);
        item.setTitle("UNSELECT");
    }

    public void selectTitle() {
        MenuItem item = basketMenu.findItem(R.id.selectBtn);
        item.setTitle("SELECT ALL");
    }

    public static void itemsRemoved() {
        Log.d(DEBUG_TAG, "itemsRemoved is called");
        recyclerAdapter.removeItems();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void enterItemPrice(BigDecimal value) {
        List<PurchaseItem> items = ShopBasket.getInstance().getList();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String rmName = user.getEmail();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss z");
        String date = dateFormat.format(calendar.getTime());

        String price = "$" + value;

        PurchaseBasket basket = new PurchaseBasket(items, price, rmName, date);

        DatabaseReference fire = FirebaseDatabase.getInstance()
                .getReference("purchaseItems");

        fire.push().setValue(basket)
            .addOnSuccessListener( new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d( DEBUG_TAG, "Purchase list item saved: " + basket );
                    ShopBasket.getInstance().clear();
                    // Show a quick confirmation
                    Toast.makeText(getApplicationContext(), "Basket added to purchase list",
                            Toast.LENGTH_SHORT).show();
                    Intent viewPurchase = new Intent(getApplicationContext(), PurchasedListActivity.class);
                    viewPurchase.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(viewPurchase);
                }
            })
            .addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure( @NonNull Exception e ) {
                    Toast.makeText(getApplicationContext(), "Failed to add basket to purchase list",
                            Toast.LENGTH_SHORT).show();
                }
            });

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // begin to track money spent by user, initializing entry if needed
        Query emailLogged = dbRef.child("userList")
                .orderByChild("name").equalTo(email);
        emailLogged.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserEntry thisUser = null;
                String userId = "";
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    thisUser = data.getValue(UserEntry.class);
                    userId = data.getKey();
                    break;
                }
                thisUser.increment(value);
                Log.d(DEBUG_TAG, "User: " + thisUser);
                dbRef.child("userList").child(userId).setValue(thisUser);
                Log.d(DEBUG_TAG, "updated user spending");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(DEBUG_TAG, "couldn't get user info");
            }
        });

        ShopBasket.getInstance().removeFromShoppingList(getApplicationContext());
    }

    private class BasketRecyclerAdapter extends RecyclerView.Adapter<BasketRecyclerAdapter.BasketItemHolder> {
        public static final String DEBUG_TAG = "ShopItemRecyclerAdapter";

        private ShopBasket basket;
        private List<String> basketKeyList;
        private Context context;
        int numChecks = 0;
        boolean selectAll = false;

        public BasketRecyclerAdapter( ShopBasket shopBasket, Context context ) {
            this.basket = shopBasket;
            this.basketKeyList = new ArrayList<String>();
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
            String basketKey = item.getKey();

            Log.d( DEBUG_TAG, "onBindViewHolder: " + item );

            holder.itemName.setText( item.getItemName());
            holder.rmName.setText( item.getRmName() );
            holder.itemTime.setText( item.getItemTime() );

            if (selectAll) {
                holder.checkBox.setChecked(true);
                numChecks++;
                basketKeyList.add(basketKey);
            }

            if (numChecks == 0) {
                holder.checkBox.setChecked(false);
            }

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.checkBox.isChecked()) {
                        numChecks++;
                        basketKeyList.add(basketKey);
                        Log.d(DEBUG_TAG, "num of selected checkbox: " + numChecks);
                    } else {
                        numChecks--;
                        basketKeyList.remove(basketKey);
                        Log.d(DEBUG_TAG, "num of selected checkbox: " + numChecks);
                    }
                    if (numChecks > 0) {
                        setRemoveButton();
                        unselectTitle();
                    } else {
                        if (getItemCount() == 0) {
                            setHelpButton();
                        } else {
                            setPurchaseButton();
                        }
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
            selectAll = true;
            numChecks = getItemCount();
            setRemoveButton();
            notifyDataSetChanged();
        }

        public void setUnselectAll() {
            selectAll = false;
            numChecks = 0;
            setPurchaseButton();
            notifyDataSetChanged();
        }

        public void removeItems() {
            Log.d(DEBUG_TAG, "removeItems() is called");

            for (String key : basketKeyList) {
                if (basket.containsKey(key)) {
                    ShoppingListActivity.returnItem(basket.getFromKey(key));
                    basket.removeKey(key);
                    numChecks--;
                }
            }
            basketKeyList.clear();
            selectTitle();
            setPurchaseButton();
            notifyDataSetChanged();
        }
    }
}