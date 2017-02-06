package uk.gov.dvsa.motr.notifications.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.web.eventlog.notify.NotifyConfirmationFailureEvent;
import uk.gov.dvsa.motr.web.eventlog.notify.NotifyConfirmationSuccessEvent;
import uk.gov.dvsa.motr.web.eventlog.notify.NotifyEvent;
import uk.gov.dvsa.motr.web.helper.DateDisplayHelper;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class NotifyService {

    private NotificationClient notificationClient;
    private String confirmationTemplateId;

    public NotifyService(NotificationClient notificationClient, String confirmationTemplateId) {

        this.notificationClient = notificationClient;
        this.confirmationTemplateId = confirmationTemplateId;
    }

    public SendEmailResponse sendConfirmationEmail(
            String emailAddress,
            String registrationNumber,
            LocalDate motExpiryDate,
            String unsubscribeLink
    ) throws NotificationClientException {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("registration_number", registrationNumber);
        personalisation.put("mot_expiry_date", DateDisplayHelper.asDisplayDate(motExpiryDate));
        personalisation.put("unsubscribe_link", unsubscribeLink);

        try {
            SendEmailResponse response = notificationClient.sendEmail(confirmationTemplateId, emailAddress, personalisation, "");
            NotifyEvent notifyEvent = new NotifyConfirmationSuccessEvent().setEmail(emailAddress).setVrm(registrationNumber);
            EventLogger.logEvent(notifyEvent);
            return response;
        } catch (NotificationClientException exception) {
            NotifyEvent notifyEvent = new NotifyConfirmationFailureEvent().setEmail(emailAddress).setVrm(registrationNumber);
            EventLogger.logErrorEvent(notifyEvent, exception);
            throw exception;
        }
    }
}
