package com.example.smartbank;

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
        setContentView(R.layout.activity_main); // Links to the XML layout

        initSystem();
        bindUI();
        setupListeners();
        updateUI();
    }

    // Create mock data
    private void initSystem() {
        bankManager = new BankManager();
        currentAccount = new BankAccount("12345", "Israel Israeli", 1000.0);
        BankAccount targetAccount = new BankAccount("67890", "Shlomo Artzi", 500.0);

        bankManager.addAccount(currentAccount);
        bankManager.addAccount(targetAccount);
    }

    // Link code to screen components
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

    // Set click events for buttons
    private void setupListeners() {

        btnWithdraw.setOnClickListener(v -> {
            boolean success = false;
            try {
                // Parse amount from text input
                String amountStr = etAmount.getText().toString();
                double amount = Double.parseDouble(amountStr);

                // Arithmetic exception example
                if (amount == 0.123) {
                    int error = 10 / 0;
                }

                // Attempt to withdraw
                currentAccount.withdraw(amount);
                success = true;

                Toast.makeText(this, "Withdrawal successful!", Toast.LENGTH_SHORT).show();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Error: Please enter a valid number", Toast.LENGTH_LONG).show();
            } catch (InsufficientFundException e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (InvalidAmountException e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (ArithmeticException e) {
                Toast.makeText(this, "Math Error: Illegal calculation occurred", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Unexpected error occurred", Toast.LENGTH_LONG).show();
            } finally {
                // Finally block runs no matter what
                etAmount.setText("");
                if (success) {
                    updateUI();
                }
            }
        });

        btnTransfer.setOnClickListener(v -> {
            try {
                double amount = Double.parseDouble(etAmount.getText().toString());
                String targetId = etTargetAccount.getText().toString();

                bankManager.executeTransfer(currentAccount.getAccountNumber(), targetId, amount);
                Toast.makeText(this, "Transfer successful!", Toast.LENGTH_SHORT).show();

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
            try {
                // Try accessing a transaction that might not exist
                Transaction t = currentAccount.getTransactionByIndex(100);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(this, "No such transaction index exists", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Refresh display values
    private void updateUI() {
        tvOwnerName.setText("Hello, " + currentAccount.getOwnerName());
        tvAccountNumber.setText("Account No: " + currentAccount.getAccountNumber());
        tvBalance.setText("Current Balance: $" + currentAccount.getBalance());
    }
}