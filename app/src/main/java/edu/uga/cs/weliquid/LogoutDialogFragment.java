package edu.uga.cs.weliquid;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LogoutDialogFragment extends DialogFragment {

    public static LogoutDialogFragment newInstance() {
        LogoutDialogFragment dialog = new LogoutDialogFragment();
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.logout_dialog,
                getActivity().findViewById(R.id.logoutRoot));

        // create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        // Set its view (inflated above).
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "Logout Confirm" );
        // Provide the negative button listener
        builder.setNegativeButton( "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichBtn) {
                // close the dialog
                dialog.dismiss();
            }
        });

        // Provide the positive button listener
        builder.setPositiveButton( "YES", new LogoutListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class LogoutListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(getContext(), MainActivity.class);
            getContext().startActivity(intent);
        }
    }
}
