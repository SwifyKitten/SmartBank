package com.example.smartbank; // Make sure this matches your actual package name!

import java.util.HashMap;

public class BankManager {
    private HashMap<String, BankAccount> accountsList;

    public BankManager() {
        accountsList = new HashMap<>();
    }

    public void addAccount(BankAccount account) {
        if(isAccountExists(account.getAccountNumber())) {
            throw new IllegalArgumentException("Account ID already exists in the system!");
        }
        accountsList.put(account.getAccountNumber(), account);
    }

    public boolean isAccountExists(String accountNumber) {
        return accountsList.containsKey(accountNumber);
    }

    public BankAccount findAccount(String accountNumber) {
        return accountsList.get(accountNumber);
    }

    public void executeTransfer(String fromAccountNum, String toAccountNum, double amount)
            throws InsufficientFundException, InvalidAmountException {

        // תיקון: חסימת העברה לאותו החשבון
        if (fromAccountNum.equals(toAccountNum)) {
            throw new IllegalArgumentException("You cannot transfer money to your own account!");
        }

        BankAccount sender = findAccount(fromAccountNum);
        BankAccount receiver = findAccount(toAccountNum);

        // תיקון: זריקת שגיאה אם חשבון היעד לא קיים (במקום ליצור אותו)
        if (receiver == null) {
            throw new IllegalArgumentException("Target account does not exist in the bank!");
        }
        if (sender == null) {
            throw new IllegalArgumentException("Sender account is missing!");
        }

        sender.transfer(receiver, amount);
    }
}