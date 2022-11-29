package edu.uga.cs.weliquid;

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

import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all shopping list items.
 */
public class ShoppingItemRecyclerAdapter extends RecyclerView.Adapter<ShoppingItemRecyclerAdapter.ShoppingItemHolder> {
    public static final String DEBUG_TAG = "ShopItemRecyclerAdapter";

    private List<ShoppingItem> shoppingList;
    private Context context;
    int numChecks = 0;

    public ShoppingItemRecyclerAdapter( List<ShoppingItem> shoppingList, Context context ) {
        this.shoppingList = shoppingList;
        this.context = context;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ShoppingItemHolder extends RecyclerView.ViewHolder {

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
    public void onBindViewHolder( ShoppingItemHolder holder, int position ) {
        ShoppingItem shoppingItem = shoppingList.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + shoppingItem );

        String key = shoppingItem.getKey();
        String itemName = shoppingItem.getItemName();
        String userName = shoppingItem.getRmName();
        String userTime = shoppingItem.getItemTime();

        holder.itemName.setText( shoppingItem.getItemName());
        holder.rmName.setText( shoppingItem.getRmName() );
        holder.itemTime.setText( shoppingItem.getItemTime() );

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
                    numChecks++;
                    Log.d(DEBUG_TAG, "num of selected checkbox: " + numChecks);
                } else {
                    numChecks--;
                    Log.d(DEBUG_TAG, "num of selected checkbox: " + numChecks);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }
}
