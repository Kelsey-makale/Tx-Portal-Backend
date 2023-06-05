package com.interswitchgroup.tx_user_portal.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendMail(String to, String subject, String body){
        try{

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(body);
            mailMessage.setFrom("sianwamak@gmail.com"); //todo: get proper email

            mailSender.send(mailMessage);


        }catch(Exception e){
            LOGGER.error("Failed to send email", e);
            throw new IllegalStateException("Failed to send email");
        }

    }

    @Async
    public void sendMailWithCC(String to, String subject, String body, String[] cc){
        try{

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(body);
            mailMessage.setCc(cc);
            mailMessage.setFrom("support.ke@quickteller.com"); //todo: get proper email

            mailSender.send(mailMessage);

        }catch(Exception e){
            LOGGER.error("Failed to send email", e);
            throw new IllegalStateException("Failed to send email");
        }

    }
}
