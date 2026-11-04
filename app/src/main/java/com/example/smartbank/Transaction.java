package com.example.smartbank;

import java.util.Date;

public class Transaction {
    private String type; // Deposit, Withdrawal, Transfer
    private double amount;
    private Date date;
    private String description;

    // Constructor to initialize transaction
    public Transaction(String type, double amount, Date date, String description) {
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    // Getters for UI display
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public Date getDate() { return date; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return date.toString() + " | " + type + ": " + amount + " - " + description;
    }
}