package uk.gov.dvsa.motr.service;

import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class NotifyService {

    private NotificationClient notificationClient;

    public NotifyService(NotificationClient notificationClient) {

        this.notificationClient = notificationClient;
    }

    public SendEmailResponse sendEmail(String emailAddress, String registrationNumber, LocalDate motExpiryDate,
            String emailTemplateId) throws NotificationClientException {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_details", registrationNumber);
        personalisation.put("mot_expiry_date", motExpiryDate.toString());
        personalisation.put("unsubscribe_link", "integration/test");

        return notificationClient.sendEmail(emailTemplateId, emailAddress, personalisation, "");
    }
}
