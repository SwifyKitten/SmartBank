package com.example.smartbank;

import java.util.ArrayList;
import java.util.Date;

public class BankAccount {
    private String accountNumber;
    private String ownerName;
    private double balance;
    private ArrayList<Transaction> transactions;

    // Initialize account with empty transaction list
    public BankAccount(String accountNumber, String ownerName, double initialBalance) {
        if (accountNumber == null || ownerName == null) {
            throw new IllegalArgumentException("Account details cannot be null");
        }
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
    }

    // Add money to account
    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero");
        }
        balance += amount;
        transactions.add(new Transaction("Deposit", amount, new Date(), "Deposited to branch"));
    }

    // Withdraw money with 'throws' declaration
    public void withdraw(double amount) throws InsufficientFundException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero");
        }
        if (amount > balance) {
            throw new InsufficientFundException("Not enough money in account");
        }
        balance -= amount;
        transactions.add(new Transaction("Withdrawal", amount, new Date(), "ATM Withdrawal"));
    }

    // Transfer money to another account
    public void transfer(BankAccount target, double amount) throws InsufficientFundException, InvalidAmountException {
        if (target == null) {
            throw new NullPointerException("Target account does not exist");
        }

        this.withdraw(amount); // Deduct from this account
        target.deposit(amount); // Add to target account

        this.transactions.add(new Transaction("Transfer Out", amount, new Date(), "Sent to " + target.getAccountNumber()));
    }

    // Get current balance
    public double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    // Print account details to console
    public void printDetails() {
        System.out.println("Owner: " + ownerName + ", Account: " + accountNumber + ", Balance: " + balance);
    }

    // Get specific transaction
    public Transaction getTransactionByIndex(int index) {
        if (index < 0 || index >= transactions.size()) {
            throw new IndexOutOfBoundsException("Transaction index is invalid");
        }
        return transactions.get(index);
    }

    public ArrayList<Transaction> getAllTransactions() {
        return transactions;
    }
}