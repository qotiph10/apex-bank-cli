package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE = "data/audit.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(String action, String accountId, double amount) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[%s] ACTION: %-10s | ACC: %-8s | AMT: $%,.2f",
                timestamp, action, accountId, amount);

        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Could not write to log file: " + e.getMessage());
        }
    }

    public static void logSystemEvent(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(String.format("[%s] SYSTEM: %s", timestamp, message));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Could not write system log: " + e.getMessage());
        }
    }
}