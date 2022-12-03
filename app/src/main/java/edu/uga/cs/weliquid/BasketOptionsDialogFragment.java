package edu.uga.cs.weliquid;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * BasketActivity options dialog when selecting items
 */
public class BasketOptionsDialogFragment extends DialogFragment {
    private static final String DEBUG_TAG = "OptionsDialogFragment";
    private Button purchaseBtn;
    private Button removeBtn;

    public static BasketOptionsDialogFragment newInstance() {
        BasketOptionsDialogFragment dialog = new BasketOptionsDialogFragment();
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.options_dialog,
                getActivity().findViewById(R.id.optionsRoot));

        // get the button objects in the AlertDialog
        purchaseBtn = layout.findViewById(R.id.pBtn);
        removeBtn = layout.findViewById(R.id.rBtn);

        // create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        // Set its view (inflated above).
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "Options" );

        purchaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(DEBUG_TAG, "purchase button clicked");

                ArrayList<String> items = ShopBasket.getInstance().getList();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String rmName = user.getEmail();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss z");
                String date = dateFormat.format(calendar.getTime());

                PurchaseBasketItem basket = new PurchaseBasketItem(items, "$three-fiddy", rmName, date);

                DatabaseReference fire = FirebaseDatabase.getInstance()
                        .getReference("purchaseItems");

                fire.push().setValue(basket)
                        .addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d( DEBUG_TAG, "Purchase list item saved: " + basket );
                                // Show a quick confirmation
                                Toast.makeText(getContext(), "Basket added to purchase list",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure( @NonNull Exception e ) {
                                Toast.makeText(getContext(), "Failed to add basket to purchase list",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                dismiss();
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(DEBUG_TAG, "remove button clicked");
                BasketActivity.itemsRemoved();
                dismiss();
            }
        });

        // Create the AlertDialog and show it
        return builder.create();
    }
}
