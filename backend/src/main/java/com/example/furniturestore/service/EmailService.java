package com.example.furniturestore.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.furniturestore.model.Order;
import com.example.furniturestore.model.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendOrderConfirmation(User user, Order order) {
        if (user == null || user.getEmail() == null) {
            return;
        }
        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("order", order);
        String html = templateEngine.process("order-confirmation", context);
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject("Order Confirmation");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            // log and continue
        }
    }
}
