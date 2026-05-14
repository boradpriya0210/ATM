package config;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailConfig {
    public static final String HOST = "smtp.gmail.com";
    public static final String PORT = "587";

    // Static fields populated from Spring properties (which read from env vars)
    public static String FROM_EMAIL;
    public static String APP_PASSWORD;

    // Spring injects these from application.properties → which reads from env vars
    @Value("${mail.email:}")
    public void setFromEmail(String email) {
        FROM_EMAIL = email;
    }

    @Value("${mail.password:}")
    public void setAppPassword(String password) {
        APP_PASSWORD = password;
    }

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
