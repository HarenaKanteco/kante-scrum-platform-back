package com.scrumplateform.kante.service.email;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(
            String toEmail,
            String object,
            String templateName,
            Context context
    )throws Exception{


        MimeMessage simpleMailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(simpleMailMessage,true);
        String htmlContent = templateEngine.process(templateName, context);

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setText(htmlContent,true);
        helper.setSubject(object);

        mailSender.send(simpleMailMessage);
    }
}


