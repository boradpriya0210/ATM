package service;

import model.Transaction;
import repository.TransactionRepository;
import java.util.List;

public class TransactionService {
    private final TransactionRepository transactionRepository = new TransactionRepository();

    public void displayMiniStatement(String accountNumber) {
        List<Transaction> transactions = transactionRepository.getMiniStatement(accountNumber);
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        System.out.println("\n--- Mini Statement ---");
        for (Transaction t : transactions) {
            System.out.printf("%s | %s | %.2f | %s\n", 
                t.getTransactionTime(), t.getTransactionType(), t.getAmount(), t.getAccountNumber());
        }
    }
}
