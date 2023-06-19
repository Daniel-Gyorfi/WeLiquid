package edu.uga.cs.weliquid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;

import edu.uga.cs.weliquid.LoginActivity;
import edu.uga.cs.weliquid.R;

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

            Intent intent = new Intent(getContext(), LoginActivity.class);
            getContext().startActivity(intent);
        }
    }
}
