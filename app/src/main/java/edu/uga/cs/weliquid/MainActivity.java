package edu.uga.cs.weliquid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import edu.uga.cs.weliquid.dialog.LogoutDialogFragment;

/**
 * This is the screen users are taken to when they log in
 * Users can open the shopping list and purchased list screens from here
 */
public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "ManagementActivity";

    private TextView signedInTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_management);

        final ActionBar ab = getSupportActionBar();
        assert ab != null;

        Log.d( DEBUG_TAG, "ItemManagementActivity.onCreate()" );

        ImageView logo = findViewById(R.id.loggedLogo);
        Button shoppingListBtn = findViewById(R.id.button1);
        Button purchasedListBtn = findViewById(R.id.button2);
        Button basketBtn = findViewById(R.id.basket_btn);
        signedInTextView = findViewById(R.id.textView3);

        shoppingListBtn.setOnClickListener( new ShoppingListBtnClickListener() );
        purchasedListBtn.setOnClickListener( new PurchasedListBtnClickListener() );
        basketBtn.setOnClickListener( new BasketBtnClickListener() );

        // Setup a listener for a change in the sign in status (authentication status change)
        // when it is invoked, check if a user is signed in and update the UI text view string,
        // as needed.
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if( currentUser != null ) {
                    // User is signed in
                    Log.d(DEBUG_TAG, "onAuthStateChanged:signed_in:" + currentUser.getUid());
                    String userEmail = currentUser.getEmail();
                    signedInTextView.setText( "Signed in as: " + userEmail );
                } else {
                    // User is signed out
                    Log.d( DEBUG_TAG, "onAuthStateChanged:signed_out" );
                    signedInTextView.setText( "Signed in as: not signed in" );
                }
            }
        });

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // begin to track money spent by user, initializing entry if needed
        Query emailLogged = dbRef.child("userList")
                .orderByChild("name").equalTo(email);
        emailLogged.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    String userId = dbRef.child("userList").push().getKey();
                    UserEntry thisUser = new UserEntry(email, "0");
                    dbRef.child("userList").child(userId).setValue(thisUser);
                    Log.d(DEBUG_TAG, "added user entry");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(DEBUG_TAG, "couldn't check for user spending");
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_management_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            DialogFragment logoutFrag = new LogoutDialogFragment();
            logoutFrag.show( getSupportFragmentManager(), null);
        }
        return super.onOptionsItemSelected(item);
    }

    private class ShoppingListBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), ShoppingListActivity.class);
            view.getContext().startActivity( intent );
        }
    }

    private class PurchasedListBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), PurchasedListActivity.class);
            view.getContext().startActivity(intent);
        }
    }

    private class BasketBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), BasketActivity.class);
            view.getContext().startActivity(intent);
        }
    }
}