package uk.gov.dvsa.motr.notifier.processing.model.notification.sms;

import uk.gov.dvsa.motr.notifier.events.NotifyEvent;
import uk.gov.dvsa.motr.notifier.events.OneMonthSmsReminderEvent;

public class MotOneMonthSmsNotification extends SendableSmsNotification {

    public MotOneMonthSmsNotification() {
        setNotificationPathBody("one-month-notification-sms.txt");
    }

    @Override
    public NotifyEvent getEvent() {
        return new OneMonthSmsReminderEvent();
    }
}
