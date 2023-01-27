package com.example.smsweb;

import com.example.smsweb.mail.Mail;
import com.example.smsweb.mail.MailService;
import com.example.smsweb.utils.StringUtils;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class SmswebApplicationTests {

    @Autowired
    private MailService mailService;
    String numbers="1234567890";

    @Test
    public void sendHtmlMessageTest() throws MessagingException {
        Mail email = new Mail();
        email.setToMail("vongochoa30@gmail.com");
        email.setSubject("Welcome Email from CodingNConcepts");
        Map<String, Object> properties = new HashMap<>();
        properties.put("accountName", "Ashish");
        properties.put("password", "123456");
        email.setProps(properties);
        Assertions.assertDoesNotThrow(() -> mailService.sendHtmlMessage(email));

    }

    @Test
    public void TestRandomStudentCard(){
        Assertions.assertDoesNotThrow(()-> StringUtils.randomStudentCard(numbers));
    }

}
