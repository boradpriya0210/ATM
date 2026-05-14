package dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String accountNumber;
    private String userName;
    private String email;
    private String pin;
}
