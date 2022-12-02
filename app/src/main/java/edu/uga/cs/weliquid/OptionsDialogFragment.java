package edu.uga.cs.weliquid;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.security.cert.PKIXRevocationChecker;

public class OptionsDialogFragment extends DialogFragment {
    private static final String DEBUG_TAG = "OptionsDialogFragment";
    private Button purchaseBtn;
    private Button removeBtn;

    public static OptionsDialogFragment newInstance() {
        OptionsDialogFragment dialog = new OptionsDialogFragment();
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
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(DEBUG_TAG, "remove button clicked");
            }
        });

        // Create the AlertDialog and show it
        return builder.create();
    }
}
