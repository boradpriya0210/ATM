package util;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class UnlockUser {
    public static void main(String[] args) {
        String sql = "UPDATE users SET account_locked = FALSE, failed_attempts = 0 WHERE account_number = '1234567890'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int rows = pstmt.executeUpdate();
            System.out.println("User unlocked! Rows affected: " + rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
