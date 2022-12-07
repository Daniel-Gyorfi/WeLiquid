package edu.uga.cs.weliquid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SettleCostActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "SettleCostActivity";
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private FirebaseDatabase database;
    private List<UserEntry> rmList;
    double totalCostNum = 0.0;
    double avgCostNum = 0.0;
    String results = "";
    int userCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_cost);
        getUsers();

        final ActionBar ab = getSupportActionBar();
        assert ab != null;

        Log.d( DEBUG_TAG, "SettleCostActivity.onCreate()" );
        TextView totalCost = findViewById(R.id.totalCost);
        TextView avgCost = findViewById(R.id.avgCost);
        TextView roommateCost = findViewById(R.id.roommateCost);
        Button homeBtn = findViewById(R.id.homePage);

        rmList = new ArrayList<UserEntry>();
//        Intent i = getIntent();
//        rmList = (List<UserEntry>) i.getSerializableExtra("roommatesList");

        database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("userList");
        userRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                rmList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserEntry roommate = dataSnapshot.getValue(UserEntry.class);
                    rmList.add(roommate);
                }

                for (UserEntry user: rmList) {
                    totalCostNum += Double.parseDouble(user.getSpend());
                    results += user.getName() + ": " + user.getSpend() + "\n";
                }
                totalCostNum = (int) (totalCostNum * 100);
                totalCostNum = totalCostNum / 100;

                avgCostNum = totalCostNum / userCount;
                avgCostNum = (int) (avgCostNum * 100);
                avgCostNum = avgCostNum / 100;

                totalCost.setText(String.valueOf(totalCostNum));
                avgCost.setText(String.valueOf(avgCostNum));
                roommateCost.setText(results);
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {
                System.out.println( "ValueEventListener: reading failed: " + error.getMessage() );
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference itemsRef = db.getReference("purchaseItems");
                itemsRef.removeValue();

                Toast.makeText(getApplicationContext(), "Deleted items from purchased list",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), ItemManagementActivity.class);
                view.getContext().startActivity( intent );
            }
        });
    }

    private void getUsers() {
        FirebaseDatabase.getInstance().getReference().child("userList").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            count++;
                        }
                        userCount = count;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println( "ValueEventListener: reading failed: " + error.getMessage() );
                    }
                });
    }
}