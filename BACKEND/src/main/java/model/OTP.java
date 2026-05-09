package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTP {
    private String accountNumber;
    private String otpCode;
    private Timestamp createdAt;
}
