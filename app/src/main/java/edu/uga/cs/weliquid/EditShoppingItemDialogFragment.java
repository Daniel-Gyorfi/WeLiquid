package edu.uga.cs.weliquid;

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


// This is a DialogFragment to handle edits to a ShoppingItem.
// The edits are: updates and deletions of existing ShoppingItems.
public class EditShoppingItemDialogFragment extends DialogFragment {
    // indicate the type of an edit
    public static final int SAVE = 1;   // update an existing shopping list item
    public static final int DELETE = 2; // delete an existing shopping list item

    private EditText itemNameView;

    int position;     // the position of the edited ShoppingItem on the list of items
    String key;
    String itemName;
    String rmName;
    String itemTime;

    // A callback listener interface to finish up the editing of a ShoppingItem.
    // ShoppingListActivity implements this listener interface, as it will
    // need to update the list of ShoppingItems and also update the RecyclerAdapter to reflect the
    // changes.
    public interface EditItemDialogListener {
        void updateShoppingItem(int position, ShoppingItem shoppingItem, int action);
    }

    public static EditShoppingItemDialogFragment newInstance(int position, String key, String itemName, String rmName, String itemTime) {
        EditShoppingItemDialogFragment dialog = new EditShoppingItemDialogFragment();

        // Supply item values as an argument.
        Bundle args = new Bundle();
        args.putString( "key", key );
        args.putInt( "position", position );
        args.putString("itemName", itemName);
        args.putString("rmName", rmName);
        args.putString("itemTime", itemTime);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {

        key = getArguments().getString( "key" );
        position = getArguments().getInt( "position" );
        itemName = getArguments().getString( "itemName" );
        rmName = getArguments().getString( "rmName" );
        itemTime = getArguments().getString( "itemTime" );

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate( R.layout.add_shopping_item_dialog, getActivity().findViewById( R.id.root ) );

        itemNameView = layout.findViewById( R.id.editText1 );

        // Pre-fill the edit texts with the current values for this shopping list item.
        // The user will be able to modify them.
        itemNameView.setText( itemName );

        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity(), R.style.AlertDialogStyle );
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "Edit Item" );

        // The Cancel button handler
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });

        // The Save button handler
        builder.setPositiveButton( "SAVE", new SaveButtonClickListener() );

        // The Delete button handler
        builder.setNeutralButton( "DELETE", new DeleteButtonClickListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String itemName = itemNameView.getText().toString();
            String userName = getArguments().getString( "rmName" );
            String date = getArguments().getString( "itemTime" );

            ShoppingItem saveItem = new ShoppingItem( itemName, userName, date );

            saveItem.setKey( key );

            // get the Activity's listener to add the new shopping list item
            EditItemDialogListener listener = (EditShoppingItemDialogFragment.EditItemDialogListener) getActivity();
            // add the new shopping list item
            listener.updateShoppingItem( position, saveItem, SAVE );

            // close the dialog
            dismiss();
        }
    }

    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick( DialogInterface dialog, int which ) {

            ShoppingItem deleteItem = new ShoppingItem( itemName, rmName, itemTime );
            deleteItem.setKey( key );

            // get the Activity's listener to add the new shopping list item
            EditShoppingItemDialogFragment.EditItemDialogListener listener = (EditShoppingItemDialogFragment.EditItemDialogListener) getActivity();            // add the new shopping list item
            listener.updateShoppingItem( position, deleteItem, DELETE );
            // close the dialog
            dismiss();
        }
    }
}
