

import exception.AccountNotFoundException;
import model.Account;
import service.BankService;
import service.BankServiceImpl;
import util.SessionManager;
import java.util.Scanner;

    private static final String HR = "========================================================";
    private static final String BRAND = "                   BANKING SYSTEM                    ";
    public static void main() {


        BankService service = new BankServiceImpl();
        SessionManager sessionManager = new SessionManager();
        Scanner s = new Scanner(System.in);
        boolean exit = false;

        System.out.println(HR);
        System.out.println(BRAND);
        System.out.println(HR);

        while (!exit) {
            if (!sessionManager.isLoggedIn()) {
                exit = showGuestMenu(service, sessionManager, s);
            } else {
                exit = showUserMenu(service, sessionManager, s);
            }
        }

        System.out.println("\n[SYSTEM] Saving data and shutting down...");
        service.saveData();
        System.out.println("Goodbye!");
        s.close();
    }

    private static boolean showGuestMenu(BankService service, SessionManager sessionManager, Scanner s) {
        System.out.println("\nMAIN MENU");
        System.out.println("1. > Secure Login");
        System.out.println("2. > Open New Account");
        System.out.println("3. > Exit System");
        System.out.print("\nAction Required > ");

        String choice = s.nextLine();

        switch (choice) {
            case "1":
                performLogin(service, sessionManager, s);
                return false;
            case "2":
                Account acc = createAccountMenu(service, s);
                if (acc != null) {
                    sessionManager.setSession(acc);
                    System.out.println("\nSUCCESS: Account established.");
                    System.out.println(acc);
                }
                return false;
            case "3":
                return true;
            default:
                System.out.println("Invalid selection.");
                return false;
        }
    }

    private static boolean showUserMenu(BankService service, SessionManager sessionManager, Scanner s) {
        Account current = sessionManager.getCurrentAccount();
        System.out.println("\n" + HR);
        System.out.println("LOGGED IN AS: " + current.getAccountHolderName().toUpperCase());
        System.out.println("ACCOUNT ID  : " + current.getAccountNumber());
        System.out.println(HR);
        System.out.println("1. Deposit Funds");
        System.out.println("2. Withdraw Funds");
        System.out.println("3. View Statement (Balance)");
        System.out.println("4. Internal Transfer");
        System.out.println("5. Process interest");
        System.out.println("6. Secure Logout");
        System.out.println("7. Save & Exit");
        System.out.print("\nAction Required > ");

        String choice = s.nextLine();

        try {
            switch (choice) {
                case "1":
                    System.out.print("Enter deposit amount: $");
                    double depAmt = Double.parseDouble(s.nextLine());
                    service.deposit(current.getAccountNumber(), depAmt);
                    System.out.println("SUCCESS: Funds deposited.");
                    break;
                case "2":
                    System.out.print("Enter withdrawal amount: $");
                    double withAmt = Double.parseDouble(s.nextLine());
                    service.withdraw(current.getAccountNumber(), withAmt);
                    System.out.println("SUCCESS: Withdrawal processed.");
                    break;
                case "3":
                    System.out.printf("CURRENT BALANCE: $%,.2f\n", current.getBalance());
                    break;
                case "4":
                    System.out.print("Enter Target Account ID: ");
                    String toId = s.nextLine();
                    // Validate ID before asking for money
                    Account toAcc = service.getAccount(toId);

                    System.out.print("Amount to transfer to " + toAcc.getAccountHolderName() + ": $");
                    double transAmt = Double.parseDouble(s.nextLine());

                    service.transfer(current.getAccountNumber(), toId, transAmt);
                    System.out.println("SUCCESS: Transfer complete.");
                    break;
                case "5":
                    System.out.println("Processing Month End...");
                    service.processMonthlyInterest();
                    break;
                case "6":
                    sessionManager.logout();
                    System.out.println("Session terminated.");
                    break;
                default:
                    System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid input. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
        return false;
    }

    private static void performLogin(BankService service, SessionManager sessionManager, Scanner s) {
        System.out.print("Enter Account Number: ");
        String id = s.nextLine();
        try {
            sessionManager.setSession(service.login(id));
            System.out.println("Authentication Successful.");
        } catch (AccountNotFoundException e) {
            System.out.println("AUTHENTICATION FAILED: " + e.getMessage());
        }
    }

    private static Account createAccountMenu(BankService service, Scanner s) {
        System.out.println("\n" + HR);
        System.out.println("               ACCOUNT CREATION PORTAL                ");
        System.out.println(HR);
        System.out.println("1. Standard Checking");
        System.out.println("2. Premium Savings (High Interest)");
        System.out.println("3. Cancel");
        System.out.print("\nSelect Product > ");

        String type = s.nextLine();
        if (type.equals("3")) return null;

        System.out.print("Full Name: ");
        String name = s.nextLine();

        double balance = 0;
        try {
            if (type.equals("2")) {
                System.out.println("\n[INFO] Savings requires a minimum $5,000 opening deposit.");
                while (balance < 5000) {
                    System.out.print("Initial Deposit: $");
                    balance = Double.parseDouble(s.nextLine());
                    if (balance < 5000) System.out.println("Under minimum threshold. Try again.");
                }
            } else {
                System.out.print("Initial Deposit: $");
                balance = Double.parseDouble(s.nextLine());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered. Creating account with $0.00.");
            balance = 0;
        }

        return service.createAccount(name, type.equals("2") ? "SAVINGS" : "NORMAL", balance);
    }