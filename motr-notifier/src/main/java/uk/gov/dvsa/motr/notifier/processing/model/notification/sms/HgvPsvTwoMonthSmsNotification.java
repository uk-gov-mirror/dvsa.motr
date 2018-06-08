package uk.gov.dvsa.motr.notifier.processing.model.notification.sms;

import uk.gov.dvsa.motr.notifier.events.HgvPsvTwoMonthSmsReminderEvent;
import uk.gov.dvsa.motr.notifier.events.NotifyEvent;

public class HgvPsvTwoMonthSmsNotification extends SendableSmsNotification {

    public HgvPsvTwoMonthSmsNotification() {
        setNotificationPathBody(HGV_PSV_DIRECTORY + "hgv-psv-notification-sms.txt");
    }

    @Override
    public NotifyEvent getEvent() {
        return new HgvPsvTwoMonthSmsReminderEvent();
    }
}
