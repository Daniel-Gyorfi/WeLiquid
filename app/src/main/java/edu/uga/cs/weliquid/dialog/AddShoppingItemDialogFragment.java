package edu.uga.cs.weliquid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.uga.cs.weliquid.R;
import edu.uga.cs.weliquid.item.ShoppingItem;

/**
 * A DialogFragment class to handle item additions from the shopping list activity.
 * It uses a DialogFragment to allow the input of a new shopping list item.
 */
public class AddShoppingItemDialogFragment extends DialogFragment {
    private EditText itemNameView;

    // This interface will be used to obtain the new shopping list item from an AlertDialog.
    // A class implementing this interface will handle the new shopping list item, i.e. store it
    // in Firebase and add it to the RecyclerAdapter.
    public interface AddShoppingItemDialogListener {
        void addShoppingItem(ShoppingItem shoppingItem);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the AlertDialog view
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE) ;
        final View layout = inflater.inflate( R.layout.add_shopping_item_dialog,
                getActivity().findViewById( R.id.root ) );

        // get the view objects in the AlertDialog
        itemNameView = layout.findViewById( R.id.editText1 );

        // create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity(), R.style.AlertDialogStyle );
        // Set its view (inflated above).
        builder.setView( layout );

        // Set the title of the AlertDialog
        builder.setTitle( "New Item" );
        // Provide the negative button listener
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int whichButton)  {
                // close the dialog
                dialog.dismiss();
            }
        });
        // Provide the positive button listener
        builder.setPositiveButton( android.R.string.ok, new AddShoppingItemListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class AddShoppingItemListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick( DialogInterface dialog, int which ) {
            // get the new shopping list item data from the user
            String itemName = itemNameView.getText().toString();
            String rmName;

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            rmName = user.getEmail();

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss z");
            String date = dateFormat.format( calendar.getTime() );

            //Date currentTime = Calendar.getInstance().getTime();
            //            String time = currentTime.toString();

            // create a new ShoppingItem object
            ShoppingItem newItem = new ShoppingItem( itemName, rmName, date );

            // get the Activity's listener to add the new shopping list item
            AddShoppingItemDialogListener listener = (AddShoppingItemDialogListener) getActivity();

            // add the new shopping list item
            listener.addShoppingItem( newItem );

            // close the dialog
            dismiss();
        }
    }
}
