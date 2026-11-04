package com.example.smartbank; // Make sure this matches your actual package name!

import java.util.HashMap;

public class BankManager {
    private HashMap<String, BankAccount> accountsList;

    public BankManager() {
        accountsList = new HashMap<>();
    }

    public void addAccount(BankAccount account) {
        accountsList.put(account.getAccountNumber(), account);
    }

    public boolean isAccountExists(String accountNumber) {
        return accountsList.containsKey(accountNumber);
    }

    public BankAccount findAccount(String accountNumber) {
        return accountsList.get(accountNumber);
    }

    // Updated: Automatically creates a target account if it doesn't exist (allows any ID to work)
    public void executeTransfer(String fromAccountNum, String toAccountNum, double amount)
            throws InsufficientFundException, InvalidAmountException {

        BankAccount sender = findAccount(fromAccountNum);
        BankAccount receiver = findAccount(toAccountNum);

        // If receiver doesn't exist, we create a dummy account so the transfer works
        if (receiver == null) {
            receiver = new BankAccount(toAccountNum, "External User", 0.0);
            addAccount(receiver);
        }

        if (sender == null) {
            throw new IllegalArgumentException("Sender account does not exist");
        }

        sender.transfer(receiver, amount);
    }
}