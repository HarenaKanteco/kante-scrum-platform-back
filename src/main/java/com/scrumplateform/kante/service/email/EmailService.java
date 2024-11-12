package com.scrumplateform.kante.service.email;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
public interface EmailService {
    public void sendEmail(
        String toEmail,
        String object,
        String templateName,
        Context context
    )throws Exception;
}
