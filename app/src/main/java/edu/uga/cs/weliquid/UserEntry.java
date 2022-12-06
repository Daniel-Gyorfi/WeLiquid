package edu.uga.cs.weliquid;

import java.math.BigDecimal;

public class UserEntry {

    public String name;

    public float spend;

    public UserEntry() {}

    public UserEntry(String name, float money) {
        this.name = name;
        this.spend = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getSpend() {
        return spend;
    }

    public void setSpend(float spend) {
        this.spend = spend;
    }

    public void increment(float value) {
        spend += value;
    }

    @Override
    public String toString() {
        return name + " " + spend;
    }
}
