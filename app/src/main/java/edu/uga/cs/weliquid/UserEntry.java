package edu.uga.cs.weliquid;

import android.util.Log;

import java.math.BigDecimal;

public class UserEntry {

    public static final String TAG = "UserEntry";

    public String name;

    public String spend;

    public UserEntry() {}

    public UserEntry(String name, String money) {
        this.name = name;
        this.spend = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpend() {
        return spend;
    }

    public void setSpend(String spend) {
        this.spend = spend;
    }

    public void increment(BigDecimal value) {
        BigDecimal temp = new BigDecimal(spend).add(value);
        Log.d(TAG, "increment: " + temp.toString());
        this.spend = temp.toString();
    }

    @Override
    public String toString() {
        return name + " " + spend;
    }
}
