package com.example.smartbank;

// Custom exception for negative or zero amounts
public class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }
}