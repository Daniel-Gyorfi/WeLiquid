package edu.uga.cs.weliquid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PurchasedListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased_list);
        setTitle("Purchased List");
    }
}