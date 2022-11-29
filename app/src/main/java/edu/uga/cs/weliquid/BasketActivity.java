package edu.uga.cs.weliquid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * The Basket Screen is shown here,
 */
public class BasketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        setTitle("Shopping Basket");
    }
}