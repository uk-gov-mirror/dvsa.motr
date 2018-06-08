package uk.gov.dvsa.motr.notifier.processing.model.notification.email;

import uk.gov.dvsa.motr.notifier.events.NotifyEvent;
import uk.gov.dvsa.motr.notifier.events.TwoWeekEmailReminderEvent;
import uk.gov.dvsa.motr.notifier.notify.DateFormatterForEmailDisplay;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;

public class MotTwoWeekEmailNotification extends SendableEmailNotification {

    public static final String MOT_EXPIRY_DATE_KEY = "mot_expiry_date";
    public static final String IS_DUE_OR_EXPIRES_KEY = "is_due_or_expires";

    public MotTwoWeekEmailNotification(String webBaseUrl) {
        super(webBaseUrl);
        setNotificationPathSubject("two-week-notification-email-subject.txt");
        setNotificationPathBody("two-week-notification-email-body.txt");
    }

    @Override
    public void personalise(SubscriptionQueueItem subscription, VehicleDetails vehicleDetails) {
        super.personalise(subscription, vehicleDetails);

        personalisation.put(MOT_EXPIRY_DATE_KEY, DateFormatterForEmailDisplay.asFormattedForEmailDate(vehicleDetails.getMotExpiryDate()));
        personalisation.put(IS_DUE_OR_EXPIRES_KEY, vehicleHadItsFirstMotTest(vehicleDetails) ? "expires" : "is due");
    }

    @Override
    public NotifyEvent getEvent() {
        return new TwoWeekEmailReminderEvent();
    }
}
