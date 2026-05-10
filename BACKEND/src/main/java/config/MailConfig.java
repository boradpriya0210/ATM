package config;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class MailConfig {
    public static final String HOST = "smtp.gmail.com";
    public static final String PORT = "587";
    public static final String FROM_EMAIL = System.getenv("MAIL_FROM") != null ? System.getenv("MAIL_FROM") : "your_email@gmail.com";
    public static final String APP_PASSWORD = System.getenv("MAIL_PASSWORD") != null ? System.getenv("MAIL_PASSWORD") : "your_app_password";

    public static Session getSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });
    }
}
