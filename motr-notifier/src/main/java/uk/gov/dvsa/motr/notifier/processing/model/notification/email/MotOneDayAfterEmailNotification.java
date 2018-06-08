package uk.gov.dvsa.motr.notifier.processing.model.notification.email;

import uk.gov.dvsa.motr.notifier.events.NotifyEvent;
import uk.gov.dvsa.motr.notifier.events.OneDayAfterEmailReminderEvent;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;

public class MotOneDayAfterEmailNotification extends SendableEmailNotification {

    public static final String WAS_DUE_OR_EXPIRED_KEY = "was_due_or_expired";

    public MotOneDayAfterEmailNotification(String webBaseUrl) {
        super(webBaseUrl);
        setNotificationPathSubject("one-day-after-notification-email-subject.txt");
        setNotificationPathBody("one-day-after-notification-email-body.txt");
    }

    @Override
    public void personalise(SubscriptionQueueItem subscription, VehicleDetails vehicleDetails) {

        super.personalise(subscription, vehicleDetails);

        personalisation.put(WAS_DUE_OR_EXPIRED_KEY, vehicleHadItsFirstMotTest(vehicleDetails) ? "expired" : "was due");
    }

    @Override
    public NotifyEvent getEvent() {
        return new OneDayAfterEmailReminderEvent();
    }

}
