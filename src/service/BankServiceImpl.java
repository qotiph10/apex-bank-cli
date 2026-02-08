package service;

import exception.AccountNotFoundException;
import exception.InsufficientFundsException;
import exception.WithdrawalLimitExceededException;
import model.Account;
import model.CheckingAccount;
import model.SavingsAccount;
import repository.FileRepository;
import util.Logger;

import java.util.HashMap;
import java.util.Map;

public class BankServiceImpl implements BankService{
    private Map<String, Account> accounts = new HashMap<>();
    private FileRepository repository = new FileRepository();
    private Logger logger = new Logger();

    public BankServiceImpl() {
        loadData();
    }

    @Override
    public Account createAccount(String name, String type , double balance) {
        Account newAccount;

        if (type.equalsIgnoreCase("SAVINGS")) {
            newAccount = new SavingsAccount(name, balance);
        } else {
            newAccount = new CheckingAccount(name, balance);
        }

        accounts.put(newAccount.getAccountNumber(), newAccount);

        return newAccount;
    }

    @Override
    public void deposit(String accountNumber, double amount) throws AccountNotFoundException {
        Account acc = getAccount(accountNumber);
        acc.deposit(amount);
        logger.log("DEPOSIT", accountNumber, amount);
        saveData();
    }

    @Override
    public void withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InsufficientFundsException, WithdrawalLimitExceededException {

        Account acc = getAccount(accountNumber);
        acc.withdraw(amount);

        logger.log("WITHDRAWAL", accountNumber, amount);
        saveData();
    }

    @Override
    public Account login(String accountNumber) throws AccountNotFoundException {
        Account account = accounts.get(accountNumber);

        if (account == null) {
            throw new AccountNotFoundException("Login failed: The provided account number does not exist.", accountNumber);
        }

        Logger.logSystemEvent("Account " + accountNumber + " successfully logged in.");

        return account;
    }

    @Override
    public Account getAccount(String accountNumber) throws AccountNotFoundException {
        try{
            Account acc = accounts.get(accountNumber);
            if (acc == null) {
                throw new AccountNotFoundException("Account not found!", accountNumber);
            }
            return acc;
        }
        catch(AccountNotFoundException e){
            System.out.println("Could not find account: " + e.getAttemptedId());
        }
        return null;
    }

    @Override
    public void transfer(String fromId, String toId, double amount) throws AccountNotFoundException, InsufficientFundsException, WithdrawalLimitExceededException {


        Account from = getAccount(fromId);
        Account to = getAccount(toId);

        from.withdraw(amount);
        to.deposit(amount);

        String detail = String.format("%s to %s", fromId, toId);
        Logger.log("TRANSFER", detail, amount);

        saveData();
    }

    @Override
    public void processMonthlyInterest() {
        // We create a new thread so the Main UI doesn't freeze
        Thread interestThread = new Thread(() -> {
            System.out.println("\n[SYSTEM] Background Task Started: Calculating Interest...");

            int count = 0;
            try {
                // Simulate heavy processing time
                Thread.sleep(3000);

                for (Account acc : accounts.values()) {

                    if (acc instanceof SavingsAccount) {
                        SavingsAccount savings = (SavingsAccount) acc;

                        double interestAmount = savings.getBalance() * savings.getInterestRate();

                        if (interestAmount > 0) {
                            savings.deposit(interestAmount);
                            Logger.log("INTEREST", savings.getAccountNumber(), interestAmount);
                            count++;
                        }
                    }
                }
                saveData();
                System.out.println("\n[SYSTEM] Task Complete: Interest applied to " + count + " accounts.");
            } catch (InterruptedException e) {
                System.err.println("Interest calculation interrupted!");
            }
        });

        // Start the thread parallel to the main program
        interestThread.start();
    }

    @Override
    public Map<String, Account> getAllAccounts() {
        return new HashMap<>(accounts);
    }

    @Override
    public void saveData() {
        repository.save(accounts);
    }

    @Override
    public void loadData() {
        this.accounts = repository.load();

        if (this.accounts == null) {
            this.accounts = new HashMap<>();
        }
    }
}
