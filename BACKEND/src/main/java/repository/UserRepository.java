package repository;

import config.DBConnection;
import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    public User findUserByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM users WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("account_number"),
                        rs.getString("user_name"),
                        rs.getString("email"),
                        rs.getString("pin"),
                        rs.getDouble("balance"),
                        rs.getInt("failed_attempts"),
                        rs.getBoolean("account_locked")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateFailedAttempts(String accountNumber, int attempts) {
        String sql = "UPDATE users SET failed_attempts = ? WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attempts);
            pstmt.setString(2, accountNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void lockAccount(String accountNumber) {
        String sql = "UPDATE users SET account_locked = TRUE WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE users SET balance = ? WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean saveUser(User user) {
        String sql = "INSERT INTO users (account_number, user_name, email, pin, balance) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getAccountNumber());
            pstmt.setString(2, user.getUserName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPin());
            pstmt.setDouble(5, user.getBalance());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsByAccountNumber(String accountNumber) {
        String sql = "SELECT COUNT(*) FROM users WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void clearData() {
        try (Connection conn = DBConnection.getConnection()) {
            conn.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0");
            conn.createStatement().execute("TRUNCATE TABLE transactions");
            conn.createStatement().execute("TRUNCATE TABLE users");
            conn.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
