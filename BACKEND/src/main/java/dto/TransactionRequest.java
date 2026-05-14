package dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private String accountNumber;
    private double amount;
}
