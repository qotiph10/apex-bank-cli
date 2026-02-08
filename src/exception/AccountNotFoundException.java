package exception;

public class AccountNotFoundException extends Exception {
    private final String attemptedId;

    public AccountNotFoundException(String message, String attemptedId) {
        super(message);
        this.attemptedId = attemptedId;
    }

    public String getAttemptedId() {
        return attemptedId;
    }
}
