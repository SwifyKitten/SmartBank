package com.example.smartbank;

import java.util.HashMap;

public class BankManager {
    private HashMap<String, BankAccount> accountsList;

    // Setup manager
    public BankManager() {
        accountsList = new HashMap<>();
    }

    // Add new account
    public void addAccount(BankAccount account) {
        accountsList.put(account.getAccountNumber(), account);
    }

    // Check if account exists
    public boolean isAccountExists(String accountNumber) {
        return accountsList.containsKey(accountNumber);
    }

    // Find account by ID
    public BankAccount findAccount(String accountNumber) {
        return accountsList.get(accountNumber);
    }

    // Transfer between two managed accounts
    public void executeTransfer(String fromAccountNum, String toAccountNum, double amount)
            throws InsufficientFundException, InvalidAmountException {

        BankAccount sender = findAccount(fromAccountNum);
        BankAccount receiver = findAccount(toAccountNum);

        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("One or both accounts do not exist");
        }

        sender.transfer(receiver, amount);
    }
}