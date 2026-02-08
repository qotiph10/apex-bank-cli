package model;

import exception.InsufficientFundsException;
import exception.WithdrawalLimitExceededException;

public class SavingsAccount extends Account{

    private double interestRate = 1.0;
    private int withdrawalLimit = 0;
    public SavingsAccount(String accountHolderName, double initialDeposit) {
        super(accountHolderName, initialDeposit);
    }

    @Override
    public void applyInterest() {

        double interestEarned = balance * (interestRate / 100);
        balance += interestEarned;
        System.out.printf("Interest of $%.2f applied. New balance: $%.2f%n",interestEarned, balance);
    }

    @Override
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit must be positive.");
        }
        balance += amount;
        addLog("Deposited: $" + amount);
        updateInterestRate();
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException, WithdrawalLimitExceededException {
        if(withdrawalLimit > 6){
            throw new WithdrawalLimitExceededException("Withdrawal limit exceeded for account: " + getAccountNumber());
        }
        if (amount > balance) {
            throw new InsufficientFundsException(balance , amount);
        }
        withdrawalLimit +=1;
        balance -= amount;
        addLog("Withdrew: $" + amount);
        updateInterestRate();
    }

    public void updateInterestRate() {
        if (this.balance < 5000) {
            this.interestRate = 0.0;
        } else if (this.balance < 10000) {
            this.interestRate = 1.0;
        } else if (this.balance < 50000) {
            this.interestRate = 2.5;
        } else {
            this.interestRate = 4.0;
        }

        // Optional: Feedback for the console
        System.out.println("Current Interest Rate updated to: " + this.interestRate + "%");
    }

    public double getInterestRate() {
        return interestRate;
    }
}
