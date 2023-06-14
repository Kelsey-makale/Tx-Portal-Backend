package com.interswitchgroup.tx_user_portal.services;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class EmailService {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendMail(String to, String subject, String body){
        try{
            MimeMessage msg = mailSender.createMimeMessage();
            msg.setFrom("sianwamak@gmail.com");
            msg.setRecipients(MimeMessage.RecipientType.TO,to);
            msg.setSubject(subject);
            msg.setText(body, "utf-8", "html");

            mailSender.send(msg);
        }catch(Exception e){
            LOGGER.error("Failed to send email", e);
            throw new IllegalStateException("Failed to send email");
        }

    }

    @Async
    public void sendMailWithCC(String to, String subject, String body, String[] cc){

        try{
            MimeMessage msg = mailSender.createMimeMessage();
            InternetAddress[] ccAddress = new InternetAddress[cc.length];

            // To get the array of ccaddresses
            for( int i = 0; i < cc.length; i++ ) {
                ccAddress[i] = new InternetAddress(cc[i]);
            }

            // Set cc: header field of the header.
            for(InternetAddress address : ccAddress) {
                msg.addRecipient(MimeMessage.RecipientType.CC, address);
            }

            msg.setFrom("sianwamak@gmail.com");
            msg.setRecipients(MimeMessage.RecipientType.TO,to);
            msg.setSubject(subject);
            msg.setText(body, "utf-8", "html");

            mailSender.send(msg);

        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("Failed to send email", e);
            throw new IllegalStateException("Failed to send email");
        }



    }

}
