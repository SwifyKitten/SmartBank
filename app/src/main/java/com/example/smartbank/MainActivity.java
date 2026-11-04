package com.example.smartbank; // Make sure this matches your actual package name!

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private BankManager bankManager;
    private BankAccount currentAccount;

    // UI Elements
    private TextView tvOwnerName, tvAccountNumber, tvBalance;
    private EditText etAmount, etTargetAccount;
    private Button btnDeposit, btnWithdraw, btnTransfer, btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bankManager = new BankManager();

        bindUI();
        setupListeners();

        // Ask for username on startup
        askForUsername();
    }

    // Shows a popup to get the user's name before initializing the account
    private void askForUsername() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welcome to Smartbank");
        builder.setMessage("Please enter your name:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Start", (dialog, which) -> {
            String name = input.getText().toString();
            if(name.isEmpty()) {
                name = "Guest User";
            }

            // Initialize account with the chosen name
            currentAccount = new BankAccount("12345", name, 1000.0);
            bankManager.addAccount(currentAccount);

            updateUI();
        });

        builder.setCancelable(false); // Forces the user to enter a name
        builder.show();
    }

    private void bindUI() {
        tvOwnerName = findViewById(R.id.tvOwnerName);
        tvAccountNumber = findViewById(R.id.tvAccountNumber);
        tvBalance = findViewById(R.id.tvBalance);

        etAmount = findViewById(R.id.etAmount);
        etTargetAccount = findViewById(R.id.etTargetAccount);

        btnDeposit = findViewById(R.id.btnDeposit);
        btnWithdraw = findViewById(R.id.btnWithdraw);
        btnTransfer = findViewById(R.id.btnTransfer);
        btnHistory = findViewById(R.id.btnHistory);
    }

    private void setupListeners() {

        // Added Deposit logic
        btnDeposit.setOnClickListener(v -> {
            if (currentAccount == null) return;
            try {
                double amount = Double.parseDouble(etAmount.getText().toString());
                currentAccount.deposit(amount);

                Toast.makeText(this, "Deposit successful!", Toast.LENGTH_SHORT).show();
                etAmount.setText(""); // Clear input
                updateUI();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Error: Please enter a valid number", Toast.LENGTH_LONG).show();
            } catch (InvalidAmountException e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        btnWithdraw.setOnClickListener(v -> {
            if (currentAccount == null) return;
            boolean success = false;
            try {
                double amount = Double.parseDouble(etAmount.getText().toString());

                // Required ArithmeticException demonstration
                if (amount == 0.123) {
                    int error = 10 / 0;
                }

                currentAccount.withdraw(amount);
                success = true;

                Toast.makeText(this, "Withdrawal successful!", Toast.LENGTH_SHORT).show();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Error: Please enter a valid number", Toast.LENGTH_LONG).show();
            } catch (InsufficientFundException | InvalidAmountException e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (ArithmeticException e) {
                Toast.makeText(this, "Math Error: Illegal calculation occurred", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Unexpected error occurred", Toast.LENGTH_LONG).show();
            } finally {
                etAmount.setText("");
                if (success) updateUI();
            }
        });

        btnTransfer.setOnClickListener(v -> {
            if (currentAccount == null) return;
            try {
                double amount = Double.parseDouble(etAmount.getText().toString());
                String targetId = etTargetAccount.getText().toString();

                if (targetId.isEmpty()) {
                    Toast.makeText(this, "Please enter a target account", Toast.LENGTH_SHORT).show();
                    return;
                }

                bankManager.executeTransfer(currentAccount.getAccountNumber(), targetId, amount);
                Toast.makeText(this, "Transfer successful!", Toast.LENGTH_SHORT).show();
                etAmount.setText("");
                etTargetAccount.setText("");

            } catch (NullPointerException e) {
                Toast.makeText(this, "Error: Account data is missing", Toast.LENGTH_LONG).show();
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Error: Invalid account ID", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Transfer Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                updateUI();
            }
        });

        btnHistory.setOnClickListener(v -> {
            if (currentAccount == null) return;

            // 1. Keep the exception demonstration for the assignment requirement
            try {
                Transaction testTrans = currentAccount.getTransactionByIndex(9999);
            } catch (IndexOutOfBoundsException e) {
                // We caught it, now let's actually show the history below
            }

            // 2. Actually display the transaction history in a popup dialog
            StringBuilder historyText = new StringBuilder();
            for (Transaction t : currentAccount.getAllTransactions()) {
                historyText.append(t.toString()).append("\n\n");
            }

            if (historyText.length() == 0) {
                historyText.append("No transactions made yet.");
            }

            new AlertDialog.Builder(this)
                    .setTitle("Transaction History")
                    .setMessage(historyText.toString())
                    .setPositiveButton("Close", null)
                    .show();
        });
    }

    private void updateUI() {
        if (currentAccount != null) {
            tvOwnerName.setText("Hello, " + currentAccount.getOwnerName());
            tvAccountNumber.setText("Account No: " + currentAccount.getAccountNumber());
            tvBalance.setText("Current Balance: $" + currentAccount.getBalance());
        }
    }
}