package config;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class MailConfig {
    public static final String HOST = "smtp.gmail.com";
    public static final String PORT = "587"; // Changed from 465 (SSL) to 587 (STARTTLS) — 465 is blocked by Render

    // Credentials loaded from environment variables — NEVER hardcode these
    public static final String FROM_EMAIL = System.getenv("MAIL_EMAIL");
    public static final String APP_PASSWORD = System.getenv("MAIL_PASSWORD");

    public static Session getSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Required for port 587
        // Removed socketFactory lines — those were only needed for SSL/port 465

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });
    }
}
