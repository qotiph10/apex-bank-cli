package util;

import model.Account;

public class SessionManager {
    private static Account loggedInAccount;

    public static void setSession(Account account) {
        loggedInAccount = account;
    }

    public static Account getCurrentAccount() {
        return loggedInAccount;
    }

    public static boolean isLoggedIn() {
        return loggedInAccount != null;
    }

    public static void logout() {
        loggedInAccount = null;
    }
}
