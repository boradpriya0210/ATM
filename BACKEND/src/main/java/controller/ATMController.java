package controller;

import model.User;
import dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ATMService;
import service.AuthService;
import repository.UserRepository;
import repository.TransactionRepository;
import util.JwtUtil;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/atm")
@CrossOrigin(origins = {"http://localhost:5173", "https://aura-bank.onrender.com"})
@RequiredArgsConstructor
public class ATMController {

    private final AuthService authService;
    private final ATMService atmService;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = authService.login(request.getAccountNumber(), request.getPin());
        if (user != null) {
            authService.initiateOTP(user);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Credentials verified. Please enter any 6-digit number as your OTP.");
            response.put("accountNumber", user.getAccountNumber());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials or account locked");
        }
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "****";
        String[] parts = email.split("@");
        String name = parts[0];
        if (name.length() <= 2) return "****@" + parts[1];
        return name.substring(0, 2) + "****@" + parts[1];
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (authService.register(request.getAccountNumber(), request.getUserName(), request.getEmail(), request.getPin())) {
            return ResponseEntity.ok("Registration successful. Please login.");
        } else {
            if (userRepository.existsByAccountNumber(request.getAccountNumber())) {
                return ResponseEntity.status(400).body("Account number already exists");
            } else {
                return ResponseEntity.status(500).body("Registration failed: Database connection error");
            }
        }
    }

    @PostMapping("/otp/send")
    public ResponseEntity<?> sendOTP(@RequestBody Map<String, String> request) {
        String accountNumber = request.get("accountNumber");
        User user = userRepository.findUserByAccountNumber(accountNumber);
        if (user != null) {
            authService.initiateOTP(user);
            return ResponseEntity.ok("OTP step ready. Please enter any 6-digit number to proceed.");
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<?> verifyOTP(@RequestBody VerifyOtpRequest request) {
        if (authService.verifyOTP(request.getAccountNumber(), request.getOtp())) {
            String token = JwtUtil.generateToken(request.getAccountNumber());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP Verified Successfully");
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(400).body("Invalid or Expired OTP");
    }

    private String validateAndGetAccount(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        if (JwtUtil.validateToken(token)) {
            return JwtUtil.extractAccountNumber(token);
        }
        return null;
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@RequestParam String accountNumber, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String authenticatedAccount = validateAndGetAccount(authHeader);
        if (authenticatedAccount == null || !authenticatedAccount.equals(accountNumber)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please login and verify OTP.");
        }

        User user = userRepository.findUserByAccountNumber(accountNumber);
        if (user != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("accountNumber", user.getAccountNumber());
            response.put("userName", user.getUserName());
            response.put("balance", user.getBalance());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody TransactionRequest request, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String authenticatedAccount = validateAndGetAccount(authHeader);
        if (authenticatedAccount == null || !authenticatedAccount.equals(request.getAccountNumber())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please login and verify OTP.");
        }

        User user = userRepository.findUserByAccountNumber(request.getAccountNumber());
        if (user != null) {
            try {
                String message = atmService.deposit(user, request.getAmount());
                return ResponseEntity.ok(message);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(400).body(e.getMessage());
            }
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody TransactionRequest request, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String authenticatedAccount = validateAndGetAccount(authHeader);
        if (authenticatedAccount == null || !authenticatedAccount.equals(request.getAccountNumber())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please login and verify OTP.");
        }

        User user = userRepository.findUserByAccountNumber(request.getAccountNumber());
        if (user != null) {
            try {
                String message = atmService.withdraw(user, request.getAmount());
                return ResponseEntity.ok(message);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(400).body(e.getMessage());
            }
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String authenticatedAccount = validateAndGetAccount(authHeader);
        if (authenticatedAccount == null || !authenticatedAccount.equals(request.getFromAccountNumber())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please login and verify OTP.");
        }

        User fromUser = userRepository.findUserByAccountNumber(request.getFromAccountNumber());
        if (fromUser != null) {
            try {
                String message = atmService.transfer(fromUser, request.getToAccountNumber(), request.getAmount());
                return ResponseEntity.ok(message);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(400).body(e.getMessage());
            }
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(@RequestParam String accountNumber, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String authenticatedAccount = validateAndGetAccount(authHeader);
        if (authenticatedAccount == null || !authenticatedAccount.equals(accountNumber)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please login and verify OTP.");
        }
        return ResponseEntity.ok(transactionRepository.getMiniStatement(accountNumber));
    }

    @GetMapping("/debug/db")
    public ResponseEntity<?> debugDB() {
        Map<String, Object> status = new HashMap<>();
        boolean isConnected = config.DBConnection.checkConnection();
        status.put("databaseConnected", isConnected);
        
        if (isConnected) {
            User testUser = userRepository.findUserByAccountNumber("1234567890");
            status.put("sampleUserFound", testUser != null);
            if (testUser != null) {
                status.put("accountLocked", testUser.isAccountLocked());
            }
        }
        
        return ResponseEntity.ok(status);
    }
}
