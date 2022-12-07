package edu.uga.cs.weliquid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class PurchaseItemRecyclerAdapter extends RecyclerView.Adapter<PurchaseItemRecyclerAdapter.PurchaseItemHolder> {
    public static final String DEBUG_TAG = "PurchaseItemRA";
    private List<PurchaseItem> purchaseItemsList;
    private Context context;

    public PurchaseItemRecyclerAdapter(List<PurchaseItem> boughtItemsList, Context context) {
        this.purchaseItemsList = boughtItemsList;
        this.context = context;
    }

    public void setKey(String key) {
    }

    public void setPosition(int adapterPosition) {
    }

    public class PurchaseItemHolder extends RecyclerView.ViewHolder {
        int position = -1;
        TextView itemName;
        Button deleteButton;

        public PurchaseItemHolder(View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.purchasedItemName);
            deleteButton = itemView.findViewById(R.id.deleteBasketBtn);
        }
    }

    @NonNull
    @Override
    public PurchaseItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_item, parent, false);
        return new PurchaseItemHolder(view);
    }

    @Override
    public void onBindViewHolder(PurchaseItemHolder holder, @SuppressLint("RecyclerView") int position ) {

        PurchaseItem purchaseItem = purchaseItemsList.get(position);
        if (purchaseItem != null) {
            String key = purchaseItem.getItemKey();
            String itemName = purchaseItem.getPurchaseItemName();

            holder.position = position;
            holder.itemName.setText(itemName);

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference fire = FirebaseDatabase.getInstance()
                            .getReference("purchaseItems")
                            .child(key + "/itemList/");
                    if (position < getItemCount()) {
                        fire.child(String.valueOf(position)).removeValue();
                        purchaseItemsList.remove(position);
                        notifyItemRemoved(position);
                        returnItem(purchaseItem.getPurchaseItemName());
                        if (purchaseItemsList.size() == 0) {
                            PurchasedListActivity.deleteBasket(key);
                        }
                        Log.d(DEBUG_TAG, "delete item");
                    } else {
                        fire.child("0").removeValue();
                        purchaseItemsList.remove(0);
                        notifyItemRemoved(0);
                        returnItem(purchaseItem.getPurchaseItemName());
                        Log.d(DEBUG_TAG, "removing last");
                        PurchasedListActivity.deleteBasket(key);
                    }
                }
            });
        }
    }

    private void returnItem(String name) {
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss z");
        String date = dateFormat.format( calendar.getTime() );

        ShoppingItem returnItem = new ShoppingItem(name, user, date);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("shoppingItems");

        myRef.push().setValue( returnItem )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d( DEBUG_TAG, "ShoppingItem item saved: " + returnItem );
                        // Show a quick confirmation

                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure( @NonNull Exception e ) {
                        Log.d( DEBUG_TAG, "ShoppingItem not retuned: " + returnItem );
                    }
                });
    }

    @Override
    public int getItemCount() {
        return purchaseItemsList != null? purchaseItemsList.size(): 0;
    }
}
