package uk.gov.dvsa.motr.notifier.processing.model.notification.sms;

import uk.gov.dvsa.motr.notifier.events.NotifyEvent;
import uk.gov.dvsa.motr.notifier.events.TwoWeekSmsReminderEvent;

public class MotTwoWeekSmsNotification extends SendableSmsNotification {

    public MotTwoWeekSmsNotification() {
        setNotificationPathBody("two-week-notification-sms.txt");
    }

    @Override
    public NotifyEvent getEvent() {
        return new TwoWeekSmsReminderEvent();
    }
}
