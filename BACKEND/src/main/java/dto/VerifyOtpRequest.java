package dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String accountNumber;
    private String otp;
}
