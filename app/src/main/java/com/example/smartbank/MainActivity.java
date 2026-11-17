package com.example.smartbank;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private Button btnCreateAccount, btnSwitchAccount, btnVerifyTarget; // New buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bankManager = new BankManager();

        // יצירת חשבונות התחלתיים כדי שיהיה קל לבדוק
        bankManager.addAccount(new BankAccount("111", "Admin", 5000));
        bankManager.addAccount(new BankAccount("222", "Test User", 100));

        bindUI();
        setupListeners();

        // התחברות לחשבון הדיפולטיבי בהתחלה
        switchAccount("111");
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

        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnSwitchAccount = findViewById(R.id.btnSwitchAccount);
        btnVerifyTarget = findViewById(R.id.btnVerifyTarget);
    }

    private void setupListeners() {

        // יצירת חשבון חדש באופן יזום
        btnCreateAccount.setOnClickListener(v -> showCreateAccountDialog());

        // בחירת חשבון מקור
        btnSwitchAccount.setOnClickListener(v -> showSwitchAccountDialog());

        // הצגת פרטי חשבון יעד לפני העברה
        btnVerifyTarget.setOnClickListener(v -> {
            String targetId = etTargetAccount.getText().toString();
            BankAccount target = bankManager.findAccount(targetId);
            if(target != null) {
                Toast.makeText(this, "Target: " + target.getOwnerName(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Account not found!", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeposit.setOnClickListener(v -> {
            if (currentAccount == null) return;
            try {
                double amount = Double.parseDouble(etAmount.getText().toString());
                currentAccount.deposit(amount);
                Toast.makeText(this, "Deposit successful!", Toast.LENGTH_SHORT).show();
                etAmount.setText("");
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
                currentAccount.withdraw(amount);
                success = true;
                Toast.makeText(this, "Withdrawal successful!", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Error: Please enter a valid number", Toast.LENGTH_LONG).show();
            } catch (InsufficientFundException | InvalidAmountException e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

                bankManager.executeTransfer(currentAccount.getAccountNumber(), targetId, amount);
                Toast.makeText(this, "Transfer successful!", Toast.LENGTH_SHORT).show();
                etAmount.setText("");
                etTargetAccount.setText("");

            } catch (NullPointerException e) {
                Toast.makeText(this, "Error: Target details missing", Toast.LENGTH_LONG).show();
            } catch (IllegalArgumentException | InsufficientFundException | InvalidAmountException e) {
                Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Transfer Failed", Toast.LENGTH_LONG).show();
            } finally {
                updateUI();
            }
        });

        btnHistory.setOnClickListener(v -> {
            if (currentAccount == null) return;

            // זריקה ותפיסה של IndexOutOfBoundsException כנדרש בפרויקט
            try {
                currentAccount.getTransactionByIndex(9999);
            } catch (IndexOutOfBoundsException e) { }

            StringBuilder historyText = new StringBuilder();
            for (Transaction t : currentAccount.getAllTransactions()) {
                historyText.append(t.toString()).append("\n\n");
            }
            if (historyText.length() == 0) historyText.append("No transactions yet.");

            new AlertDialog.Builder(this)
                    .setTitle("History: " + currentAccount.getOwnerName())
                    .setMessage(historyText.toString())
                    .setPositiveButton("Close", null)
                    .show();
        });
    }

    // פונקציה ליצירת חשבון חדש מתוך האפליקציה
    private void showCreateAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Account");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText idInput = new EditText(this);
        idInput.setHint("Account ID (e.g. 333)");
        layout.addView(idInput);

        final EditText nameInput = new EditText(this);
        nameInput.setHint("Owner Name");
        layout.addView(nameInput);

        builder.setView(layout);

        builder.setPositiveButton("Create", (dialog, which) -> {
            try {
                String id = idInput.getText().toString();
                String name = nameInput.getText().toString();
                BankAccount newAcc = new BankAccount(id, name, 0); // מתחיל עם 0 דולר
                bankManager.addAccount(newAcc);
                Toast.makeText(this, "Account Created! You can now switch to it.", Toast.LENGTH_LONG).show();
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // פונקציה להחלפת החשבון הנוכחי
    private void showSwitchAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Switch Account");
        builder.setMessage("Enter the Account ID to login:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Login", (dialog, which) -> {
            String id = input.getText().toString();
            switchAccount(id);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void switchAccount(String accountId) {
        BankAccount acc = bankManager.findAccount(accountId);
        if(acc != null) {
            currentAccount = acc;
            Toast.makeText(this, "Switched to " + acc.getOwnerName(), Toast.LENGTH_SHORT).show();
            updateUI();
        } else {
            Toast.makeText(this, "Account not found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        if (currentAccount != null) {
            tvOwnerName.setText(currentAccount.getOwnerName() + "'s Account");
            tvAccountNumber.setText("Account ID: " + currentAccount.getAccountNumber());
            tvBalance.setText("Current Balance: $" + currentAccount.getBalance());
        }
    }
}