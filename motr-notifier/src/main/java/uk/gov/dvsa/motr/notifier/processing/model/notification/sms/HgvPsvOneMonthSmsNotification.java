package uk.gov.dvsa.motr.notifier.processing.model.notification.sms;

import uk.gov.dvsa.motr.notifier.events.HgvPsvOneMonthSmsReminderEvent;
import uk.gov.dvsa.motr.notifier.events.NotifyEvent;

public class HgvPsvOneMonthSmsNotification extends SendableSmsNotification {

    public HgvPsvOneMonthSmsNotification() {
        setNotificationPathBody(HGV_PSV_DIRECTORY + "hgv-psv-notification-sms.txt");
    }

    @Override
    public NotifyEvent getEvent() {
        return new HgvPsvOneMonthSmsReminderEvent();
    }
}
