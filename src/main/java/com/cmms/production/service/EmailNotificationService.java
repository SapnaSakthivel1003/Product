package com.cmms.production.service;


import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailNotificationService {

    private final Resend resend;

    public EmailNotificationService(Resend resend) {
        this.resend = resend;
    }

    public void sendFailureEmail(String toAddress, Long orderNum, String inspectionNum) {

        CreateEmailOptions   params = CreateEmailOptions.builder()
                .from("sapna.s@mitrahsoft.com")
                .to(toAddress)
                .subject("URGENT: Inspection FAILED - Order: " + orderNum)
                .html(String.format(
                        "<h3>An inspection has failed</h3>" +
                                "<p><strong>Order Number:</strong> %s</p>" +
                                "<p><strong>Inspection Number:</strong> %s</p>" ,
                        orderNum, inspectionNum
                ))
                .build();

        try {
            CreateEmailResponse response = resend.emails().send(params);
            log.info("response {}:",response);

        } catch (ResendException e) {
          log.error("print track trace {}:",e.getMessage());
        }
    }
}
