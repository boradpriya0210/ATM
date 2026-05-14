package service;

import model.OTP;
import repository.OTPRepository;
import util.OTPGenerator;
import java.sql.Timestamp;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OTPService {
    private final OTPRepository otpRepository;
    private int attempts = 0;

    public String generateAndSaveOTP(String accountNumber) {
        String otp = OTPGenerator.generateOTP();
        otpRepository.saveOTP(accountNumber, otp);
        attempts = 0; // Reset attempts for new OTP
        return otp;
    }

    public boolean validateOTP(String accountNumber, String enteredOtp) {
        // Fake OTP for local testing
        if ("000000".equals(enteredOtp)) {
            System.out.println("DEBUG: Fake OTP '000000' used for account: " + accountNumber);
            otpRepository.deleteOTP(accountNumber);
            return true;
        }

        OTP otp = otpRepository.getOTP(accountNumber);
        if (otp == null) return false;

        // Check expiry (2 minutes)
        long currentTime = System.currentTimeMillis();
        long otpTime = otp.getCreatedAt().getTime();
        if (currentTime - otpTime > 2 * 60 * 1000) {
            System.out.println("OTP expired.");
            return false;
        }

        if (otp.getOtpCode().equals(enteredOtp)) {
            otpRepository.deleteOTP(accountNumber);
            return true;
        } else {
            attempts++;
            if (attempts >= 3) {
                System.out.println("Maximum OTP attempts reached.");
            }
            return false;
        }
    }

    public int getAttempts() {
        return attempts;
    }
}
