package uk.gov.dvsa.motr.notifier.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifier.events.NotifyEvent;
import uk.gov.dvsa.motr.notifier.processing.model.notification.SendableNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.SendableEmailNotification;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineException;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineFailedEvent;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.util.Map;

public class NotifyEmailService {

    private NotificationClient notificationClient;

    private NotifyTemplateEngine notifyTemplateEngine;

    public NotifyEmailService(NotificationClient notificationClient, NotifyTemplateEngine notifyTemplateEngine) {
        this.notificationClient = notificationClient;
        this.notifyTemplateEngine = notifyTemplateEngine;
    }

    public void sendEmail(String emailAddress, SendableEmailNotification notification, VehicleDetails vehicleDetails)
        throws NotificationClientException {

        Logger logger = LoggerFactory.getLogger(NotifyEmailService.class);
        logger.info("enter sendEmail");

        Map<String, String> notifyParameters = getNotifyParameters(
                notification.getNotificationPathSubject(), notification.getNotificationPathBody(), notification.getPersonalisation());
        logger.info(notifyParameters.toString());

        SendEmailResponse response = notificationClient.sendEmail(notification.getTemplateId(), emailAddress,
                notifyParameters, "");
        if (response != null) {
            logger.info(response.toString());
        } else {
            logger.info("response is null");
        }

        logEvent(emailAddress, notification, vehicleDetails);
    }

    private void logEvent(String email, SendableNotification notification, VehicleDetails vehicleDetails) {
        NotifyEvent event = notification.getEvent()
                .setEmail(email)
                .setVrm(vehicleDetails.getRegNumber())
                .setExpiryDate(vehicleDetails.getMotExpiryDate());

        EventLogger.logEvent(event);
    }

    private Map<String, String> getNotifyParameters(String subject, String body, Map<String, String> parameters)
        throws NotificationClientException {

        try {
            return notifyTemplateEngine.getNotifyParameters(subject, body, parameters);
        } catch (NotifyTemplateEngineException exception) {
            EventLogger.logErrorEvent(
                    new NotifyTemplateEngineFailedEvent().setType(NotifyTemplateEngineFailedEvent.Type.ERROR_GETTING_PARAMETERS),
                    exception);
            // wrapping because nothing can be done about it
            throw new NotificationClientException(exception);
        }
    }
}
