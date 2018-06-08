package uk.gov.dvsa.motr.notifier.notify;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.SendableSmsNotification;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineException;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineFailedEvent;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.Map;

public class NotifySmsService {

    private NotificationClient notificationClient;
    private NotifyTemplateEngine notifyTemplateEngine;

    public NotifySmsService(NotificationClient notificationClient, NotifyTemplateEngine notifyTemplateEngine) {

        this.notificationClient = notificationClient;
        this.notifyTemplateEngine = notifyTemplateEngine;
    }

    public void sendSms(String phoneNumber, SendableSmsNotification notification)
            throws NotificationClientException {

        Map<String, String> notifyParameters = getNotifyParameters(
                notification.getNotificationPathBody(), notification.getPersonalisation());

        notificationClient.sendSms(notification.getTemplateId(), phoneNumber, notifyParameters, "");
    }

    private Map<String, String> getNotifyParameters(String body, Map<String, String> parameters) throws NotificationClientException {
        try {
            return notifyTemplateEngine.getNotifyParameters(body, parameters);
        } catch (NotifyTemplateEngineException exception) {
            EventLogger.logErrorEvent(
                    new NotifyTemplateEngineFailedEvent().setType(NotifyTemplateEngineFailedEvent.Type.ERROR_GETTING_PARAMETERS),
                    exception);
            // wrapping because nothing can be done about it
            throw new NotificationClientException(exception);
        }
    }
}
