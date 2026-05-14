package service;

import model.User;
import repository.UserRepository;
import repository.TransactionRepository;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ATMService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public void checkBalance(User user) {
        System.out.println("Current Balance: " + user.getBalance());
    }

    public String deposit(User user, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid amount. Must be greater than 0.");
        }
        double newBalance = user.getBalance() + amount;
        userRepository.updateBalance(user.getAccountNumber(), newBalance);
        transactionRepository.saveTransaction(user.getAccountNumber(), "DEPOSIT", amount);
        user.setBalance(newBalance);
        return "Deposited: " + amount + ". New Balance: " + newBalance;
    }

    public String withdraw(User user, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid amount. Must be greater than 0.");
        }
        if (user.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        double newBalance = user.getBalance() - amount;
        userRepository.updateBalance(user.getAccountNumber(), newBalance);
        transactionRepository.saveTransaction(user.getAccountNumber(), "WITHDRAW", amount);
        user.setBalance(newBalance);
        return "Withdrawn: " + amount + ". New Balance: " + newBalance;
    }

    public String transfer(User fromUser, String toAccountNumber, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid amount. Must be greater than 0.");
        }
        if (fromUser.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        if (fromUser.getAccountNumber().equals(toAccountNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        User toUser = userRepository.findUserByAccountNumber(toAccountNumber);
        if (toUser == null) {
            throw new IllegalArgumentException("Recipient account not found");
        }

        // Debit sender
        double fromNewBalance = fromUser.getBalance() - amount;
        userRepository.updateBalance(fromUser.getAccountNumber(), fromNewBalance);
        transactionRepository.saveTransaction(fromUser.getAccountNumber(), "TRANSFER_OUT", amount);
        fromUser.setBalance(fromNewBalance);

        // Credit recipient
        double toNewBalance = toUser.getBalance() + amount;
        userRepository.updateBalance(toUser.getAccountNumber(), toNewBalance);
        transactionRepository.saveTransaction(toUser.getAccountNumber(), "TRANSFER_IN", amount);

        return "Successfully transferred $" + amount + " to account " + toAccountNumber;
    }
}
