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
                .from("onboarding@resend.dev")
                .to(toAddress)
                .subject("URGENT: Inspection FAILED - Order: " + orderNum)
                .html(String.format(
                        "<div style=\"font-family: 'Segoe UI', Helvetica, Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; color: #333333;\">" +
                                "<div style=\"border-bottom: 2px solid #dc3545; padding-bottom: 10px; margin-bottom: 20px;\">" +
                                "<h2 style=\"color: #dc3545; margin: 0; font-size: 22px;\">⚠️ Quality Inspection Alert</h2>" +
                                "</div>" +
                                "<p style=\"font-size: 16px; line-height: 1.5; color: #555555;\">An inspection has failed during the production process. Please review the details below:</p>" +
                                "<table style=\"width: 100%%; border-collapse: collapse; margin-top: 15px;\">" +
                                "<tr>" +
                                "<td style=\"padding: 10px; border-bottom: 1px solid #eeeeee; font-weight: bold; width: 35%%; color: #666666;\">Order Number:</td>" + // Changed 35% to 35%%
                                "<td style=\"padding: 10px; border-bottom: 1px solid #eeeeee; font-family: monospace; font-size: 15px; color: #111111;\">%s</td>" +
                                "</tr>" +
                                "<tr>" +
                                "<td style=\"padding: 10px; border-bottom: 1px solid #eeeeee; font-weight: bold; color: #666666;\">Inspection ID:</td>" +
                                "<td style=\"padding: 10px; border-bottom: 1px solid #eeeeee; font-family: monospace; font-size: 15px; color: #111111;\">%s</td>" +
                                "</tr>" +
                                "</table>" +
                                "<div style=\"margin-top: 30px; font-size: 12px; color: #888888; text-align: center; border-top: 1px solid #eeeeee; padding-top: 15px;\">" +
                                "This is an automated notification from the Production Quality System." +
                                "</div>" +
                                "</div>" ,
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
