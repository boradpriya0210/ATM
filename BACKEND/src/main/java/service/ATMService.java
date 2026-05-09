package service;

import model.User;
import repository.UserRepository;
import repository.TransactionRepository;

public class ATMService {
    private final UserRepository userRepository = new UserRepository();
    private final TransactionRepository transactionRepository = new TransactionRepository();

    public void checkBalance(User user) {
        System.out.println("Current Balance: " + user.getBalance());
    }

    public void deposit(User user, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid amount. Must be greater than 0.");
            return;
        }
        double newBalance = user.getBalance() + amount;
        userRepository.updateBalance(user.getAccountNumber(), newBalance);
        transactionRepository.saveTransaction(user.getAccountNumber(), "DEPOSIT", amount);
        user.setBalance(newBalance);
        System.out.println("Deposited: " + amount + ". New Balance: " + newBalance);
    }

    public void withdraw(User user, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid amount. Must be greater than 0.");
            return;
        }
        if (user.getBalance() < amount) {
            System.out.println("Insufficient balance.");
            return;
        }
        double newBalance = user.getBalance() - amount;
        userRepository.updateBalance(user.getAccountNumber(), newBalance);
        transactionRepository.saveTransaction(user.getAccountNumber(), "WITHDRAW", amount);
        user.setBalance(newBalance);
        System.out.println("Withdrawn: " + amount + ". New Balance: " + newBalance);
    }
}
