package com.example.smartbank;
import java.util.ArrayList;
import java.util.Date;

public class BankAccount {
    private String accountNumber;
    private String ownerName;
    private double balance;
    private ArrayList<Transaction> transactions;

    public BankAccount(String accountNumber, String ownerName, double initialBalance) {
        if (accountNumber == null || ownerName == null || accountNumber.isEmpty()) {
            throw new IllegalArgumentException("Account details cannot be empty");
        }
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
    }

    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero.");
        }
        if (amount > 1000000) { // בדיקה אוטומטית למניעת הלבנת הון/סכום חריג
            throw new InvalidAmountException("Cannot deposit more than $1,000,000 at once.");
        }
        balance += amount;
        transactions.add(new Transaction("Deposit", amount, new Date(), "Branch Deposit"));
    }

    public void withdraw(double amount) throws InsufficientFundException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        }
        // בדיקה אוטומטית קריטית - מניעת כניסה למינוס
        if (balance - amount < 0) {
            throw new InsufficientFundException("Transaction denied: Cannot go into negative balance. You are short by $" + (amount - balance));
        }
        balance -= amount;
        transactions.add(new Transaction("Withdrawal", amount, new Date(), "ATM Withdrawal"));
    }

    public void transfer(BankAccount target, double amount) throws InsufficientFundException, InvalidAmountException {
        if (target == null) {
            throw new NullPointerException("Target account does not exist.");
        }
        this.withdraw(amount);
        target.deposit(amount);

        this.transactions.add(new Transaction("Transfer Out", amount, new Date(), "Sent to " + target.getOwnerName()));
    }

    public double getBalance() { return balance; }
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }

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