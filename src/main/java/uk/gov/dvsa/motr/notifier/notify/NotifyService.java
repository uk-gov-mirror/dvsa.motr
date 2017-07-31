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
    private String oneDayAfterNotificationTemplateId;

    public NotifyService(NotificationClient notificationClient, String oneMonthNotificationTemplateId, String
            twoWeekNotificationTemplateId, String oneDayAfterNotificationTemplateId) {

        this.notificationClient = notificationClient;
        this.oneMonthNotificationTemplateId = oneMonthNotificationTemplateId;
        this.twoWeekNotificationTemplateId = twoWeekNotificationTemplateId;
        this.oneDayAfterNotificationTemplateId = oneDayAfterNotificationTemplateId;
    }

    public SendEmailResponse sendOneMonthNotificationEmail(String emailAddress, String vehicleDetails, LocalDate motExpiryDate,
            String unsubscribeLink) throws NotificationClientException {

        return sendEmail(emailAddress, vehicleDetails, motExpiryDate, unsubscribeLink, this.oneMonthNotificationTemplateId);
    }

    public SendEmailResponse sendTwoWeekNotificationEmail(String emailAddress, String vehicleDetails, LocalDate motExpiryDate, String
            unsubscribeLink) throws NotificationClientException {

        return sendEmail(emailAddress, vehicleDetails, motExpiryDate, unsubscribeLink, this.twoWeekNotificationTemplateId);
    }

    public SendEmailResponse sendOneDayAfterNotificationEmail(String emailAddress, String vehicleDetails, LocalDate motExpiryDate,
            String unsubscribeLink) throws NotificationClientException {

        return sendEmail(emailAddress, vehicleDetails, motExpiryDate, unsubscribeLink, this.oneDayAfterNotificationTemplateId);
    }

    private SendEmailResponse sendEmail(String emailAddress, String vehicleDetails, LocalDate motExpiryDate, String unsubscribeLink,
            String emailTemplateId) throws NotificationClientException {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_details", vehicleDetails);
        personalisation.put("mot_expiry_date", DateFormatterForEmailDisplay.asFormattedForEmailDate(motExpiryDate));
        personalisation.put("unsubscribe_link", unsubscribeLink);

        return notificationClient.sendEmail(emailTemplateId, emailAddress, personalisation, "");
    }
}
