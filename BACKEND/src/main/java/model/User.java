package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String accountNumber;
    private String userName;
    private String email;
    private String pin;
    private double balance;
    private int failedAttempts;
    private boolean accountLocked;
}
