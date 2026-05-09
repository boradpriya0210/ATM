package controller;

import model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ATMService;
import service.AuthService;
import repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/atm")
@CrossOrigin(origins = "*")
public class ATMController {

    private final AuthService authService = new AuthService();
    private final ATMService atmService = new ATMService();
    private final UserRepository userRepository = new UserRepository();
    private final repository.TransactionRepository transactionRepository = new repository.TransactionRepository();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String accountNumber = request.get("accountNumber");
        String pin = request.get("pin");
        
        User user = authService.login(accountNumber, pin);
        if (user != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials or account locked");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String accountNumber = request.get("accountNumber");
        String userName = request.get("userName");
        String email = request.get("email");
        String pin = request.get("pin");

        if (authService.register(accountNumber, userName, email, pin)) {
            return ResponseEntity.ok("Registration successful. Please login.");
        } else {
            return ResponseEntity.status(400).body("Account number already exists");
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> reset() {
        authService.resetDatabase();
        return ResponseEntity.ok("Database cleared successfully. Starting fresh!");
    }

    @PostMapping("/otp/send")
    public ResponseEntity<?> sendOTP(@RequestBody Map<String, String> request) {
        String accountNumber = request.get("accountNumber");
        User user = userRepository.findUserByAccountNumber(accountNumber);
        if (user != null) {
            authService.initiateOTP(user);
            return ResponseEntity.ok("OTP sent to your email: " + user.getEmail());
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<?> verifyOTP(@RequestBody Map<String, String> request) {
        String accountNumber = request.get("accountNumber");
        String otp = request.get("otp");
        if (authService.verifyOTP(accountNumber, otp)) {
            return ResponseEntity.ok("OTP Verified Successfully");
        }
        return ResponseEntity.status(400).body("Invalid or Expired OTP");
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@RequestParam String accountNumber) {
        User user = userRepository.findUserByAccountNumber(accountNumber);
        if (user != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("accountNumber", user.getAccountNumber());
            response.put("balance", user.getBalance());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody Map<String, Object> request) {
        String accountNumber = (String) request.get("accountNumber");
        double amount = Double.parseDouble(request.get("amount").toString());
        
        User user = userRepository.findUserByAccountNumber(accountNumber);
        if (user != null) {
            atmService.deposit(user, amount);
            return ResponseEntity.ok("Deposited: " + amount + ". New Balance: " + user.getBalance());
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody Map<String, Object> request) {
        String accountNumber = (String) request.get("accountNumber");
        double amount = Double.parseDouble(request.get("amount").toString());
        
        User user = userRepository.findUserByAccountNumber(accountNumber);
        if (user != null) {
            if (user.getBalance() < amount) {
                return ResponseEntity.status(400).body("Insufficient balance");
            }
            atmService.withdraw(user, amount);
            return ResponseEntity.ok("Withdrawn: " + amount + ". New Balance: " + user.getBalance());
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(@RequestParam String accountNumber) {
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
