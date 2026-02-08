package model;

import exception.InsufficientFundsException;

public class CheckingAccount extends Account{

    private double overdraftLimit = -1000;
    private double transactionFee = 1;
    public CheckingAccount(String accountHolderName, double initialDeposit) {
        super(accountHolderName, initialDeposit);
    }
    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (balance - amount - transactionFee < overdraftLimit) {
            throw new InsufficientFundsException(balance , amount);
        }
        balance -= amount - transactionFee;
        addLog("Withdrew: $" + amount);
    }

    @Override
    public void applyInterest() {
        return;
    }


}
