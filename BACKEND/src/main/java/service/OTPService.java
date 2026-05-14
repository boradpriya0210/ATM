package service;

import org.springframework.stereotype.Service;

@Service
public class OTPService {

    /**
     * No email is sent. Any 6-digit number entered by the user is accepted as a valid OTP.
     * This method exists for API compatibility but performs no real OTP generation/storage.
     */
    public void generateAndSaveOTP(String accountNumber) {
        // No-op: OTP is no longer generated or stored.
        System.out.println("INFO: OTP step initiated for account: " + accountNumber + ". User may enter any 6-digit number.");
    }

    /**
     * Validates that the entered value is any 6-digit number (100000–999999).
     * No email OTP lookup is performed.
     */
    public boolean validateOTP(String accountNumber, String enteredOtp) {
        if (enteredOtp == null) return false;
        String trimmed = enteredOtp.trim();
        if (trimmed.length() != 6) return false;
        try {
            int value = Integer.parseInt(trimmed);
            boolean valid = value >= 100000 && value <= 999999;
            if (valid) {
                System.out.println("INFO: OTP accepted for account: " + accountNumber);
            } else {
                System.out.println("INFO: OTP rejected (out of range) for account: " + accountNumber);
            }
            return valid;
        } catch (NumberFormatException e) {
            System.out.println("INFO: OTP rejected (not numeric) for account: " + accountNumber);
            return false;
        }
    }
}
