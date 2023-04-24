package com.example.demo.updates;

import com.example.demo.auth.models.User;
import com.example.demo.features.models.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

@Component
public class EmailUtil {

    @Value("${EMAIL_FROM}")
    private String senderEmail;

    @Value("${EMAIL_PW}")
    private String emailPw;


    public boolean sendEmail(String recipientEmail, String subject, String body) {
//        String decodedPw = new String(Base64.getDecoder().decode(emailPw));
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.auth", "true"); //enable authentication
//        props.put("mail.smtp.port", "587"); //TLS Port
//        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
        props.put("mail.smtp.ssl.enable", "true"); // enable ssl
        props.put("mail.smtp.ssl.socketFactory.port", "465"); // SSL port

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, emailPw);
            }
        };
        Session session = Session.getInstance(props, auth);
        MimeMessage msg = new MimeMessage(session);

        try {
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(senderEmail, "NoReply-JD"));

            msg.setReplyTo(InternetAddress.parse(senderEmail, false));

            msg.setSubject(subject, "UTF-8");

            msg.setContent(body, "text/html; charset=utf-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));
            Transport.send(msg);
            System.out.println("Email sent successfully!");
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendEmail(Item item) {
        String decodedPw = new String(Base64.getDecoder().decode(emailPw));
        User user = item.getUser();
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, decodedPw);
            }
        };
        Session session = Session.getInstance(props, auth);
        MimeMessage msg = new MimeMessage(session);

        try {
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(senderEmail, "NoReply-JD"));

            msg.setReplyTo(InternetAddress.parse(senderEmail, false));

            String subject = "Your item is expiring: " + item.getItemName();
            msg.setSubject(subject, "UTF-8");

            String body = "Hello " + user.getUsername() + ", your item [ " + item.getItemName() + " ] is expiring soon." +
                    " Please take action before it expires on <strong>" + item.getExpiryDate().toString() + "</strong>" ;
            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail(), false));
            Transport.send(msg);
            System.out.println("Email sent successfully!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
