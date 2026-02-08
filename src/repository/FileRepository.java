package repository;

import model.Account;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileRepository {
    private final String FILE_PATH = "data/accounts.dat";

    // This method saves the entire Map to the .dat file
    public void save(Map<String, Account> accounts) {
        // try-with-resources automatically closes the streams for us
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(accounts);
        } catch (IOException e) {
            System.err.println("Critical Error: Could not save data to " + FILE_PATH);
            e.printStackTrace();
        }
    }

    // This method reads the Map back from the .dat file
    @SuppressWarnings("unchecked")
    public Map<String, Account> load() {
        File file = new File(FILE_PATH);

        // If the file doesn't exist yet (first run), return an empty map
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Map<String, Account>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Warning: Could not load data. Starting with a fresh system.");
            return new HashMap<>();
        }
    }
}
