package ui;

import model.User;
import service.AuthService;
import service.ATMService;
import service.TransactionService;
import java.util.Scanner;

public class ATMMenu {
    private final AuthService authService = new AuthService();
    private final ATMService atmService = new ATMService();
    private final TransactionService transactionService = new TransactionService();
    private final Scanner scanner = new Scanner(System.in);

    public void showMainMenu() {
        while (true) {
            System.out.println("\n--- Welcome to Secure ATM ---");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    System.out.println("Thank you for using our ATM. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void handleLogin() {
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        User user = authService.login(accountNumber, pin);
        if (user != null) {
            authService.initiateOTP(user);
            System.out.print("Enter the 6-digit OTP sent to your email: ");
            String enteredOtp = scanner.nextLine();

            if (authService.verifyOTP(user.getAccountNumber(), enteredOtp)) {
                System.out.println("Authentication Successful!");
                showATMOperations(user);
            } else {
                System.out.println("OTP Verification Failed.");
            }
        }
    }

    private void showATMOperations(User user) {
        while (true) {
            System.out.println("\n--- ATM Operations ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Mini Statement");
            System.out.println("5. Logout");
            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    atmService.checkBalance(user);
                    break;
                case 2:
                    System.out.print("Enter amount to deposit: ");
                    double depAmt = scanner.nextDouble();
                    atmService.deposit(user, depAmt);
                    break;
                case 3:
                    System.out.print("Enter amount to withdraw: ");
                    double witAmt = scanner.nextDouble();
                    atmService.withdraw(user, witAmt);
                    break;
                case 4:
                    transactionService.displayMiniStatement(user.getAccountNumber());
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}