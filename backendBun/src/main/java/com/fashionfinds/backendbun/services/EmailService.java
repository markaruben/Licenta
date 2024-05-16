package com.fashionfinds.backendbun.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${mail.fromName}")
    private String fromName;

    @Autowired
    private JavaMailSender javaMailSender;

    public String sendMail(String to, String[] cc, String subject, String body) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(String.format("%s <%s>", fromName, fromEmail));
            mimeMessageHelper.setTo(to);
            if (cc != null) {
                mimeMessageHelper.setCc(cc);
            }
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body);

            javaMailSender.send(mimeMessage);

            return "Mail sent successfully";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
