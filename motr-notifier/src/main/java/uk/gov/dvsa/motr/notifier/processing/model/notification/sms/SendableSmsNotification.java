package uk.gov.dvsa.motr.notifier.processing.model.notification.sms;

import uk.gov.dvsa.motr.notifier.notify.DateFormatterForSmsDisplay;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.model.notification.SendableNotification;

import java.util.HashMap;

public abstract class SendableSmsNotification extends SendableNotification {

    public void personalise(SubscriptionQueueItem subscription) {
        personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", subscription.getVrm());
        personalisation.put("mot_expiry_date", DateFormatterForSmsDisplay.asFormattedForSmsDate(subscription.getMotDueDate()));
    }

    @Override
    public SendableSmsNotification setNotificationPathBody(String path) {
        notificationPathBody = path;
        return this;
    }

    @Override
    public SendableSmsNotification setTemplateId(String templateId) {
        super.setTemplateId(templateId);
        return this;
    }
}
