package edu.uga.cs.weliquid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import edu.uga.cs.weliquid.R;

public class HelpDialogFragment extends DialogFragment {

    public static HelpDialogFragment newInstance() {
        HelpDialogFragment dialog = new HelpDialogFragment();
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.help_dialog,
                getActivity().findViewById(R.id.helpRoot));

        // create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        // Set its view (inflated above).
        builder.setView(layout);
        // Set the title of the AlertDialog
        builder.setTitle( "Help" );

        // Create the AlertDialog and show it
        return builder.create();
    }
}
