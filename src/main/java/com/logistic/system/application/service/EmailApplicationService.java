package com.logistic.system.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailApplicationService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String fromEmail;

    @Async // Rất quan trọng: Gửi mail ở luồng riêng, không bắt luồng chính chờ
    public void sendShipmentStatusEmail(String to, String customerName, String trackingCode, String statusName) {
        try {
            log.info("Starting to send shipment status email to {} for tracking code {}", to, trackingCode);

            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("trackingCode", trackingCode);
            context.setVariable("statusName", statusName);

            String htmlContent = templateEngine.process("shipment-status", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setSubject("[LogisticSystem] Cập nhật trạng thái đơn hàng " + trackingCode);
            helper.setTo(to);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}