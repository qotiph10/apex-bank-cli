# Apex Core Banking System

![Java](https://img.shields.io/badge/Language-Java-orange) ![Type](https://img.shields.io/badge/Architecture-MVC-blue) ![Status](https://img.shields.io/badge/Status-Stable-green)

**Apex Core** is a robust, enterprise-grade banking application built entirely in Core Java. It simulates a real-world banking environment with features such as persistent data storage, concurrent transaction handling, audit logging, and a configurable business logic layer.

Designed with a focus on **Clean Architecture**, this project demonstrates a clear separation of concerns using the **Service-Repository Pattern** and **Singleton Design Pattern**.

---

## 📂 Project Structure

The application follows a modular architecture, separating the Data Model, Business Logic, and Persistence layers.

```text
Banking System/
├── src/
│   └── com/
│       └── apexcore/
│           ├── Main.java                 (Entry point / CLI Interface)
│           ├── model/                    (Data Entities)
│           │   ├── Account.java          (Abstract Base Class)
│           │   ├── SavingsAccount.java   (Interest-bearing account)
│           │   └──  CheckingAccount.java  (Transaction-focused account)
│           ├── service/                  (Business Logic Layer)
│           │   ├── BankService.java      (Interface defining operations)
│           │   └── BankServiceImpl.java  (Implementation: Transfers, Deposit, Login)
│           ├── repository/               (Data Persistence Layer)
│           │   └── FileRepository.java   (Handles Serialization to .dat files)
│           ├── exception/                (Custom Error Handling)
│           │   ├── InsufficientFundsException.java
│           │   ├── WithdrawalLimitExceededException.java
│           │   └── AccountNotFoundException.java
│           └── util/                     (Utilities)
│               ├── Logger.java           (Writes audit trails to .log file)
│               ├── ConfigLoader.java     (Reads external properties)
│               └── SessionManager.java   (Singleton for user sessions)
├── data/                                 (Storage Directory)
│   ├── accounts.dat                      (Binary storage file)
│   └── audit.log                         (Human-readable transaction logs)
└── resources/
    └── config.properties                 (External configuration settings) 
```

# Key Features

The **Apex Core Banking System** is designed to mimic enterprise-level banking software. Below is a detailed breakdown of its core capabilities.

### 1. Multi-Threaded Transaction Processing
* **Concurrency Safety:** Implements `synchronized` methods to prevent race conditions (e.g., two users withdrawing from the same account simultaneously).
* **Background Tasks:** Runs heavy calculations, such as month-end interest processing, on a separate background thread to keep the main user interface responsive.

### 2. Persistent Data Storage
* **File-Based Database:** Uses Java Serialization (`ObjectOutputStream`) to save the entire state of the bank (Account Map) to a binary file (`accounts.dat`).
* **Auto-Recovery:** Data is automatically loaded upon application startup, ensuring no customer data is lost between sessions.

### 3. Professional Audit Logging
* **Transaction History:** Every financial action (Deposit, Withdrawal, Transfer) is logged to a human-readable text file (`audit.log`).
* **Security Trail:** Logs include timestamps, account IDs, and specific action types, simulating a real bank's compliance requirement.

### 4. Robust Error Handling
* **Custom Exceptions:** The system defines specific error states (`InsufficientFundsException`, `WithdrawalLimitExceededException`, `AccountNotFoundException`) rather than using generic Java errors.
* **User Feedback:** Errors are caught gracefully in the UI layer to provide helpful messages to the user instead of crashing the program.

### 5. Secure Session Management
* **Singleton Pattern:** Uses a `SessionManager` class to track the currently logged-in user.
* **Access Control:** Prevents unauthorized access to account operations (Withdraw/Transfer) unless a valid session is established.

# Technical Highlights

This section dives into the architectural decisions and code patterns used to build **Apex Core**. It is designed to demonstrate proficiency in Core Java development.

### 1. Concurrency Control (Thread Safety)
To simulate a real banking environment where multiple ATMs might access an account simultaneously, critical methods are **synchronized** to prevent race conditions (Double Spending).

* **Implementation:** The `Account` model uses `synchronized` blocks for `deposit` and `withdraw` methods.
* **Background Tasks:** Month-end interest calculation runs on a separate `Thread` to keep the main UI responsive.

```java
// Thread-safe withdrawal implementation
public synchronized void withdraw(double amount) throws InsufficientFundsException, WithdrawalLimitExceededException {
        if (amount > balance) {
            throw new InsufficientFundsException(balance , amount);
        }
        balance -= amount;
        addLog("Withdrew: $" + amount);
    }
```
### 2. Design Patterns
The application architecture relies on proven software design patterns to ensure scalability and maintainability.

* **Singleton Pattern:**
    * **Implementation:** The `SessionManager` class is implemented as a Singleton.
    * **Purpose:** Ensures that only one user session exists per running instance of the application. It acts as a global point of access for the currently logged-in user, preventing session conflicts.
    
    

* **Repository Pattern:**
    * **Implementation:** The `FileRepository` class isolates the data access logic.
    * **Purpose:** The `BankService` (Business Logic) does not need to know *how* data is saved (database vs. file). It simply calls `repo.save()`, maintaining a clean Separation of Concerns.

    

* **Factory Method (Simplified):**
    * **Implementation:** The `BankService.createAccount()` method acts as a factory.
    * **Purpose:** It encapsulates the logic for instantiating different account types (`SavingsAccount` vs. `CheckingAccount`) based on the user's input string, keeping the `Main` class clean of object creation logic.

### 3. Data Persistence (Java Serialization)
Instead of using a temporary `List` or complex database setup, the system implements robust long-term storage using Java's native Serialization capabilities.

* **Mechanism:** The `Account` class implements the `Serializable` interface. This allows the entire `Map<String, Account>` object graph to be converted into a byte stream.
* **Storage:** Data is written to a binary file (`data/accounts.dat`) using `ObjectOutputStream`.
* **Auto-Recovery:** On application startup, the system automatically checks for existing data. If found, it deserializes the object graph, restoring the bank's state (balances, transaction history, and user details) exactly as it was left.



### 4. Custom Exception Hierarchy
Rather than relying on generic Java `RuntimeExceptions`, the system defines a domain-specific error hierarchy. This allows the application to handle business logic errors gracefully without crashing.

* **`AccountNotFoundException`**: Thrown when a login ID or transfer target cannot be located in the repository.
* **`InsufficientFundsException`**: Thrown when a withdrawal or transfer amount exceeds the available balance.
* **`WithdrawalLimitExceededException`**: A specific constraint for `SavingsAccount` types, enforcing the strict 6-transaction limit per month.



### 5. Robust CLI Input Handling
Command Line Interfaces (CLIs) in Java often suffer from the "Scanner Buffer Bug" (where `nextLine()` is skipped after reading a number). **Apex Core** solves this architecturally.

* **The Strategy:** We strictly avoid mixing `nextInt()`/`nextDouble()` with `nextLine()`.
* **Implementation:** All inputs are read as Strings using `Scanner.nextLine()` and then parsed explicitly (e.g., `Double.parseDouble()`).
* **Result:** This ensures a bulletproof user experience where menu inputs never get skipped or desynchronized, regardless of user speed or input type.
