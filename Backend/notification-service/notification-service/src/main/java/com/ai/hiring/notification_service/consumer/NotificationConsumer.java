package com.ai.hiring.notification_service.consumer;



import com.ai.hiring.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailService emailService;

   /* @KafkaListener(topics = "application-events", groupId = "notification-group")
    public void handleApplicationEvent(String message) {
        System.out.println("Kafka event received: " + message);

        // Email aur message alag karo
        String[] parts = message.split("::", 2);
        if (parts.length == 2) {
            String toEmail = parts[0];
            String emailBody = parts[1];

            emailService.sendEmail(
                    toEmail,
                    "HireAI — Application Update",
                    emailBody
            );
            System.out.println("Email sent to: " + toEmail);
        }
    }*/


        @KafkaListener(topics = "application-events", groupId = "notification-group")
public void handleApplicationEvent(String message) {
    System.out.println("Kafka event received: " + message);

    // Quotes remove karo
    String cleanMessage = message.replace("\"", "");

    String[] parts = cleanMessage.split("::", 2);
    if (parts.length == 2) {
        String toEmail = parts[0].trim();
        String emailBody = parts[1].trim();

        System.out.println("Sending email to: " + toEmail);
        emailService.sendEmail(
                toEmail,
                "HireAI — Application Update",
                emailBody
        );
    }
}

    /* @KafkaListener(topics = "job-alerts", groupId = "notification-group")
    public void handleJobAlert(String message) {
        System.out.println("Job alert received: " + message);

        String[] parts = message.split("::", 2);
        if (parts.length == 2) {
            String toEmail = parts[0];
            String emailBody = parts[1];

            emailService.sendEmail(
                    toEmail,
                    "HireAI — Job Alert",
                    emailBody
            );
        }
    }*/
@KafkaListener(topics = "job-alerts", groupId = "notification-group")
public void handleJobAlert(String message) {
    System.out.println("Job alert received: " + message);

    // Quotes remove karo
    String cleanMessage = message.replace("\"", "");

    String[] parts = cleanMessage.split("::", 2);
    if (parts.length == 2) {
        String toEmail = parts[0].trim();
        String emailBody = parts[1].trim();

        System.out.println("Sending email to: " + toEmail);
        emailService.sendEmail(
                toEmail,
                "HireAI — Job Alert",
                emailBody
        );
    }
}

}