package com.example.smsweb.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    @Value("$spring.mail.username")
    private String fromMail;

    public void sendHtmlMessage(Mail mail) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(mail.getProps());
        helper.setFrom(fromMail);
        helper.setTo(mail.getToMail());
        helper.setSubject(mail.getSubject());
        String html = templateEngine.process("mail/mail-template.html", context);
        helper.setText(html, true);
        log.info("Sending email: {} with html body: {}", mail, html);
        emailSender.send(message);
    }
    public void sendHtmlMessageResetPass(Mail mail) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(mail.getProps());
        helper.setFrom(fromMail);
        helper.setTo(mail.getToMail());
        helper.setSubject(mail.getSubject());
        String html = templateEngine.process("mail/rest-password_template.html", context);
        helper.setText(html, true);
        log.info("Sending email: {} with html body: {}", mail, html);
        emailSender.send(message);
    }

    public void sendHtmlMessageSendSchedule(Mail mail, File attachment) throws MessagingException {
       try {
           MimeMessage message = emailSender.createMimeMessage();
           MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
           Context context = new Context();
           context.setVariables(mail.getProps());
           helper.setFrom(fromMail);
           helper.setTo(mail.getToMail());
           helper.setSubject(mail.getSubject());
           String html = templateEngine.process("mail/mail-send-schedule.html", context);
           helper.setText(html, true);
           FileSystemResource file = new FileSystemResource(attachment);
           helper.addAttachment(attachment.getName(), file);
           log.info("Sending email: {} with html body: {}", mail, html);
           emailSender.send(message);
       }catch (Exception e) {
           e.printStackTrace();
       }
    }
}

