package service;

import config.MailConfig;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {

    public void sendOTPEmail(String toEmail, String otp) {
        Session session = MailConfig.getSession();
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MailConfig.FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("ATM Login OTP");
            message.setText("Your ATM Login OTP is: " + otp + "\nThis OTP is valid for 2 minutes.");

            System.out.println("DEBUG: Attempting to send OTP email to: " + toEmail);
            Transport.send(message);
            System.out.println("✅ SUCCESS: OTP [" + otp + "] sent to email: " + toEmail);
        } catch (MessagingException e) {
            System.out.println("❌ ERROR: Failed to send OTP email to " + toEmail);
            System.out.println("Reason: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
