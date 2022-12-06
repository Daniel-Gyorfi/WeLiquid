package edu.uga.cs.weliquid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        Log.d(DEBUG_TAG, "Bind: " + position);

        String key = purchaseItem.getItemKey();
        String itemName = purchaseItem.getPurchaseItemName();

        holder.position = position;
        holder.itemName.setText( itemName );
    }

    @Override
    public int getItemCount() {
        return purchaseItemsList.size();
    }
}
