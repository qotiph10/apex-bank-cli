package model;
import exception.InsufficientFundsException;
import exception.WithdrawalLimitExceededException;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public abstract class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String accountNumber;
    private String accountHolderName;
    protected double balance;
    private List<String> transactionHistory;

    public Account(String accountHolderName, double initialDeposit) {
        this.accountNumber = UUID.randomUUID().toString().substring(0, 8); // Unique ID
        this.accountHolderName = accountHolderName;
        this.balance = initialDeposit;
        this.transactionHistory = new ArrayList<>();
        addLog("Account created with initial balance: $" + initialDeposit);
    }


    public abstract void applyInterest();


    public synchronized void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit must be positive.");
        }
        balance += amount;
        addLog("Deposited: $" + amount);
    }

    public synchronized void withdraw(double amount) throws InsufficientFundsException, WithdrawalLimitExceededException {
        if (amount > balance) {
            throw new InsufficientFundsException(balance , amount);
        }
        balance -= amount;
        addLog("Withdrew: $" + amount);
    }

    protected void addLog(String message) {
        transactionHistory.add(message);
    }


    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getAccountHolderName() { return accountHolderName; }
    public List<String> getTransactionHistory() { return new ArrayList<>(transactionHistory); }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return "Acc #: " + accountNumber +
                " | Owner: " + accountHolderName +
                " | Balance: $" + df.format(balance);
    }
}
