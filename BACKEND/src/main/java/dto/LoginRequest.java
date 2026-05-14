package dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String accountNumber;
    private String pin;
}
