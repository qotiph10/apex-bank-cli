package exception;

public class InsufficientFundsException extends Exception {
    private final double currentBalance;
    private final double attemptedAmount;

    public InsufficientFundsException(double currentBalance, double attemptedAmount) {
        // Create a helpful error message for the superclass
        super(String.format("Transaction failed: Attempted to withdraw $%.2f but only $%.2f is available.",
                attemptedAmount, currentBalance));
        this.currentBalance = currentBalance;
        this.attemptedAmount = attemptedAmount;
    }

    public double getShortfall() {
        return attemptedAmount - currentBalance;
    }
}
