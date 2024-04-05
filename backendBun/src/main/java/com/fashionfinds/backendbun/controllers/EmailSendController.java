package com.fashionfinds.backendbun.controllers;

import com.fashionfinds.backendbun.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/mail")
public class EmailSendController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public String sendMail(
            @RequestParam String to,
            @RequestParam(required = false) String[] cc,
            @RequestParam String subject,
            @RequestParam String body) {
        return emailService.sendMail(to, cc, subject, body);
    }
}
