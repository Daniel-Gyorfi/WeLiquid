package edu.uga.cs.weliquid;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all shopping list items.
 */
public class ShoppingItemRecyclerAdapter extends RecyclerView.Adapter<ShoppingItemRecyclerAdapter.ShoppingItemHolder> {
    public static final String DEBUG_TAG = "ShopItemRecyclerAdapter";

    private List<ShoppingItem> shoppingList;
    private List<Integer> basketList;

    private Context context;
    int numChecks = 0;
    boolean selectAll = false;
    boolean addBasket = false;

    public ShoppingItemRecyclerAdapter( List<ShoppingItem> shoppingList, Context context ) {
        this.shoppingList = shoppingList;
        this.basketList = new ArrayList<Integer>();
        this.context = context;
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

    @NonNull
    @Override
    public ShoppingItemHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.shopping_item, parent, false );
        return new ShoppingItemHolder( view );
    }

    // This method fills in the values of the Views to show a ShoppingItem
    @Override
    public void onBindViewHolder(ShoppingItemHolder holder, @SuppressLint("RecyclerView") int position ) {
        ShoppingItem shoppingItem = shoppingList.get( position );

        Log.d( DEBUG_TAG, "Bind: " + position );

        String key = shoppingItem.getKey();
        String itemName = shoppingItem.getItemName();
        String userName = shoppingItem.getRmName();
        String userTime = shoppingItem.getItemTime();

        holder.position = position;
        holder.itemName.setText( shoppingItem.getItemName() );
        holder.rmName.setText( shoppingItem.getRmName() );
        holder.itemTime.setText( shoppingItem.getItemTime() );

        if (!addBasket) {
            if (!selectAll) {
                // if unselect btn is clicked and basket btn is not clicked yet
                holder.box.setChecked(false);
                basketList.remove(Integer.valueOf(holder.position));
            } else {
                // if select all btn is clicked and basket btn is not clicked yet
                holder.box.setChecked(true);
                basketList.add(holder.position);
            }
        } else {
            if (!selectAll) {
                // after basket btn is clicked, all checkboxes in shopping list are unchecked
                holder.box.setChecked(false);
            }
        }

        // this is what happens once the basket btn is clicked
        if (addBasket && basketList.contains(position)) {
            Log.d(DEBUG_TAG, "contains: " + position);
            ShopBasket.getInstance().add( shoppingItem );
            basketList.remove(Integer.valueOf(holder.position));
            if (basketList.isEmpty()) addBasket = false;
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
                    basketList.add(holder.position);
                    numChecks++;
                    Log.d(DEBUG_TAG, "num of selected checkbox: " + numChecks);
                } else {
                    numChecks--;
                    basketList.remove(Integer.valueOf(holder.position));
                    Log.d(DEBUG_TAG, "num of selected checkbox: " + numChecks);
                }
                if (numChecks > 0) {
                    ShoppingListActivity.setBasketButton();
                    ShoppingListActivity.setUnselectTitle();
                } else {
                    ShoppingListActivity.setAddButton();
                    ShoppingListActivity.setSelectTitle();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public int getNumChecks() { return numChecks; }

    public void selectAll() {
        selectAll = true;
        numChecks = getItemCount();
        ShoppingListActivity.setBasketButton();
        notifyDataSetChanged();
    }

    public void unselectAll() {
        selectAll = false;
        numChecks = 0;
        ShoppingListActivity.setAddButton();
        notifyDataSetChanged();
    }

    public void addToBasket() {
        addBasket = true;
        selectAll = false;
        numChecks = 0;
        notifyDataSetChanged();
    }
}
