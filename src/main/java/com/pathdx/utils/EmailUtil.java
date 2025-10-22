package com.pathdx.utils;

import com.pathdx.dto.requestDto.EmailModelDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

@Service
public class EmailUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailUtil.class);
    @Value("${spring.mail.username}")
    private String email;
    @Value("${spring.mail.password}")
    private String password;

    @Value("${mail.smtp.auth}")
    private String smtp;
    @Value("${mail.smtp.starttls.enable}")
    private String tls;
    @Value("${mail.smtp.host}")
    private String host;
    @Value("${mail.smtp.port}")
    private String port;


    @Async
    public void sendmail(EmailModelDto emailModelDto) throws MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", smtp);
        props.put("mail.smtp.starttls.enable", tls);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        // props.put("spring.mail.properties.mail.smtp.ssl.enable","true");
        LOGGER.info("email session started from " + emailModelDto.getTo());
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(emailModelDto.getFrom(), false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailModelDto.getTo()));
        msg.setSubject(emailModelDto.getSubject());
        msg.setContent("test", "text/html");
        msg.setSentDate(new Date());
        LOGGER.info("Current Thread in email util  " + Thread.currentThread().getName());
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(emailModelDto.getBody(), "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        MimeBodyPart attachPart = new MimeBodyPart();

        // attachPart.attachFile("");
        //multipart.addBodyPart(attachPart);
        msg.setContent(multipart);
        Transport.send(msg);
    }
}
