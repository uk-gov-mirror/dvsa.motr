package uk.gov.dvsa.motr.notifier.processing.model.notification.sms;

import uk.gov.dvsa.motr.notifier.events.NotifyEvent;
import uk.gov.dvsa.motr.notifier.events.OneDayAfterSmsReminderEvent;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;

import java.util.HashMap;

public class MotOneDayAfterSmsNotification extends SendableSmsNotification {

    public MotOneDayAfterSmsNotification() {
        setNotificationPathBody("one-day-after-notification-sms.txt");
    }

    @Override
    public void personalise(SubscriptionQueueItem subscription) {
        personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", subscription.getVrm());
    }

    @Override
    public NotifyEvent getEvent() {
        return new OneDayAfterSmsReminderEvent();
    }
}
