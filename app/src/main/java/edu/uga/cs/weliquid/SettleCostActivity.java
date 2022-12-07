package edu.uga.cs.weliquid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SettleCostActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "SettleCostActivity";
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private List<UserEntry> rmList;
    double totalCostNum = 0.0;
    double avgCostNum = 0.0;
    String results = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_cost);

        final ActionBar ab = getSupportActionBar();
        assert ab != null;

        Log.d( DEBUG_TAG, "SettleCostActivity.onCreate()" );

        TextView totalCost = findViewById(R.id.totalCost);
        TextView avgCost = findViewById(R.id.avgCost);
        TextView roommateCost = findViewById(R.id.roommateCost);
        Button homeBtn = findViewById(R.id.homePage);

        rmList = new ArrayList<UserEntry>();
        Intent i = getIntent();
        rmList = (List<UserEntry>) i.getSerializableExtra("roommatesList");

        for (UserEntry user: rmList) {
            totalCostNum += Double.parseDouble(user.getSpend());
            results += user.getName() + ": " + user.getSpend() + "\n";
        }
        BigDecimal tCost = BigDecimal.valueOf(totalCostNum);
        BigDecimal length = BigDecimal.valueOf(rmList.size());
        BigDecimal avg = tCost.divide(length).setScale(2, RoundingMode.HALF_UP);
        avgCostNum = avg.doubleValue();

        totalCost.setText(String.valueOf(totalCostNum));
        avgCost.setText(String.valueOf(avgCostNum));
        roommateCost.setText(results);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ItemManagementActivity.class);
                view.getContext().startActivity( intent );
            }
        });
    }
}