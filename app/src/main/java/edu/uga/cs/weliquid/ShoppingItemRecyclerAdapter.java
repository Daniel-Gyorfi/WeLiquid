package edu.uga.cs.weliquid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.weliquid.dialog.EditShoppingItemDialogFragment;

/**
 * This is an adapter class for the RecyclerView to show all shopping list items.
 */
public class ShoppingItemRecyclerAdapter extends RecyclerView.Adapter<ShoppingItemRecyclerAdapter.ShoppingItemHolder> {
    public static final String DEBUG_TAG = "ShopItemRecyclerAdapter";

//    private Activity owner;
    private int baseImg;
    private int specialImg;
    private FloatingActionButton button;
    private List<ShoppingItem> items;
    private ArrayList<String> selectedList;
    private Context context;
    public boolean isBaseButton = true;
    int numChecks = 0;
    boolean selectAll = false;
//    boolean addBasket = false;

    public ShoppingItemRecyclerAdapter( List<ShoppingItem> shoppingList, Context context,
                                        int basePic, int specialPic, FloatingActionButton fab
                                        ) {
        this.items = shoppingList;
        this.selectedList = new ArrayList<String>();
        this.context = context;
        baseImg = basePic;
        specialImg = specialPic;
        button = fab;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    public class ShoppingItemHolder extends RecyclerView.ViewHolder {

        int position = -1;
        TextView itemName;
        TextView rmName;
        TextView itemTime;
        CheckBox box;

        public ShoppingItemHolder(View itemView ) {
            super(itemView);

            itemName = itemView.findViewById( R.id.itemName );
            rmName = itemView.findViewById( R.id.roommateName );
            itemTime = itemView.findViewById( R.id.userTime );
            box = itemView.findViewById( R.id.checkBox );
        }
    }

    //creating the ShoppingItem layout
    @NonNull
    @Override
    public ShoppingItemHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.shopping_item, parent, false );
        return new ShoppingItemHolder( view );
    }

    // This method fills in the values of the Views to show a ShoppingItem
    // It will run when the recycler is notified of a change to its contents
    @Override
    public void onBindViewHolder(ShoppingItemHolder holder, @SuppressLint("RecyclerView") int position ) {
        ShoppingItem shoppingItem = items.get( position );

        Log.d( DEBUG_TAG, "Bind: " + position );

        String key = shoppingItem.getKey();
        String itemName = shoppingItem.getItemName();
        String userName = shoppingItem.getRmName();
        String userTime = shoppingItem.getItemTime();

        holder.position = position;
        holder.itemName.setText( shoppingItem.getItemName() );
        holder.rmName.setText( shoppingItem.getRmName() );
        holder.itemTime.setText( shoppingItem.getItemTime() );


        if (!selectAll) {
            if (selectedList.contains(key)) {
                // for when screen is rotated, reselecting items
                holder.box.setChecked(true);
            } else {
                // if unselect btn is clicked and basket btn is not clicked yet
                holder.box.setChecked(false);
                // this method won't break if the item is not in the selected list
                selectedList.remove(key);
            }
        } else {
            // if select all btn is clicked and basket btn is not clicked yet
            holder.box.setChecked(true);
            addToTemp(key);
        }

        // We can attach an OnClickListener to the itemView of the holder;
        // itemView is a public field in the Holder class.
        // It will be called when the user taps/clicks on the whole item, i.e., one of
        // the shopping list items shown.
        // This will indicate that the user wishes to edit (modify or delete) this item.
        // We create and show an EditShoppingItemDialogFragment.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d( TAG, "onBindViewHolder: getItemId: " + holder.getItemId() );
                //Log.d( TAG, "onBindViewHolder: getAdapterPosition: " + holder.getAdapterPosition() );
                EditShoppingItemDialogFragment editItemFragment =
                        EditShoppingItemDialogFragment.newInstance( holder.getAdapterPosition(), key, itemName, userName, userTime );
                editItemFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
            }
        });

        holder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.box.isChecked()) {
//                    basketList.add(key);
                    addToTemp(key);
                    numChecks++;
                    Log.d(DEBUG_TAG, "num of selected checkbox: " + numChecks);
                } else {
                    numChecks--;
                    // matches the Integer object, not the index in basket list
                    selectedList.remove(key);
                    Log.d(DEBUG_TAG, "num of selected checkbox: " + numChecks);
                }
                if (numChecks > 0) {
                    setSpecialButton();
                    ShoppingListActivity.setUnselectTitle();
                } else {
                    setBaseButton();
                    ShoppingListActivity.setSelectTitle();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public int getNumChecks() { return numChecks; }

    public void selectAll() {
        if (getItemCount() > 0) {
            selectAll = true;
            numChecks = getItemCount();
            setSpecialButton();
            ShoppingListActivity.setUnselectTitle();
            notifyDataSetChanged();
        }
    }

    public void unselectAll() {
        selectAll = false;
        numChecks = 0;
        setBaseButton();
        ShoppingListActivity.setSelectTitle();
        selectedList.clear();
        notifyDataSetChanged();
    }

    private void addToTemp(String key) {
        if (!ShopBasket.getInstance().containsKey(key))
        {
            selectedList.add(key);
        }
    }

    public ArrayList<String> getTemp() {
        return selectedList;
    }

    public void setTemp(ArrayList<String> selected) {
        selectedList = selected;
    }

    public void addBackItem(ShoppingItem item) {
        items.add(item);
    }

    private void setBaseButton () {
        button.setImageResource(baseImg);
        isBaseButton = true;
    }

    private void setSpecialButton () {
        button.setImageResource(specialImg);
        isBaseButton = false;
    }

    public void addToBasket() {
//        addBasket = true;
        selectAll = false;
        numChecks = 0;
        ArrayList<ShoppingItem> temp = new ArrayList<>(items);
        for (ShoppingItem item : temp) {
            if (selectedList.contains(item.getKey())) {
                ShopBasket.getInstance().add(item);
                items.remove(item);
            }
        }
        setBaseButton();
    }
}
