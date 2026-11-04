package com.example.smartbank;

// Custom exception for insufficient balance
public class InsufficientFundException extends Exception {
    public InsufficientFundException(String message) {
        super(message);
    }
}
