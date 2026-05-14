package service;

import model.User;
import repository.UserRepository;
import util.BCryptUtil;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OTPService otpService;

    public User login(String accountNumber, String pin) {
        User user = userRepository.findUserByAccountNumber(accountNumber);
        if (user == null) {
            System.out.println("Invalid Account Number.");
            return null;
        }

        if (user.isAccountLocked()) {
            System.out.println("Account is locked due to multiple failed attempts.");
            return null;
        }

        if (BCryptUtil.checkPassword(pin, user.getPin())) {
            userRepository.updateFailedAttempts(accountNumber, 0); // Reset attempts
            return user;
        } else {
            int attempts = user.getFailedAttempts() + 1;
            userRepository.updateFailedAttempts(accountNumber, attempts);
            if (attempts >= 3) {
                userRepository.lockAccount(accountNumber);
                System.out.println("Account locked due to 3 failed attempts.");
            } else {
                System.out.println("Invalid PIN. Attempts: " + attempts + "/3");
            }
            return null;
        }
    }

    public boolean initiateOTP(User user) {
        String otp = otpService.generateAndSaveOTP(user.getAccountNumber());
        System.out.println("DEBUG: Generated OTP [" + otp + "] for account: " + user.getAccountNumber());
        
        // Run email sending in a background thread
        new Thread(() -> {
            try {
                emailService.sendOTPEmail(user.getEmail(), otp);
            } catch (Exception e) {
                System.err.println("CRITICAL ERROR: Failed to dispatch OTP email to " + user.getEmail());
                e.printStackTrace();
            }
        }).start();
        return true;
    }


    public boolean verifyOTP(String accountNumber, String otp) {
        return otpService.validateOTP(accountNumber, otp);
    }

    public boolean register(String accountNumber, String userName, String email, String pin) {
        if (userRepository.existsByAccountNumber(accountNumber)) {
            return false;
        }
        String hashedPin = BCryptUtil.hashPassword(pin);
        User newUser = new User(accountNumber, userName, email, hashedPin, 500.0, 0, false);
        return userRepository.saveUser(newUser);
    }

    public void resetDatabase() {
        userRepository.clearData();
    }
}
