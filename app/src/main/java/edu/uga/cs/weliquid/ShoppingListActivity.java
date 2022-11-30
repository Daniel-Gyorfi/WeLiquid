package edu.uga.cs.weliquid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an activity class for displaying all items in the shopping list.
 * The current items are listed as a RecyclerView.
 */
public class ShoppingListActivity
        extends AppCompatActivity implements AddShoppingItemDialogFragment.AddShoppingItemDialogListener,
EditShoppingItemDialogFragment.EditItemDialogListener {

    public static final String DEBUG_TAG = "ShoppingListActivity";

    private RecyclerView recyclerView;
    private ShoppingItemRecyclerAdapter recyclerAdapter;
    private List<ShoppingItem> shoppingItemsList;
    private FirebaseDatabase database;
    public static FloatingActionButton floatingButton;
    public static boolean isAddButton = true;
    private static Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d( DEBUG_TAG, "onCreate()" );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        setTitle("Shopping List");

        final ActionBar ab = getSupportActionBar();
        assert ab != null;

        recyclerView = findViewById( R.id.recyclerView );

        floatingButton = findViewById(R.id.floatingActionButton);

        floatingButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAddButton == true) {
                    DialogFragment newFragment = new AddShoppingItemDialogFragment();
                    newFragment.show( getSupportFragmentManager(), null);
                } else {
                    Log.d(DEBUG_TAG, "go to basket activity");
                }
            }
        });


        // initialize the shopping list
        shoppingItemsList = new ArrayList<ShoppingItem>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with shopping list items is empty at first; it will be updated later
        recyclerAdapter = new ShoppingItemRecyclerAdapter( shoppingItemsList, ShoppingListActivity.this );
        recyclerView.setAdapter( recyclerAdapter );

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("shoppingItems");

        // Set up a listener (event handler) to receive a value for the database reference.
        // This type of listener is called by Firebase once by immediately executing its onDataChange method
        // and then each time the value at Firebase changes.
        //
        // This listener will be invoked asynchronously, hence no need for an AsyncTask class.
        myRef.addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our shopping list.
                shoppingItemsList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    ShoppingItem shopItem = postSnapshot.getValue(ShoppingItem.class);
                    shopItem.setKey( postSnapshot.getKey() );
                    shoppingItemsList.add( shopItem );
                    Log.d( DEBUG_TAG, "ValueEventListener: added: " + shopItem );
                    Log.d( DEBUG_TAG, "ValueEventListener: key: " + postSnapshot.getKey() );
                }

                Log.d( DEBUG_TAG, "ValueEventListener: notifying recyclerAdapter" );
                // notifying the recycler that the set of data has changed
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {
                System.out.println( "ValueEventListener: reading failed: " + databaseError.getMessage() );
            }
        } );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shopping_list_menu, menu);
        this.optionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            DialogFragment logoutFrag = new LogoutDialogFragment();
            logoutFrag.show( getSupportFragmentManager(), null);
        } else if (id == R.id.select) {
            if (item.getTitle().equals("SELECT ALL")) {
                recyclerAdapter.selectAll();
                setUnselectTitle();
            } else if (item.getTitle().equals("UNSELECT ALL")) {
                recyclerAdapter.unselectAll();
                setSelectTitle();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // this is our own callback for a AddShoppingItemDialogFragment which adds a new shopping list item.
    public void addShoppingItem(ShoppingItem shoppingItem) {
        // add the new shopping list item
        // Add a new element (ShoppingItem) to the list of items in Firebase.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("shoppingItems");

        // First, a call to push() appends a new node to the existing list (one is created
        // if this is done for the first time).  Then, we set the value in the newly created
        // list node to store the new shopping list item.
        // This listener will be invoked asynchronously, as no need for an AsyncTask.
        myRef.push().setValue( shoppingItem )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Reposition the RecyclerView to show the ShoppingItem most recently added (as the last item on the list).
                        // Use of the post method is needed to wait until the RecyclerView is rendered, and only then
                        // reposition the item into view (show the last item on the list).
                        // the post method adds the argument (Runnable) to the message queue to be executed
                        // by Android on the main UI thread.  It will be done *after* the setAdapter call
                        // updates the list items, so the repositioning to the last item will take place
                        // on the complete list of items.
                        recyclerView.post( new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition( shoppingItemsList.size()-1 );
                            }
                        } );

                        Log.d( DEBUG_TAG, "Shopping list item saved: " + shoppingItem );
                        // Show a quick confirmation
                        Toast.makeText(getApplicationContext(), "Shopping list card created for " + shoppingItem.getItemName(),
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure( @NonNull Exception e ) {
                        Toast.makeText( getApplicationContext(), "Failed to create a shopping list card for " + shoppingItem.getItemName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // This is our own callback for a DialogFragment which edits an existing ShoppingItem.
    // The edit may be an update or a deletion of this ShoppingItem.
    // It is called from the EditShoppingItemDialogFragment.
    public void updateShoppingItem( int position, ShoppingItem item, int action ) {
        if( action == EditShoppingItemDialogFragment.SAVE ) {
            Log.d( DEBUG_TAG, "Updating shopping list item at: " + position + "(" + item.getItemName() + ")" );

            // Update the recycler view to show the changes in the updated shopping list item in that view
            recyclerAdapter.notifyItemChanged( position );

            // Update this shopping list item in Firebase
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "shoppingItems" )
                    .child( item.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain shopping list items.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().setValue( item ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "updated shopping list item at: " + position + "(" + item.getItemName() + ")" );
                            Toast.makeText(getApplicationContext(), "Shopping list card updated for " + item.getItemName(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to update shopping list item at: " + position + "(" + item.getItemName() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to update " + item.getItemName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if( action == EditShoppingItemDialogFragment.DELETE ) {
            Log.d( DEBUG_TAG, "Deleting shopping list item at: " + position + "(" + item.getItemName() + ")" );

            // remove the deleted item from the list (internal list in the App)
            shoppingItemsList.remove( position );

            // Update the recycler view to remove the deleted shopping list item from that view
            recyclerAdapter.notifyItemRemoved( position );

            // Delete this shopping list item in Firebase.
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "shoppingItems" )
                    .child( item.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "deleted shopping list item at: " + position + "(" + item.getItemName() + ")" );
                            Toast.makeText(getApplicationContext(), "Shopping list card deleted for " + item.getItemName(),
                                    Toast.LENGTH_SHORT).show();                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to delete shopping list item at: " + position + "(" + item.getItemName() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to delete " + item.getItemName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // changes the icon and function of the circle button to adding an item in the shopping list.
    // It is a static method so that it is accessible to other classes.
    public static void setAddButton() {
        floatingButton.setImageResource(R.drawable.ic_baseline_add_24);
        isAddButton = true;
    }

    // changes the icon and function of the circle button to moving the items in the basket.
    // It is a static method so that it is accessible to other classes.
    public static void setBasketButton() {
        floatingButton.setImageResource(R.drawable.ic_baseline_shopping_basket_24);
        isAddButton = false;
    }

    public static void setUnselectTitle() {
        MenuItem item = optionsMenu.findItem(R.id.select);
        item.setTitle("UNSELECT ALL");
    }

    public static void setSelectTitle() {
        MenuItem item = optionsMenu.findItem(R.id.select);
        item.setTitle("SELECT ALL");
    }
}