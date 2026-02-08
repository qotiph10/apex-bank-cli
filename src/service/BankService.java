package service;

import exception.AccountNotFoundException;
import exception.InsufficientFundsException;
import exception.WithdrawalLimitExceededException;
import model.Account;
import java.util.List;
import java.util.Map;

public interface BankService {
    Account createAccount(String name, String type , double balance);

    void deposit(String accountNumber, double amount) throws AccountNotFoundException;

    void withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InsufficientFundsException, WithdrawalLimitExceededException;

    Account login(String accountNumber) throws AccountNotFoundException;

    Account getAccount(String accountNumber) throws AccountNotFoundException;

    void transfer(String fromId, String toId, double amount)
            throws AccountNotFoundException, InsufficientFundsException, WithdrawalLimitExceededException;

    void processMonthlyInterest();


    Map<String, Account> getAllAccounts();


    void saveData();


    void loadData();
}
