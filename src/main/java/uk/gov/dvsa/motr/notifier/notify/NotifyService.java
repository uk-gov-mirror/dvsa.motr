package uk.gov.dvsa.motr.notifier.notify;

import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class NotifyService {

    private NotificationClient notificationClient;
    private String oneMonthNotificationTemplateId;
    private String twoWeekNotificationTemplateId;

    public NotifyService(NotificationClient notificationClient, String oneMonthNotificationTemplateId, String
            twoWeekNotificationTemplateId) {

        this.notificationClient = notificationClient;
        this.oneMonthNotificationTemplateId = oneMonthNotificationTemplateId;
        this.twoWeekNotificationTemplateId = twoWeekNotificationTemplateId;
    }

    public SendEmailResponse sendOneMonthNotificationEmail(String emailAddress, String registrationNumber, LocalDate motExpiryDate,
            String unsubscribeLink) throws NotificationClientException {

        return sendEmail(emailAddress, registrationNumber, motExpiryDate, unsubscribeLink, this.oneMonthNotificationTemplateId);
    }

    public SendEmailResponse sendTwoWeekNotificationEmail(String emailAddress, String registrationNumber, LocalDate motExpiryDate, String
            unsubscribeLink) throws NotificationClientException {

        return sendEmail(emailAddress, registrationNumber, motExpiryDate, unsubscribeLink, this.twoWeekNotificationTemplateId);
    }

    private SendEmailResponse sendEmail(String emailAddress, String registrationNumber, LocalDate motExpiryDate, String unsubscribeLink,
            String emailTemplateId) throws NotificationClientException {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("registration_number", registrationNumber);
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(motExpiryDate));
        personalisation.put("unsubscribe_link", unsubscribeLink);

        return notificationClient.sendEmail(emailTemplateId, emailAddress, personalisation, "");
    }
}
