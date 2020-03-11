package uk.gov.dvsa.motr.notifier.processing.model.notification.sms;

import uk.gov.dvsa.motr.notifier.events.NotifyEvent;
import uk.gov.dvsa.motr.notifier.events.OneDayAfterSmsReminderEvent;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;

import java.util.HashMap;

public class MotOneDayAfterSmsNotification extends SendableSmsNotification {

    public MotOneDayAfterSmsNotification() {
        setNotificationPathBody("one-day-after-notification-sms.txt");
    }

    @Override
    public void personalise(VehicleDetails vehicleDetails) {
        personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", vehicleDetails.getRegNumber());
    }

    @Override
    public NotifyEvent getEvent() {
        return new OneDayAfterSmsReminderEvent();
    }
}
